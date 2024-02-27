package org.example

import jaco.mp3.player.MP3Player
import java.net.URL
import javax.swing.JOptionPane



/**
 * Entry point for the application
 */
fun main() {    // Check connection
    try {
        URL("https://example.com").readText()
    } catch (e:Exception){
        JOptionPane.showMessageDialog(null, "This program requires a functional internet connection")
        return
    }
    // Display the UI
    val playerUI = PodcastPlayerUI("Podcast Player", PodcastService())
    playerUI.isVisible = true
}






