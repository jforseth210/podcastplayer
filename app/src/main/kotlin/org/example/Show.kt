/**
 * @author Justin Forseth
 */
package org.example

import org.apache.commons.lang3.StringEscapeUtils
import org.w3c.dom.Document
import java.awt.Image
import java.net.URL

/**
 * A class to represent a podcast, with the title, description, show image, episodes,
 * and the url of the rss feed.
 */
class Show(val title: String, val url: URL) {
  private val doc: Document? by lazy { loadShowDoc() }
  // The published description of the podcast
  val description: String by lazy {loadDescription()}

  // The cover art for the podcast
  val image: Image?  by lazy { loadShowImage() }
  // A list of podcast episodes
  val episodes: MutableList<Episode>? by lazy{loadEpisodes()}

  /**
   * Download the show RSS feed
   */
  private fun loadShowDoc(): Document? {
    return loadDocFromURL(url)
  }

  /**
   * Parse the show description from the RSS feed
   */
  private fun loadDescription(): String {
    if (this.doc != null) {
      return loadDescriptionFromDocument(this.doc!!)
    }
    return ""
  }

  /**
   * Load and scale the show image from the RSS feed
   */
  private fun loadShowImage(): Image? {
    if (this.doc != null) {
      return loadShowImageFromDocument(this.doc!!)
    }
    return null
  }

  /**
   * Parse show episodes
   */
  private fun loadEpisodes(): MutableList<Episode>? {
    if (this.doc != null) {
    return loadEpisodesFromDocument(this.doc!!)
    }
    return null
  }

  /**
   * Attempt to load the show to see if it's valid XML
   */
  fun isValid(): Boolean{
    return this.doc != null
  }
  /**
   * Convert the show to an escaped csv row with the title and url
   */
  fun toCSV(): String {
    val escapedTitle = StringEscapeUtils.escapeCsv(title)
    val escapedURL = StringEscapeUtils.escapeCsv(url.toString())
    return "$escapedTitle,$escapedURL"
  }

  /**
   * Overridden toString() to show only the title in jlists
   */
  override fun toString(): String {
    return title
  }

  /**
   * Overridden equals, Two shows should be the same if they have the same url.
   */
  override fun equals(other: Any?): Boolean {
    return other is Show && url.toString() == other.url.toString()
  }

  /**
   * Override hashCode to match equals
   */
  override fun hashCode(): Int {
    return url.toString().hashCode()
  }
}
