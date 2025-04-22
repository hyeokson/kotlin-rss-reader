package util

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

fun rssReader(url: String): Document {
    val factory = DocumentBuilderFactory.newInstance()
    return factory.newDocumentBuilder()
        .parse(url)
}
