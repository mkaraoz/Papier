package org.mk.papier.ui.speech

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Thin wrapper around Android's on-device [TextToSpeech] engine, pinned to Dutch.
 *
 * Experimental: whether Dutch voice data is installed is up to the device, so the
 * caller is expected to render a disabled control when [ready] stays false.
 */
class DutchTts(context: Context) {

    /** True once the engine is initialised and a Dutch voice is actually available. */
    var ready by mutableStateOf(false)
        private set

    /** Id of the word currently being spoken, or null when silent. */
    var speakingId by mutableStateOf<String?>(null)
        private set

    private var engine: TextToSpeech? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var startedAt = 0L

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            val tts = engine
            if (status == TextToSpeech.SUCCESS && tts != null) {
                val result = tts.setLanguage(Locale("nl", "NL"))
                ready = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
                // Native pace is too quick to imitate at A2.
                tts.setSpeechRate(SPEECH_RATE)
            } else {
                ready = false
            }
        }
        engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                mainHandler.post { speakingId = utteranceId }
            }

            override fun onDone(utteranceId: String?) {
                clearIfCurrent(utteranceId)
            }

            @Deprecated("Superseded by onError(String, int)")
            override fun onError(utteranceId: String?) {
                clearIfCurrent(utteranceId)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                clearIfCurrent(utteranceId)
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                clearIfCurrent(utteranceId)
            }
        })
    }

    /** Speaks [text], cancelling anything already playing. */
    fun speak(id: String, text: String) {
        val tts = engine ?: return
        if (!ready) return
        // Drop the previous utterance's pending clear so it can't wipe this highlight.
        mainHandler.removeCallbacksAndMessages(null)
        // Optimistic: onStart arrives on a binder thread a beat later.
        speakingId = id
        startedAt = SystemClock.uptimeMillis()
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle(), id)
    }

    fun shutdown() {
        mainHandler.removeCallbacksAndMessages(null)
        engine?.stop()
        engine?.shutdown()
        engine = null
        speakingId = null
        ready = false
    }

    // Progress callbacks arrive on a binder thread; hop to main before touching state.
    // A one-syllable word is done in ~300ms, so hold the highlight long enough
    // that every tap visibly registers.
    private fun clearIfCurrent(utteranceId: String?) {
        val remaining = MIN_HIGHLIGHT_MS - (SystemClock.uptimeMillis() - startedAt)
        mainHandler.postDelayed(
            { if (speakingId == utteranceId) speakingId = null },
            remaining.coerceIn(0L, MIN_HIGHLIGHT_MS)
        )
    }

    private companion object {
        const val SPEECH_RATE = 0.85f
        const val MIN_HIGHLIGHT_MS = 500L
    }
}

/** Creates a [DutchTts] tied to the composition, shutting the engine down on dispose. */
@Composable
fun rememberDutchTts(): DutchTts {
    val context = LocalContext.current
    val tts = remember { DutchTts(context) }
    DisposableEffect(tts) {
        onDispose { tts.shutdown() }
    }
    return tts
}
