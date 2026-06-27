package com.lyihub.archiveassistant.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lyihub.archiveassistant.domain.SampleKnowledgeData
import com.lyihub.archiveassistant.ui.theme.ArchiveAssistantTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ManagePaneTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun managePane_displaysTopicList() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                ManagePane(
                    topics = SampleKnowledgeData.topics,
                    itemsByTopic = SampleKnowledgeData.items.groupBy { it.topicId },
                    onBack = {},
                    onTopicSelected = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("manage-pane").assertIsDisplayed()
        composeTestRule.onNodeWithTag("create-topic-button").assertDoesNotExist()
        SampleKnowledgeData.topics.forEach { topic ->
            composeTestRule.onNodeWithText(topic.title).assertIsDisplayed()
            composeTestRule.onNodeWithTag("rename-topic-button-${topic.id}").assertDoesNotExist()
            composeTestRule.onNodeWithTag("delete-topic-button-${topic.id}").assertDoesNotExist()
        }
    }

    @Test
    fun managePane_emptyTopics_showsEmptyState() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                ManagePane(
                    topics = emptyList(),
                    itemsByTopic = emptyMap(),
                    onBack = {},
                    onTopicSelected = {},
                )
            }
        }

        composeTestRule.onNodeWithText("暂无主题").assertIsDisplayed()
    }

    @Test
    fun managePane_tapTopic_triggersOnTopicSelected() {
        var selectedId = ""

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                ManagePane(
                    topics = SampleKnowledgeData.topics,
                    itemsByTopic = SampleKnowledgeData.items.groupBy { it.topicId },
                    onBack = {},
                    onTopicSelected = { selectedId = it },
                )
            }
        }

        val firstTopic = SampleKnowledgeData.topics.first()
        composeTestRule.onNodeWithText(firstTopic.title).performClick()
        assertEquals(firstTopic.id, selectedId)
    }
}
