package org.example

import jaco.mp3.player.MP3Player
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FileDialog
import java.awt.GridLayout
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*

class PodcastPlayerUI(title: String) : JFrame() {
  private val showList = JList<Show>()
  private val showViewPanel = JPanel(BorderLayout())

  init {
    createUI(title)
  }

  private fun createUI(title: String) {

    setTitle(title)

    defaultCloseOperation = EXIT_ON_CLOSE
    setSize(400, 300)
    setLocationRelativeTo(null)
    val masterPanel = JPanel(BorderLayout())
    this.contentPane.add(masterPanel)
    val showPanel = JPanel(BorderLayout())
    masterPanel.add(showPanel, BorderLayout.WEST)
    drawShows()
    this.showList.addListSelectionListener {
      selectedShow = shows[showList.selectedIndex]
      drawShowInfo()
    }
    showPanel.add(JScrollPane(showList), BorderLayout.CENTER)
    val showButtonPanel = JPanel(GridLayout(1, 2))
    val importOPMLButton = JButton("Import OPML")
    importOPMLButton.addActionListener {
        addOPML(this)
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
    val show = Show(titleInput, urlInput)
    if (shows.contains(show)) {
      JOptionPane.showMessageDialog(null, "Show already added")
      return
    }
    try {
      show.loadShowData()
      shows.add(show)
    } catch (e: Exception) {
      JOptionPane.showMessageDialog(
        null,
        "Invalid show URL. Please double check it's an rss feed and try again."
      )
    }
  }

  private fun drawShows() {
    this.showList.setListData(shows.toTypedArray())
    writeShows(shows)
  }
  private fun drawShowInfo() {
    showViewPanel.removeAll()
    val showHeaderPanel = JPanel(BorderLayout())
    val showImage =
      JLabel(
        ImageIcon(
          selectedShow?.image
            ?: ImageIO.read(URL("https://picsum.photos/64")),
        ),
      )
    val showTitle = JLabel(selectedShow?.title ?: "Select a Show")
    showHeaderPanel.add(showImage, BorderLayout.WEST)
    showHeaderPanel.add(showTitle, BorderLayout.CENTER)

    val showBodyPanel = JPanel(BorderLayout())
    val showDescription = JTextArea(2, 20)
    showDescription.text = selectedShow?.description ?: ""
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
    episodeList.setListData(selectedShow?.episodes?.toTypedArray() ?: arrayOf<Episode>())
    episodeList.addListSelectionListener {
      val selectedEpisode = selectedShow?.episodes?.get(episodeList.selectedIndex)
      if (selectedEpisode != null) {
        player.stop()
        player = MP3Player(URL(selectedEpisode.url))
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
  fun pickFile(frame: JFrame): File? {
    val dialog = FileDialog(frame)
    dialog.isVisible = true
    if (dialog.file == null) {
      return null
    }
    return File(dialog.directory, dialog.file)
  }
fun addOPML(frame: JFrame) {
  val file = pickFile(frame)
  if (file != null) {

    val parsedShows = parseOPML(file)
    for (parsedShow in parsedShows) {
      if (!shows.contains(parsedShow)) {
        shows.add(parsedShow)
      }
    }
  }

}
}