import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.Post
import service.RssReaderService
import view.InputView
import view.OutputView
import kotlin.time.Duration.Companion.minutes

var posts = mutableListOf<Post>()

fun main() {
    val rssReaderService = RssReaderService()

    runBlocking {
        launch {
            val xmls = rssReaderService.getXml()
            posts = rssReaderService.createPost(xmls)
            while (isActive) {
                val keyword = InputView.inputKeyword()
                val sortedPosts = rssReaderService.sort(keyword, posts)
                OutputView.result(sortedPosts)
            }
        }
        launch {
            while (isActive) {
                delay(10.minutes)
                val xmls = rssReaderService.getXml()
                val reSearchPosts = rssReaderService.createPost(xmls)
                rssReaderService.pollRssUpdates(reSearchPosts, posts)
            }
        }
    }
}
