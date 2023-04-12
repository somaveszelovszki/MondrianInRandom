package hu.soma.veszelovszki.mondrianinrandom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

interface ScreenLockListener {
    fun onScreenLocked()
}

class ScreenStateReceiver : BroadcastReceiver() {
    var listener: ScreenLockListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        listener?.onScreenLocked()
    }
}
