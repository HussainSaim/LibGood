package com.libgood.app.models

// An enum class is prefered because the data of the achievements never changes
enum class AchievementType(
    val key: String,
    val title: String,
    val description: String
) {
    FIRST_SESSION("first_session", "First Steps", "Log your very first reading session"),
    PAGES_100("pages_100", "Centurion", "Read 100 pages total"),
    PAGES_500("pages_500", "Page Turner", "Read 500 pages total"),
    STREAK_3("streak_3", "On a Roll", "Maintain a 3-day reading streak"),
    STREAK_7("streak_7", "Bookworm", "Maintain a 7-day reading streak"),
    LEVEL_5("level_5", "Rising Reader", "Reach level 5"),
    BOOK_FINISHED("book_finished", "The End", "Finish a book");


    // Function that uses polymorphism to correctly identify whether an acheievement has been completed
    fun check(totalPagesRead: Int, streak: Int, level: Int, sessionsLogged: Int, anyBookFinished: Boolean): Boolean {
        return when (this) {
            FIRST_SESSION -> sessionsLogged >= 1
            PAGES_100 -> totalPagesRead >= 100
            PAGES_500 -> totalPagesRead >= 500
            STREAK_3 -> streak >= 3
            STREAK_7 -> streak >= 7
            LEVEL_5 -> level >= 5
            BOOK_FINISHED -> anyBookFinished
        }
    }
}
