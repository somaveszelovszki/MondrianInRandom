package hu.soma.veszelovszki.mondrianinrandom

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class ScreenStateService: Service(), ScreenStateListener {
    private val screenStateReceiver = ScreenStateReceiver()

    override fun onCreate() {
        super.onCreate()

        screenStateReceiver.listener = this

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(screenStateReceiver);
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onScreenLocked() {
        Log.d("ScreenStateService", "Screen locked")
    }

    override fun onScreenUnlocked() {
        Log.d("ScreenStateService", "Screen unlocked")
        val intent = Intent(this, LockScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}