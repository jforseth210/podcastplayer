package org.example

import jaco.mp3.player.MP3Player
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.net.MalformedURLException
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*

class PodcastPlayerUI(title: String, private val podcastService: PodcastService) : JFrame() {
  private val showList = JList<Show>()
  private val showViewPanel = JPanel(BorderLayout())
  private var player = MP3Player()

  init {
    createUI(title)
  }

  private fun createUI(title: String) {
    // Some basic setup
    setTitle(title)
    defaultCloseOperation = EXIT_ON_CLOSE
    setSize(400, 300)
    setLocationRelativeTo(null)

    // Panel containing all other UI elements
    val masterPanel = JPanel(BorderLayout())
    this.contentPane.add(masterPanel)

    val showPanel = JPanel(BorderLayout())
    masterPanel.add(showPanel, BorderLayout.WEST)
    drawShows()
    this.showList.addListSelectionListener {
      if(showList.selectedIndex <0) return@addListSelectionListener
      podcastService.selectShow(showList.selectedIndex)
      drawShowInfo()
    }
    showPanel.add(JScrollPane(showList), BorderLayout.CENTER)
    val showButtonPanel = JPanel(GridLayout(1, 2))
    val importOPMLButton = JButton("Import OPML")
    importOPMLButton.addActionListener {
      val file = pickFile(this) ?: return@addActionListener
      podcastService.addOPML(file)
        drawShows()
    }
    showButtonPanel.add(importOPMLButton)
    drawShowInfo()
    val addShowButton = JButton("Add Show")
    showButtonPanel.add(addShowButton)
    addShowButton.addActionListener {
      addShow()
      drawShows()

    }
    showPanel.add(showButtonPanel, BorderLayout.SOUTH)
    masterPanel.add(showViewPanel, BorderLayout.CENTER)
  }

  private fun addShow() {
    val titleInput = JOptionPane.showInputDialog("Enter podcast name:")
    val urlInput = JOptionPane.showInputDialog("Enter podcast rss feed:")

    val show: Show
    try {
      show = Show(titleInput, URL(urlInput))
    } catch (e:MalformedURLException){
      JOptionPane.showMessageDialog(null,"Invalid URL, please double check and try again")
      return
    }
    if (podcastService.hasShow(show)) {
      JOptionPane.showMessageDialog(null, "Show already added")
      return
    }
    if (show.isValid()){
      podcastService.addShow(show)
    } else {
      JOptionPane.showMessageDialog(
        null,
        "Invalid show URL. Please double check it's an rss feed and try again."
      )
    }
  }

  private fun drawShows() {
    this.showList.setListData(podcastService.shows.toTypedArray())
    writeShows(podcastService.shows)
  }
  private fun drawShowInfo() {
    showViewPanel.removeAll()
    val showHeaderPanel = JPanel(BorderLayout())
    val showImage =
      JLabel(
        ImageIcon(
          podcastService.selectedShow?.image
            ?: ImageIO.read(URL("https://picsum.photos/64")),
        ),
      )
    val showTitle = JLabel(podcastService.selectedShow?.title ?: "Select a Show")
    showHeaderPanel.add(showImage, BorderLayout.WEST)
    showHeaderPanel.add(showTitle, BorderLayout.CENTER)

    val showBodyPanel = JPanel(BorderLayout())
    val showDescription = JTextArea(2, 20)
    showDescription.text = podcastService.selectedShow?.description ?: ""
    showDescription.wrapStyleWord = true
    showDescription.lineWrap = true
    showDescription.isOpaque = false
    showDescription.isEditable = false
    showDescription.isFocusable = false
    showDescription.background = UIManager.getColor("Label.background")
    showDescription.font = UIManager.getFont("Label.font")
    showDescription.border = UIManager.getBorder("Label.border")
    val showDescriptionScrollPane = JScrollPane(showDescription)
    showDescriptionScrollPane.preferredSize = Dimension(10000, 175)
    showBodyPanel.add(showDescriptionScrollPane, BorderLayout.NORTH)
    val episodeList = JList<Episode>()
    episodeList.setListData(podcastService.selectedShow?.episodes?.toTypedArray() ?: arrayOf<Episode>())
    episodeList.addListSelectionListener {
      val selectedEpisode = podcastService.selectedShow?.episodes?.get(episodeList.selectedIndex)
      if (selectedEpisode != null) {
        player.stop()
        player = MP3Player(selectedEpisode.url)
        player.play()
      }
    }

    val episodeScrollPane = JScrollPane(episodeList)
    showBodyPanel.add(episodeScrollPane, BorderLayout.CENTER)

    val playPause = JButton("Play/Pause")
    playPause.addActionListener {
      if (player.isPaused) {
        player.play()
      } else {
        player.pause()
      }
    }
    showViewPanel.add(showHeaderPanel, BorderLayout.NORTH)
    showViewPanel.add(showBodyPanel, BorderLayout.CENTER)
    showViewPanel.add(playPause, BorderLayout.SOUTH)

    showViewPanel.validate()
    showViewPanel.repaint()
  }
}