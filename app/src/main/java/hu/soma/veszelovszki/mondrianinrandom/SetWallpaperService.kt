package hu.soma.veszelovszki.mondrianinrandom

import android.app.Service
import android.app.WallpaperManager
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Size

class SetWallpaperService : Service(), ScreenLockListener {

    enum class Options { CanvasWidth, CanvasHeight }

    private val screenStateReceiver = ScreenStateReceiver()
    private var imageGenerator: ImageGenerator? = null

    override fun onCreate() {
        super.onCreate()

        screenStateReceiver.listener = this
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val w = intent?.getIntExtra(Options.CanvasWidth.name, 0)
        val h = intent?.getIntExtra(Options.CanvasHeight.name, 0)

        if (w == null || w == 0 || h == null || h == 0) {
            throw Exception("'${Options.CanvasWidth.name}' and '${Options.CanvasHeight.name}' extras must be provided for this service")
        }

        val lineGenerator = RandomLineGenerator(Size(w, h))
        imageGenerator = ImageGenerator(lineGenerator)

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(screenStateReceiver);
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onScreenLocked() {
        imageGenerator?.let {
            WallpaperManager.getInstance(this).setBitmap(it.generateImage(), null, true, WallpaperManager.FLAG_LOCK)
        }
    }
}