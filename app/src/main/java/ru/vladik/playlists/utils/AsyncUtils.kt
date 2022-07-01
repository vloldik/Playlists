package ru.vladik.playlists.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private fun launchInMain(runnable: Runnable) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(runnable)
    }

}