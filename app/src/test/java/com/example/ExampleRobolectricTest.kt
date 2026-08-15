package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.QuizRepository
import com.example.model.QuizCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Alnaura yang Cantik", appName)
  }

  @Test
  fun `repository loads animal questions with choices`() {
    val repository = QuizRepository()
    val animalQuestions = repository.getQuestionsByCategory(QuizCategory.ANIMALS)
    assertTrue("Should have questions in animals category", animalQuestions.isNotEmpty())
    animalQuestions.forEach { q ->
      assertEquals(QuizCategory.ANIMALS, q.category)
      assertTrue("Question must have options", q.options.isNotEmpty())
      assertTrue("Question must have at least one correct option", q.options.any { it.isCorrect })
    }
  }

  @Test
  fun `can add custom quiz question for Alnaura`() {
    val repository = QuizRepository()
    val custom = repository.addCustomQuestion(
      questionText = "Apa makanan kelinci?",
      correctAnswer = "Wortel",
      wrongAnswer1 = "Batu",
      wrongAnswer2 = "Kabel",
      wrongAnswer3 = "Buku",
      funFact = "Kelinci suka makan sayur wortel yang renyah!",
      emojiHint = "🥕"
    )
    assertNotNull(custom)
    assertEquals("Apa makanan kelinci?", custom.questionText)
    assertTrue(custom.isCustom)
    assertTrue(custom.options.any { it.text == "Wortel" && it.isCorrect })
  }
}


