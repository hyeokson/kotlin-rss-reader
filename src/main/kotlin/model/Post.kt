package model

import java.time.LocalDateTime

data class Post(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: LocalDateTime,
)
