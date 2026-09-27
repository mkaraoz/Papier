package org.mk.papier.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewWordsRepositoryTest {
    @Test
    fun selectionSurvivesReopeningAndStaysInSyncWithoutDuplicates() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val preferences = instrumentation.context
            .getSharedPreferences("new_words_test", Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
        try {
            instrumentation.runOnMainSync {
                NewWordsRepository(preferences).use { first ->
                    NewWordsRepository(preferences).use { second ->
                        assertTrue(first.add("1"))
                        assertFalse(second.add("1"))
                        assertTrue(second.add("2"))
                        assertEquals(setOf("1", "2"), first.wordIds.value)
                        first.remove("1")
                        assertEquals(setOf("2"), second.wordIds.value)
                    }
                }
                NewWordsRepository(preferences).use { reopened ->
                    assertEquals(setOf("2"), reopened.wordIds.value)
                    reopened.remove("2")
                    assertTrue(reopened.wordIds.value.isEmpty())
                    assertTrue(reopened.add("1"))
                }
            }
        } finally {
            preferences.edit().clear().commit()
        }
    }
}
