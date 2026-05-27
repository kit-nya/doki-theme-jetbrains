package io.unthrottled.doki.promotions

import com.intellij.ide.IdleTracker
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import io.unthrottled.doki.themes.ThemeManager
import io.unthrottled.doki.util.doOrElse
import io.unthrottled.doki.util.toOptional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Optional
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

enum class PromotionStatus {
  ACCEPTED,
  REJECTED,
  BLOCKED,
  UNKNOWN,
}

data class PromotionResults(
  val status: PromotionStatus,
)

object AniMemePromotionService {
  fun runPromotion(
    onPromotion: (PromotionResults) -> Unit,
    onReject: () -> Unit,
  ) {
    AniMemePluginPromotionRunner(onPromotion, onReject)
  }
}

class AniMemePluginPromotionRunner(
  private val onPromotion: (PromotionResults) -> Unit,
  private val onReject: () -> Unit,
) {
  init {
    (ApplicationManager.getApplication() as CoroutineScope).launch {
      val timeout = 5.minutes + Random.nextInt(0, 2000).milliseconds
      while (true) {
        val event = withTimeoutOrNull(timeout) {
          IdleTracker.getInstance().events.first()
        }
        if (event == null) break
      }
      AniMemePluginPromotion.runPromotion(onPromotion, onReject)
    }
  }
}

object AniMemePluginPromotion {
  fun runPromotion(
    onPromotion: (PromotionResults) -> Unit,
    onReject: () -> Unit,
  ) {
    ApplicationManager.getApplication().executeOnPooledThread {
      ThemeManager.instance.currentTheme.ifPresent { dokiTheme ->
        val promotionAssets = PromotionAssets(dokiTheme)
        ApplicationManager.getApplication().invokeLater {
          getFirstProject()
            .doOrElse(
              { project ->
                AniMemePromotionDialog(
                  promotionAssets,
                  project,
                  onPromotion,
                ).show()
              },
              onReject,
            )
        }
      }
    }
  }
}

fun getFirstProject(): Optional<Project> =
  ProjectManager.getInstance().openProjects
    .toOptional()
    .filter { it.isNotEmpty() }
    .map { it.first() }
