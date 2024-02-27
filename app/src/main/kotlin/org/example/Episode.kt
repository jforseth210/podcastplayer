/**
 * @author Justin Forseth
 */
package org.example

import java.net.URL

/**
 * A simple class to represent an episode of a podcast.
 * Includes the title and the url to download the audio file
 */
class Episode(private val title: String, val url: URL) {
  /**
   * Overridden toString to show only title in jlists
   */
  override fun toString(): String {
    return title
  }
}