package com.lyihub.archiveassistant.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.lyihub.archiveassistant.domain.AiEngineSettings
import com.lyihub.archiveassistant.domain.AiEngineType
import com.lyihub.archiveassistant.ui.theme.ArchiveAssistantTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsPaneTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsPane_displaysGroupLabelAndEngineSelector() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = AiEngineSettings(engineType = AiEngineType.CLOUD_API),
                    onAiSettingsChanged = {},
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("settings-pane").assertIsDisplayed()
        composeTestRule.onNodeWithText("AI 推理引擎配置").assertIsDisplayed()
        composeTestRule.onNodeWithTag("engine-type-selector").assertIsDisplayed()
    }

    @Test
    fun settingsPane_cloudMode_showsCloudFieldsAndHidesLocalFields() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = AiEngineSettings(engineType = AiEngineType.CLOUD_API),
                    onAiSettingsChanged = {},
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("cloud-base-url-input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("api-key-input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cloud-model-input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("local-endpoint-input").assertDoesNotExist()
        composeTestRule.onNodeWithTag("local-model-input").assertDoesNotExist()
    }

    @Test
    fun settingsPane_localMode_showsLocalFieldsAndHidesCloudFields() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = AiEngineSettings(engineType = AiEngineType.LOCAL_MODEL),
                    onAiSettingsChanged = {},
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("local-endpoint-input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("local-model-input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cloud-base-url-input").assertDoesNotExist()
        composeTestRule.onNodeWithTag("api-key-input").assertDoesNotExist()
        composeTestRule.onNodeWithTag("cloud-model-input").assertDoesNotExist()
    }

    @Test
    fun settingsPane_switchEngineType_updatesVisibleFields() {
        var currentSettings = AiEngineSettings(engineType = AiEngineType.CLOUD_API)

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = currentSettings,
                    onAiSettingsChanged = { currentSettings = it },
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("cloud-base-url-input").assertIsDisplayed()

        composeTestRule.onNodeWithTag("engine-type-selector").performClick()
        composeTestRule.onNodeWithText("本地模型").performClick()

        composeTestRule.waitForIdle()
        assertEquals(AiEngineType.LOCAL_MODEL, currentSettings.engineType)
        composeTestRule.onNodeWithTag("local-endpoint-input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cloud-base-url-input").assertDoesNotExist()
    }

    @Test
    fun settingsPane_apiKeyInput_isMasked() {
        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = AiEngineSettings(engineType = AiEngineType.CLOUD_API),
                    onAiSettingsChanged = {},
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("api-key-input").performTextInput("sk-test-secret")
        composeTestRule.onNodeWithTag("api-key-input").assertTextEquals("•••••••••••••")
    }

    @Test
    fun settingsPane_baseUrlChange_triggersCallback() {
        var updatedSettings: AiEngineSettings? = null

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = AiEngineSettings(
                        engineType = AiEngineType.CLOUD_API,
                        baseUrl = "https://api.example.com/v1",
                    ),
                    onAiSettingsChanged = { updatedSettings = it },
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("cloud-base-url-input").performTextInput("/chat")
        composeTestRule.waitForIdle()

        assertEquals("https://api.example.com/v1/chat", updatedSettings?.baseUrl)
    }

    @Test
    fun settingsPane_localEndpointChange_triggersCallback() {
        var updatedSettings: AiEngineSettings? = null

        composeTestRule.setContent {
            ArchiveAssistantTheme {
                SettingsPane(
                    aiSettings = AiEngineSettings(
                        engineType = AiEngineType.LOCAL_MODEL,
                        localEndpoint = "http://127.0.0.1:11434",
                    ),
                    onAiSettingsChanged = { updatedSettings = it },
                    onBack = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("local-endpoint-input").performTextInput("/api")
        composeTestRule.waitForIdle()

        assertEquals("http://127.0.0.1:11434/api", updatedSettings?.localEndpoint)
    }
}
