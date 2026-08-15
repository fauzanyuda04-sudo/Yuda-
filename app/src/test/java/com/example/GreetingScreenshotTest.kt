package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.AlnauraEmotion
import com.example.model.QuizOption
import com.example.ui.components.AlnauraCharacterBubble
import com.example.ui.components.QuizOptionCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleOption = QuizOption(
      id = "opt_demo",
      text = "Kucing Lucu",
      emoji = "🐱",
      isCorrect = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        QuizOptionCard(
          option = sampleOption,
          index = 0,
          isSelected = true,
          isAnswerRevealed = true,
          onSelect = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}


