package io.unthrottled.doki.service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.ColorUtil
import io.unthrottled.doki.config.ThemeConfig
import io.unthrottled.doki.themes.ThemeManager

class BeholdService {
  companion object {
    val instance: BeholdService
      get() = ApplicationManager.getApplication().getService(BeholdService::class.java)

    private val BACKGROUND_COLOR = ColorKey.createColorKey("BACKGROUND_COLOR")
  }

  fun onBeholdModeChanged() {
    val isBeholdMode = ThemeConfig.instance.isBeholdMode
    val editorColorsManager = EditorColorsManager.getInstance()
    val currentScheme = editorColorsManager.schemeForCurrentUITheme

    if (isBeholdMode) {
      val backgroundColor = currentScheme.defaultBackground
      val transparentColor = ColorUtil.toAlpha(backgroundColor, 0)
      currentScheme.setColor(BACKGROUND_COLOR, transparentColor)
    } else {
      ThemeManager.instance.currentTheme.ifPresent {
        currentScheme.setColor(BACKGROUND_COLOR, it.textEditorBackground)
      }
    }
  }
}
