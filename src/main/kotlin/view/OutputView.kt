package view

import model.Post

object OutputView {
    fun result(posts: List<Post>) {
        posts.forEachIndexed { index, post ->
            println("[${index + 1}] ${post.title} (${post.pubDate.toLocalDate()}) - ${post.link}")
        }
        println()
    }
}
