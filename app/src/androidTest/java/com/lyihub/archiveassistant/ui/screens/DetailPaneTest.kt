package com.lyihub.archiveassistant.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lyihub.archiveassistant.domain.ContentType
import com.lyihub.archiveassistant.domain.KnowledgeItem
import com.lyihub.archiveassistant.domain.SampleKnowledgeData
import com.lyihub.archiveassistant.domain.Topic
import com.lyihub.archiveassistant.ui.theme.ArchiveAssistantTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailPaneTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultTopic = SampleKnowledgeData.topics.first { it.id == SampleKnowledgeData.DefaultTopicId }
    private val defaultItems = SampleKnowledgeData.items.filter { it.topicId == SampleKnowledgeData.DefaultTopicId }

    @Test
    fun detailPane_displaysTabsAndCards() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                DetailPane(
                    topic = defaultTopic,
                    items = defaultItems,
                    activeFilter = ContentType.ALL,
                    onBack = {},
                    onFilterSelected = {},
                    onItemClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("detail-pane").assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail-tabs").assertIsDisplayed()
        defaultItems.forEach { item ->
            composeTestRule.onNodeWithTag("knowledge-card-${item.id}").assertIsDisplayed()
        }
    }

    @Test
    fun detailPane_filterTabs_switchFilter() {
        var selectedFilter: ContentType? = null

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                DetailPane(
                    topic = defaultTopic,
                    items = defaultItems,
                    activeFilter = ContentType.ALL,
                    onBack = {},
                    onFilterSelected = { selectedFilter = it },
                    onItemClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("文档/PDF").performClick()
        assertEquals(ContentType.DOCUMENT_PDF, selectedFilter)

        composeTestRule.onNodeWithText("网页文章").performClick()
        assertEquals(ContentType.WEB_ARTICLE, selectedFilter)
    }

    @Test
    fun detailPane_emptyFilter_showsEmptyState() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                DetailPane(
                    topic = defaultTopic,
                    items = emptyList(),
                    activeFilter = ContentType.DOCUMENT_PDF,
                    onBack = {},
                    onFilterSelected = {},
                    onItemClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("detail-tabs").assertIsDisplayed()
        composeTestRule.onNodeWithText("该分类下暂无资料").assertIsDisplayed()
    }

    @Test
    fun detailPane_cardClick_triggersCallback() {
        var clickedItemId: String? = null

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                DetailPane(
                    topic = defaultTopic,
                    items = defaultItems,
                    activeFilter = ContentType.ALL,
                    onBack = {},
                    onFilterSelected = {},
                    onItemClick = { clickedItemId = it },
                )
            }
        }

        val targetItem = defaultItems.first()
        composeTestRule.onNodeWithTag("knowledge-card-${targetItem.id}").performClick()
        assertEquals(targetItem.id, clickedItemId)
    }

    @Test
    fun cardModal_displaysItemDetailsAndCloses() {
        val item = defaultItems.first()
        var closeCalled = false

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                CardModal(
                    item = item,
                    onClose = { closeCalled = true },
                )
            }
        }

        composeTestRule.onNodeWithTag("card-modal").assertIsDisplayed()
        composeTestRule.onNodeWithTag("card-modal-close").assertIsDisplayed()
        composeTestRule.onNodeWithText(item.title).assertIsDisplayed()
        composeTestRule.onNodeWithText("类型: ${item.contentType.label}").assertIsDisplayed()

        composeTestRule.onNodeWithTag("card-modal-close").performClick()
        assertTrue(closeCalled)
    }
}
