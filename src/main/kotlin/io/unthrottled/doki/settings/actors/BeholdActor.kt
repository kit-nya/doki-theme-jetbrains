package io.unthrottled.doki.settings.actors

import io.unthrottled.doki.config.ThemeConfig
import io.unthrottled.doki.service.BeholdService

object BeholdActor {
  fun enableBeholdMode(enabled: Boolean) {
    if (enabled != ThemeConfig.instance.isBeholdMode) {
      ThemeConfig.instance.isBeholdMode = enabled
      BeholdService.instance.onBeholdModeChanged()
    }
  }
}
