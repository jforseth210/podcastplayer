/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.example

import jaco.mp3.player.MP3Player

var player = MP3Player()
var shows: MutableList<Show> = mutableListOf()
var selectedShow: Show? = null


fun main() {
    shows= readShows()
    val playerUI = PodcastPlayerUI("Podcast Player")
    playerUI.isVisible = true
}





