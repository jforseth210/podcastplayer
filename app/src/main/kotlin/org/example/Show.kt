package org.example

import org.apache.commons.lang3.StringEscapeUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.awt.Image
import java.net.URL
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class Show(val title: String, val url: String) {
  private var showDataLoaded = false
  private var showDataLoading = false
  var description: String? = null
    get() {
      loadShowData()
      return field
    }
  var image: Image? = null
    get() {
      loadShowData()
      return field
    }
  var episodes: MutableList<Episode> = mutableListOf()
    get() {
      loadShowData()
      return field
    }

  override fun toString(): String {
    return title
  }

  override fun equals(other: Any?): Boolean {
    return other is Show && url == other.url
  }
  fun loadShowData() {
    if (showDataLoaded) return
    if (showDataLoading) return
    showDataLoading = true
    val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val docBuilder: DocumentBuilder = builderFactory.newDocumentBuilder()
    val doc: Document = docBuilder.parse(this.url)
    doc.documentElement.normalize()
    this.description =
      doc.getElementsByTagName("description").item(0).textContent.replace("<br />", "\n")
    loadShowImage(doc)
    loadEpisodes(doc)
    showDataLoading = false
    showDataLoaded = true
  }
  private fun loadShowImage(doc: Document) {
    if (doc.getElementsByTagName("image").item(0) == null) {
      return
    }
    val imageURLString =
      (doc.getElementsByTagName("image").item(0) as Element)
        .getElementsByTagName("url")
        .item(0)
        .textContent
    this.image = ImageIO.read(URL(imageURLString)).getScaledInstance(64, 64, Image.SCALE_SMOOTH)
  }
  private fun loadEpisodes(doc: Document) {
    val episodeNodes = doc.getElementsByTagName("item")
    for (i in 0 until episodeNodes.length) {
      val episode = episodeNodes.item(i)
      if (episode.nodeType == Node.ELEMENT_NODE) {
        this.episodes.add(
          Episode(
            (episode as Element)
              .getElementsByTagName("title")
              .item(0)
              .textContent,
            (episode.getElementsByTagName("enclosure").item(0) as Element)
              .getAttribute("url")
          )
        )
      }
    }
  }
  fun toCSV(): String {
    val escapedTitle = StringEscapeUtils.escapeCsv(title)
    val escapedURL = StringEscapeUtils.escapeCsv(url)
    return "$escapedTitle,$escapedURL"
  }

  override fun hashCode(): Int {
    var result = title.hashCode()
    result = 31 * result + url.hashCode()
    return result
  }
}
