package org.example

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.StringEscapeUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXParseException
import java.io.*
import javax.swing.JOptionPane
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

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

fun readShows(): MutableList<Show> {
  val file = File("src/main/resources/data/shows.csv")
  val fileReader = FileReader(file)
  val csvParser = CSVParser(fileReader, CSVFormat.DEFAULT)

  val shows = mutableListOf<Show>()

  for (csvRecord in csvParser) {
    val escapedTitle = csvRecord.get(0)
    val escapedUrl = csvRecord.get(1)

    val title = StringEscapeUtils.unescapeCsv(escapedTitle)
    val url = StringEscapeUtils.unescapeCsv(escapedUrl)

    shows.add(Show(title, url))
  }
  return shows
}

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

    parsedShows.add(Show((show as Element).getAttribute("text"), show.getAttribute("xmlUrl")))
  }
  return parsedShows
}