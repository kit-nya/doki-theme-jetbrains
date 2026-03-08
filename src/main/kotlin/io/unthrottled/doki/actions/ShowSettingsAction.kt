package io.unthrottled.doki.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import io.unthrottled.doki.settings.ThemeSettingsUI

class ShowSettingsAction : AnAction(), DumbAware {
  override fun actionPerformed(e: AnActionEvent) {
    ShowSettingsUtil.getInstance().showSettingsDialog(
      e.project,
      ThemeSettingsUI::class.java,
    )
  }
}
