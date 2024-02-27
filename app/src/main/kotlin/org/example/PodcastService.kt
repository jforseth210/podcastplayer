/**
 * @author Justin Forseth
 */
package org.example

import java.io.File

/**
 * This class handles making modifications to the show list
 */
class PodcastService {
  var shows: MutableList<Show> = readShows()
  var selectedShow: Show? = null

  /**
   * Add shows from an OPML file
   */
  fun addOPML(file: File) {
      val parsedShows = parseOPML(file)
      for (parsedShow in parsedShows) {
       addShow(parsedShow)
      }
    }

  /**
   * Update the selected show
   */
  fun selectShow(show: Show){
    selectedShow = show
  }

  /**
   * Update the selected show by index
   */
  fun selectShow(index: Int){
    selectedShow = shows[index]
  }

  /**
   * Check whether show exists
   */
  fun hasShow(show: Show): Boolean{
    return shows.contains(show)
  }

  /**
   * Add an episode if it doesn't exist
   */
  fun addShow(show: Show){
    if (hasShow(show)) return
    shows.add(show)
    writeShows(shows)
  }
}
