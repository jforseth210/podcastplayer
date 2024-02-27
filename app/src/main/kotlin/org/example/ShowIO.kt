package org.example

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.StringEscapeUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXParseException
import java.awt.Image
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.JOptionPane
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Save a list of the users current shows and their urls to a csv file
 */
fun writeShows(shows: List<Show>) {
  val file = File("src/main/resources/data/shows.csv")
  if (!file.exists()) {
    file.createNewFile()
  }

  val writer = BufferedWriter(FileWriter(file))
  writer.write("")
  for (show in shows) {
    writer.appendLine(show.toCSV())
  }
  writer.close()
}

/**
 * Load the user's shows from a csv file
 */
fun readShows(): MutableList<Show> {
  val file = File("src/main/resources/data/shows.csv")
  if(!file.exists()){
    return mutableListOf()
  }
  val fileReader = FileReader(file)
  val csvParser = CSVParser(fileReader, CSVFormat.DEFAULT)

  val shows = mutableListOf<Show>()

  for (csvRecord in csvParser) {
    val escapedTitle = csvRecord.get(0)
    val escapedUrl = csvRecord.get(1)

    val title = StringEscapeUtils.unescapeCsv(escapedTitle)
    val url = StringEscapeUtils.unescapeCsv(escapedUrl)

    shows.add(Show(title, URL(url)))
  }
  return shows
}

/**
 * Parse a list of shows from an OPML export
 */
fun parseOPML(file: File): MutableList<Show> {
  val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
  val docBuilder: DocumentBuilder = builderFactory.newDocumentBuilder()
  val doc: Document?
  try {
    doc = docBuilder.parse(file)
  } catch (e: SAXParseException) {
    JOptionPane.showMessageDialog(null, "Invalid OPML file, please try again")
    return mutableListOf()
  } catch (e: FileNotFoundException) {
    JOptionPane.showMessageDialog(
      null,
      "Couldn't find OPML file, has it been moved or deleted?"
    )
    return mutableListOf()
  }
  if (doc == null) {
    return mutableListOf()
  }

  doc.documentElement.normalize()
  val shows: NodeList = doc.getElementsByTagName("outline")
  val parsedShows: MutableList<Show> = mutableListOf()
  for (i in 0 until shows.length) {
    val show: Node = shows.item(i)
    if (show.nodeType != Node.ELEMENT_NODE) {
      continue
    }
    try{
      parsedShows.add(Show((show as Element).getAttribute("text"), URL(show.getAttribute("xmlUrl"))))
    } catch(e:MalformedURLException){
      JOptionPane.showMessageDialog(null,"Invalid URL ${(show as Element).getAttribute("xmlUrl")}, skipping show")
    }
  }
  return parsedShows
}
 /**
   * Download the show RSS feed
   */
   fun loadDocFromURL(url: URL): Document? {
    try {
      val builderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
      val docBuilder: DocumentBuilder = builderFactory.newDocumentBuilder()
      val doc = docBuilder.parse(url.toString())
      doc.documentElement.normalize()
      return doc
    } catch (e: FileNotFoundException){
      return null
    } catch (e: SAXParseException){
      return null
    }
  }

  /**
   * Parse the show description from the RSS feed
   */
   fun loadDescriptionFromDocument(doc:Document): String {
    return  doc.getElementsByTagName("description")?.item(0)?.textContent?.replace("<br />", "\n") ?: ""
  }

  /**
   * Load and scale the show image from the RSS feed
   */
   fun loadShowImageFromDocument(doc:Document): Image? {
    val imageNode = doc.getElementsByTagName("image").item(0) ?:  doc.getElementsByTagName("itunes:image").item(0)?: return null;
    if (imageNode.nodeType != Node.ELEMENT_NODE) return null

    val imageElement = imageNode as Element

    val imageURLString = imageElement.getElementsByTagName("url")?.item(0)?.textContent ?: imageElement.getAttribute("href")

    return ImageIO.read(URL(imageURLString))?.getScaledInstance(64, 64, Image.SCALE_SMOOTH) ?: ImageIO.read(URL("https://picsum.photos/64"))
   }

  /**
   * Parse show episodes
   */
   fun loadEpisodesFromDocument(doc: Document): MutableList<Episode> {
    val episodeNodes = doc.getElementsByTagName("item") ?: return mutableListOf()
    val episodes = mutableListOf<Episode>()
    for (i in 0 until episodeNodes.length) {
      val episode = episodeNodes.item(i)
      if (episode.nodeType != Node.ELEMENT_NODE) continue
      val title = (episode as Element).getElementsByTagName("title").item(0)
      if (title == null || title.nodeType != Node.ELEMENT_NODE) continue
      val enclosure = episode.getElementsByTagName("enclosure").item(0)
      if (enclosure == null || enclosure.nodeType != Node.ELEMENT_NODE) continue
      val enclosureURL:URL
      try{
        enclosureURL = URL((enclosure as Element).getAttribute("url"))
      } catch (e:MalformedURLException){
        JOptionPane.showMessageDialog(null, "Episode: ${title.textContent} has an invalid url")
        continue
      }

      episodes.add(Episode(
        title.textContent,
          enclosureURL
        )
      )
    }
    return episodes
  }