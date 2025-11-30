package space.httpjames.tauri_plugin_tts

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.webkit.WebView
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.Plugin
import app.tauri.plugin.Invoke
import app.tauri.plugin.JSObject
import org.json.JSONArray
import java.util.Locale
import java.util.UUID

@InvokeArg
internal class SpeakArgs {
    lateinit var text: String
    var rate: Float = 1.0f
}

data class TTSVoiceData(
    val id: String,
    val name: String,
    val lang: String,
    val disabled: Boolean = false
)

@TauriPlugin
class ExamplePlugin(private val activity: Activity): Plugin(activity) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    override fun load(webView: WebView) {
        initializeTTS()
    }

    private fun initializeTTS() {
        tts = TextToSpeech(activity) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                tts?.language = Locale.US
                val event = JSObject()
                event.put("initialized", true)
                trigger("ttsInitialized", event)
            } else {
                val event = JSObject()
                event.put("error", "Failed to initialize TTS")
                trigger("ttsError", event)
            }
        }
    }

    @Command
    fun speak(invoke: Invoke) {
        if (!isInitialized || tts == null) {
            invoke.reject("TTS not initialized")
            return
        }

        try {
            val args = invoke.parseArgs(SpeakArgs::class.java)
            val utteranceId = UUID.randomUUID().toString()
            tts?.apply {
                setSpeechRate(args.rate)
            }
            args.rate
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    val event = JSObject()
                    event.put("status", "started")
                    trigger("ttsStatus", event)
                }

                override fun onDone(utteranceId: String?) {
                    val ret = JSObject()
                    ret.put("success", true)
                    invoke.resolve(ret)
                }

                override fun onError(utteranceId: String?) {
                    invoke.reject("Speech failed")
                }
            })

            val result = tts?.speak(args.text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            if (result == TextToSpeech.ERROR) {
                invoke.reject("Failed to queue speech")
            }

        } catch (e: Exception) {
            invoke.reject(e.message ?: "Unknown error")
        }
    }

    @Command
    fun stop(invoke: Invoke) {
        tts?.stop()
        invoke.resolve()
    }

    @Command
    fun set_voice(invoke: Invoke) {
        val voice = invoke.parseArgs(String::class.java)
        try {
            val voices = tts?.voices
            val targetVoice = voices?.find { it.name == voice }
            if (targetVoice != null) {
                val result = tts?.setVoice(targetVoice)
                if (result == TextToSpeech.SUCCESS) {
                    invoke.resolve()
                } else {
                    invoke.reject("Failed to set voice: ${voice}")
                }
            } else {
                invoke.reject("Voice not found: ${voice}")
            }
        } catch (e: Exception) {
            invoke.reject("Exception setting voice: ${e.message}")
        }
    }

    @Command
    fun get_all_voices(invoke: Invoke) {
        try {
            val voices = tts?.voices?.map { voice ->
                JSObject().apply {
                    put("id", voice.name)
                    put("name", voice.name)
                    put("lang", voice.locale.toLanguageTag())
                    put("disabled", false)
                }
            } ?: emptyList()
            val result = JSObject().apply {
                put("voices", JSONArray(voices))
            }
            invoke.resolve(result)
        } catch (e: Exception) {
            invoke.reject("Exception getting voices: ${e.message}")
        }
    }

    // override fun destroy() {
    //     tts?.stop()
    //     tts?.shutdown()
    //     super.destroy()
    // }
}
