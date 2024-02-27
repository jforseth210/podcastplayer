/**
 * Entry point for the application
 * @author Justin Forseth
 */
package org.example

import java.net.URL
import javax.swing.JOptionPane


/**
 * Checks connectivity, creates the UI and displays it
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






