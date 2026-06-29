package com.lyihub.archiveassistant.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.lyihub.archiveassistant.domain.KnowledgeItem

@Composable
fun MemorialDemoOverlay(
  items: List<KnowledgeItem>,
  onDismiss: () -> Unit,
) {
  val foldView = remember { mutableStateOf<MemorialFoldView?>(null) }
  val dossiers = remember(items) { buildPendingDossiers(items) }
  val dismissStarted = remember { mutableStateOf(false) }
  val completeDismiss = {
    onDismiss()
  }
  val autoDismiss = {
    if (!dismissStarted.value) {
      dismissStarted.value = true
      onDismiss()
    }
  }
  val requestDismiss = {
    if (!dismissStarted.value) {
      dismissStarted.value = true
      foldView.value?.closeWithAnimation(completeDismiss) ?: completeDismiss()
    }
  }

  BackHandler(onBack = requestDismiss)
  MemorialImmersiveSystemUi(onDispose = { foldView.value = null })

  Box(
    modifier =
      Modifier.fillMaxSize().background(Color(APP_BACKGROUND_BASE)).testTag("memorial-demo-overlay")
  ) {
    AndroidView(
      factory = { context ->
        MemorialFoldView(context).apply {
          foldView.value = this
          setAutoDismissHandler(autoDismiss)
          setDossiers(dossiers)
        }
      },
      update = { view ->
        foldView.value = view
        view.setAutoDismissHandler(autoDismiss)
        view.setDossiers(dossiers)
      },
      modifier = Modifier.fillMaxSize(),
    )
  }
}

private fun buildPendingDossiers(items: List<KnowledgeItem>): List<PendingMemorialDossier> {
  val fromItems =
    items.take(TOTAL_PENDING_MEMORIALS).map { item ->
      PendingMemorialDossier(
        title = item.title,
        source = sourceLine(item),
        summary = item.summary.ifBlank { item.fullText.lineSequence().firstOrNull().orEmpty() },
        body = readingBody(item),
        tags = articleTagsFromFullText(item.fullText),
        createdAtEpochMillis = item.createdAtEpochMillis,
      )
    }
  if (fromItems.size >= TOTAL_PENDING_MEMORIALS) return fromItems
  return (fromItems + fallbackPendingMemorialDossiers).take(TOTAL_PENDING_MEMORIALS)
}

private fun sourceLine(item: KnowledgeItem): String {
  val source =
    item.fullText
      .lineSequence()
      .firstOrNull { it.startsWith("来源：") }
      ?.removePrefix("来源：")
      ?.takeIf { it.isNotBlank() }
  return source ?: item.sourceUrl ?: "聚合拾遗"
}

private fun readingBody(item: KnowledgeItem): String {
  return item.fullText.substringAfter("整理正文", item.fullText).trim().ifBlank {
    item.summary.ifBlank { item.title }
  }
}

private fun articleTagsFromFullText(fullText: String): List<String> {
  return fullText
    .lineSequence()
    .firstOrNull { it.startsWith("标签：") }
    ?.removePrefix("标签：")
    ?.split("·", "、", ",", "，")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?.distinct()
    ?.take(4)
    .orEmpty()
}
