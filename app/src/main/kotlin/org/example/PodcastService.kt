package org.example

import java.io.File


class PodcastService {
  var shows: MutableList<Show> = readShows()
  var selectedShow: Show? = null
  fun addOPML(file: File) {
      val parsedShows = parseOPML(file)
      for (parsedShow in parsedShows) {
       addShow(parsedShow)
      }
    }
  fun selectShow(show: Show){
    selectedShow = show
  }
  fun selectShow(index: Int){
    selectedShow = shows[index]
  }
  fun hasShow(show: Show): Boolean{
    return shows.contains(show)
  }
  fun addShow(show: Show){
    if (hasShow(show)) return
    shows.add(show)
  }
  fun playShow(){

  }
}
