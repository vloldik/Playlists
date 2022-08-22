package ru.vladik.playlists.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.RuntimeException

object AsyncUtils {

    fun asyncLaunch(runnable: Runnable, inMain: Runnable? = null) {
        object : Thread() {
            override fun run() {
                runnable.run()
                if (inMain != null) {
                    launchInMain(inMain)
                }
            }
        }.start()
    }

    fun launchInMain(runnable: Runnable) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(runnable)
    }

}