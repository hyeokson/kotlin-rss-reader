package service

import model.Post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import javax.xml.parsers.DocumentBuilderFactory

class RssReaderServiceTest {
    @Test
    @DisplayName("rss 읽어와 포스트를 생성한다.")
    fun createPost() {
        // given
        val xml =
            """
            <item>
                <title>test</title>
                <description>desc</description>
                <link>link</link>
                <pubDate>Tue, 27 Aug 2024 01:01:45 GMT</pubDate>
            </item>
            """.trimIndent()
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputStream = ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8))
        val document = builder.parse(inputStream)

        // when
        val testPosts = RssReaderService().createPost(listOf(document))

        // then
        assertEquals("test", testPosts.get(0).title)
        assertEquals("desc", testPosts.get(0).description)
        assertEquals("link", testPosts.get(0).link)
        assertEquals(
            LocalDateTime.of(2024, 8, 27, 10, 1, 45),
            testPosts.get(0).pubDate,
        )
    }

    @Test
    @DisplayName("rss update 테스트")
    fun pooRssUpdate() {
        // given
        val oldPosts =
            mutableListOf(
                Post("old", "old desc", "https://old.com", LocalDateTime.now()),
            )
        val reSearchPosts =
            mutableListOf(
                Post("old", "old desc", "https://old.com", LocalDateTime.now()),
                Post("new", "new desc", "https://new.com", LocalDateTime.now()),
            )

        // when
        val postTitles =
            RssReaderService().pollRssUpdates(reSearchPosts, oldPosts)
                .map { it.title }

        // then
        assertEquals(true, postTitles.contains("new"))
    }
}
