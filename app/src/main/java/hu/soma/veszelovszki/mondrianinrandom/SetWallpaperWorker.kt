package hu.soma.veszelovszki.mondrianinrandom

import android.app.WallpaperManager
import android.content.*
import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.Display
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.work.*
import java.time.Duration
import java.util.*

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed")
            schedulePeriodicSetWallpaperWorker(context)
        }
    }
}

class SetWallpaperWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    companion object {
        const val ONE_TIME_WORKER_TAG = "OneTimeSetWallpaper"
        const val PERIODIC_WORKER_TAG = "PeriodicSetWallpaper"
    }

    override fun doWork(): Result {
        Log.d(TAG, "Updating wallpaper")
        generateImage().also { bitmap -> setLockScreenWallpaper(bitmap) }
        return Result.success()
    }

    private fun generateImage(): Bitmap {
        val defaultDisplay =
            DisplayManagerCompat.getInstance(applicationContext).getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = applicationContext.createDisplayContext(defaultDisplay!!)

        val canvasSize = Size(
            displayContext.resources.displayMetrics.widthPixels,
            displayContext.resources.displayMetrics.heightPixels
        )

        val lineGenerator = RandomLineGenerator(canvasSize)
        return ImageGenerator(lineGenerator).generateImage()
    }

    private fun setLockScreenWallpaper(bitmap: Bitmap) {
        WallpaperManager.getInstance(applicationContext)
            .setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
    }
}

fun schedulePeriodicSetWallpaperWorker(context: Context) {
    // initial delay is the time until midnight
    val initialDelay = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis() + Duration.ofDays(1).toMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.let { Duration.ofMillis(it.timeInMillis - System.currentTimeMillis()) }

    val workManager = WorkManager.getInstance(context)

    Log.d(SetWallpaperWorker.TAG, "Scheduling worker to run every day at midnight")

    workManager.enqueueUniquePeriodicWork(
        SetWallpaperWorker.PERIODIC_WORKER_TAG,
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        PeriodicWorkRequestBuilder<SetWallpaperWorker>(Duration.ofDays(1)).setInitialDelay(
            initialDelay
        ).build()
    )

    Log.d(SetWallpaperWorker.TAG, "Scheduling worker to run now")

    workManager.enqueueUniqueWork(
        SetWallpaperWorker.ONE_TIME_WORKER_TAG,
        ExistingWorkPolicy.REPLACE,
        OneTimeWorkRequestBuilder<SetWallpaperWorker>().build()
    )
}