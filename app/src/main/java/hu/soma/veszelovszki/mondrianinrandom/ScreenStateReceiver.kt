package hu.soma.veszelovszki.mondrianinrandom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

interface ScreenStateListener {
    fun onScreenLocked()
    fun onScreenUnlocked()
}

class ScreenStateReceiver : BroadcastReceiver() {
    var listener: ScreenStateListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BroadcastReceiver", "Broadcast action received: ${intent.action}")

        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            listener?.onScreenLocked()
        } else {
            listener?.onScreenUnlocked()
        }
    }
}
