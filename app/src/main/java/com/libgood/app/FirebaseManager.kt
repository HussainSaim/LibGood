package com.libgood.app

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.libgood.app.models.Achievement
import com.libgood.app.models.AchievementType
import com.libgood.app.models.Book
import com.libgood.app.models.ReadingSession
import com.libgood.app.models.User
import java.util.Calendar
import java.util.Date

// This singular file is responsible for handling all interactions with Firebase
object FirebaseManager {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    private const val USERS = "users"
    private const val BOOKS = "userBooks"
    private const val SESSIONS = "readingSessions"
    private const val ACHIEVEMENTS = "achievements"

    const val XP_PER_PAGE = 2


    // Authentication
    val currentUserId: String? get() = auth.currentUser?.uid
    val isLoggedIn: Boolean get() = currentUserId != null

    fun register(
        email: String,
        password: String,
        username: String,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: run {
                    onResult(false, "Account created but no user id returned")
                    return@addOnSuccessListener
                }
                val user = User(
                    uid = uid,
                    email = email,
                    username = username.ifBlank { email.substringBefore("@") },
                    xp = 0,
                    streak = 0,
                    lastReadDate = null,
                    createdAt = Timestamp.now()
                )
                db.collection(USERS).document(uid).set(user)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
            }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun login(email: String, password: String, onResult: (success: Boolean, error: String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun logout() = auth.signOut()

    // Users

    //  Gets tbe user data from firestore and turns it into an object
    fun getCurrentUser(onResult: (User?) -> Unit) {
        val uid = currentUserId ?: return onResult(null)
        db.collection(USERS).document(uid).get()
            .addOnSuccessListener { snap -> onResult(snap.toObject(User::class.java)) }
            .addOnFailureListener { onResult(null) }
    }

    // Books

    // Create a new doucment id and book object
    fun addBook(title: String, author: String, totalPages: Int, onResult: (success: Boolean, error: String?) -> Unit) {
        val uid = currentUserId ?: return onResult(false, "Not logged in")
        val docRef = db.collection(BOOKS).document()
        val book = Book(
            id = docRef.id,
            userId = uid,
            title = title,
            author = author,
            totalPages = totalPages,
            currentPage = 0,
            addedAt = Timestamp.now()
        )
        docRef.set(book)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun getUserBooks(onResult: (List<Book>) -> Unit) {
        val uid = currentUserId ?: return onResult(emptyList())
        db.collection(BOOKS)
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.documents.mapNotNull { it.toObject(Book::class.java) })
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Core App Functionality

    // Logs the reading session and completes all the main functionalities of the application
    fun logReadingSession(
        book: Book,
        pagesRead: Int,
        onResult: (updatedUser: User?, newAchievements: List<AchievementType>, error: String?) -> Unit
    ) {
        val uid = currentUserId ?: return onResult(null, emptyList(), "Not logged in")
        if (pagesRead <= 0) return onResult(null, emptyList(), "Pages must be greater than 0")

        val xpEarned = pagesRead * XP_PER_PAGE
        val sessionRef = db.collection(SESSIONS).document()
        val session = ReadingSession(
            id = sessionRef.id,
            userId = uid,
            bookId = book.id,
            bookTitle = book.title,
            pagesRead = pagesRead,
            xpEarned = xpEarned,
            date = Timestamp.now()
        )

        // Save the session
        sessionRef.set(session)
            .addOnSuccessListener {

                // Advance book progress
                val newCurrentPage = (book.currentPage + pagesRead).coerceAtMost(book.totalPages.coerceAtLeast(pagesRead))
                db.collection(BOOKS).document(book.id)
                    .update("currentPage", newCurrentPage)
                    .addOnSuccessListener {

                        // Update XP and streak on the user document
                        getCurrentUser { user ->
                            if (user == null) {
                                onResult(null, emptyList(), "Could not load user")
                                return@getCurrentUser
                            }
                            val newXp = user.xp + xpEarned
                            val newStreak = nextStreak(user.streak, user.lastReadDate?.toDate(), Date())

                            val updates = mapOf(
                                "xp" to newXp,
                                "streak" to newStreak,
                                "lastReadDate" to Timestamp.now()
                            )
                            db.collection(USERS).document(uid).update(updates)
                                .addOnSuccessListener {
                                    val updatedUser = user.copy(
                                        xp = newXp,
                                        streak = newStreak,
                                        lastReadDate = Timestamp.now()
                                    )

                                    // Check achievements
                                    checkAchievements(updatedUser, newCurrentPage >= book.totalPages) { newOnes ->
                                        onResult(updatedUser, newOnes, null)
                                    }
                                }
                                .addOnFailureListener { e -> onResult(null, emptyList(), e.localizedMessage) }
                        }
                    }
                    .addOnFailureListener { e -> onResult(null, emptyList(), e.localizedMessage) }
            }
            .addOnFailureListener { e -> onResult(null, emptyList(), e.localizedMessage) }
    }

    /**
     * Returns the streak value to write after a session is logged today.
     *
     * Rules:
     *   - No previous read date          -> 1 (first session ever)
     *   - Last read was earlier today    -> keep the existing streak (at least 1)
     *   - Last read was yesterday        -> existing streak + 1
     *   - Last read was longer ago       -> 1 (broken, restart)
     */

    // Function to determine the next value of the users streak
    private fun nextStreak(existingStreak: Int, lastRead: Date?, now: Date): Int {
        if (lastRead == null) return 1

        val last = Calendar.getInstance().apply { time = lastRead }
        val today = Calendar.getInstance().apply { time = now }

        // Was the last reading session logged today
        val sameDay = last.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                last.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        if (sameDay) return existingStreak.coerceAtLeast(1)

        // Was the last reading session exactly one day before today
        last.add(Calendar.DAY_OF_YEAR, 1)
        val wasYesterday = last.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                last.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        return if (wasYesterday) existingStreak + 1 else 1
    }

    // Achievements

    /**
     * Loads totals needed to evaluate achievements, then checks each AchievementType.
     * Any newly-unlocked achievements are written to Firestore and returned via [onResult].
     */

    // Ran after each session to check for any achievements unlocked
    private fun checkAchievements(
        user: User,
        bookJustFinished: Boolean,
        onResult: (List<AchievementType>) -> Unit
    ) {
        val uid = user.uid
        // Get already unlocked types so we don't duplicate
        db.collection(ACHIEVEMENTS)
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { existingSnap ->
                val existing = existingSnap.documents.mapNotNull { it.getString("type") }.toSet()

                // Pull totals from sessions
                db.collection(SESSIONS)
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener { sessionSnap ->
                        val sessions = sessionSnap.documents.mapNotNull { it.toObject(ReadingSession::class.java) }
                        val totalPages = sessions.sumOf { it.pagesRead }
                        val sessionCount = sessions.size

                        val newlyUnlocked = mutableListOf<AchievementType>()
                        for (type in AchievementType.values()) {
                            if (existing.contains(type.key)) continue
                            val unlocked = type.check(
                                totalPagesRead = totalPages,
                                streak = user.streak,
                                level = user.level,
                                sessionsLogged = sessionCount,
                                anyBookFinished = bookJustFinished
                            )
                            if (unlocked) newlyUnlocked.add(type)
                        }

                        if (newlyUnlocked.isEmpty()) {
                            onResult(emptyList())
                            return@addOnSuccessListener
                        }

                        // Write each new achievement
                        val batch = db.batch()
                        for (type in newlyUnlocked) {
                            val ref = db.collection(ACHIEVEMENTS).document()
                            val ach = Achievement(
                                id = ref.id,
                                userId = uid,
                                type = type.key,
                                earnedAt = Timestamp.now()
                            )
                            batch.set(ref, ach)
                        }
                        batch.commit()
                            .addOnSuccessListener { onResult(newlyUnlocked) }
                            .addOnFailureListener { onResult(emptyList()) }
                    }
                    .addOnFailureListener { onResult(emptyList()) }
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Used to display on the achievements screen
    fun getUserAchievements(onResult: (List<Achievement>) -> Unit) {
        val uid = currentUserId ?: return onResult(emptyList())
        db.collection(ACHIEVEMENTS)
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snap ->
                val achievements = snap.documents.mapNotNull { it.toObject(Achievement::class.java) }
                // Sort client-side to avoid needing a Firestore composite index
                onResult(achievements.sortedByDescending { it.earnedAt?.seconds ?: 0 })
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Leaderboard


    // Fetches all users XP in descending order
    fun getLeaderboard(onResult: (List<User>) -> Unit) {
        db.collection(USERS)
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snap ->
                onResult(snap.documents.mapNotNull { it.toObject(User::class.java) })
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
