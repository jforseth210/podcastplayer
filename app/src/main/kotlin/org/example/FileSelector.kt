package org.example

import java.awt.FileDialog
import java.io.File
import javax.swing.JFrame

fun pickFile(frame: JFrame): File? {
  val dialog = FileDialog(frame)
  dialog.isVisible = true
  if (dialog.file == null) {
    return null
  }
  return File(dialog.directory, dialog.file)
}