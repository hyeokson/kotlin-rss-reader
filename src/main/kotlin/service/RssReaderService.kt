package service

import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import model.Post
import org.w3c.dom.Document
import org.w3c.dom.Element
import util.rssReader
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class RssReaderService() {
    suspend fun getXml(): List<Document> {
        val kurly = "https://helloworld.kurly.com/feed.xml"
        val hmg = "https://developers.hyundaimotorgroup.com/blog/rss"
        val banksalad = "https://blog.banksalad.com/rss.xml"

        var documents: List<Document> = emptyList()

        supervisorScope {
            val kurlyXml =
                async {
                    rssReader(kurly)
                }
            val hmgXml =
                async {
                    rssReader(hmg)
                }
            val banksaladXml =
                async {
                    rssReader(banksalad)
                }

            documents = listOf(kurlyXml.await(), hmgXml.await(), banksaladXml.await())
        }

        return documents
    }

    fun pollRssUpdates(
        reSearchPosts: MutableList<Post>,
        posts: MutableList<Post>,
    ): MutableList<Post> {
        val oldPostsLinks = posts.map { it.link }
        val newPosts = reSearchPosts.filter { reSearchPost -> !oldPostsLinks.contains(reSearchPost.link) }

        if (newPosts.isNotEmpty()) {
            println("새로운 글이 등록되었습니다!")

            newPosts.forEach {
                println("[NEW] ${it.title} (${it.pubDate.toLocalDate()}) - ${it.link}")
            }
            posts.addAll(newPosts)
        }
        return posts
    }

    fun createPost(xmls: List<Document>): MutableList<Post> {
        return xmls.map {
            convertXmlToPost(it)
        }
            .flatten()
            .toMutableList()
    }

    fun sort(
        keyword: String?,
        posts: List<Post>,
    ): List<Post> {
        if (keyword.isNullOrBlank()) {
            return posts.sortedByDescending { it.pubDate }
                .take(10)
        }
        return posts.filter { it.title.contains(keyword, ignoreCase = false) }
            .sortedByDescending { it.pubDate }
            .take(10)
    }
}

private fun convertXmlToPost(document: Document): MutableList<Post> {
    val list = mutableListOf<Post>()
    val items = document.getElementsByTagName("item")
    for (i in 0..items.length - 1) {
        val item = items.item(i)
        val element = item as Element
        val title = element.getElementsByTagName("title").item(0)
        val description = element.getElementsByTagName("description").item(0)
        val link = element.getElementsByTagName("link").item(0)
        val pubDate = element.getElementsByTagName("pubDate").item(0)
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        val parsed = formatter.parse(pubDate.textContent)
        val localDateTime = ZonedDateTime.ofInstant(parsed.toInstant(), ZoneId.systemDefault()).toLocalDateTime()

        val post = Post(title.textContent, description.textContent, link.textContent, localDateTime)
        list.add(post)
    }
    return list
}
