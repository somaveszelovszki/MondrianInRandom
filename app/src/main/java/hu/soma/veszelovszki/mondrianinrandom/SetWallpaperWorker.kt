package hu.soma.veszelovszki.mondrianinrandom

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.Display
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.work.*
import java.time.Duration
import java.util.*

/**
 * Broadcast receiver for the phone boot event.
 * Schedules a periodic action to set the wallpaper to a random-generated Mondrian-style picture.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed")
            schedulePeriodicSetWallpaperWorker(context)
        }
    }
}

/**
 * Worker class for generating a random Mondrian-style picture and settings it as wallpaper.
 */
class SetWallpaperWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val ONE_TIME_WORKER_TAG = "OneTimeSetWallpaper"
        const val PERIODIC_WORKER_TAG = "PeriodicSetWallpaper"
    }

    override fun doWork(): Result {
        val prefs = PreferenceManager(applicationContext)

        if (!prefs.systemWallpaperEnabled && !prefs.lockScreenWallpaperEnabled) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork(PERIODIC_WORKER_TAG)
        } else {
            generateImage().also { bitmap ->
                setLockScreenWallpaper(
                    bitmap, prefs.systemWallpaperEnabled, prefs.lockScreenWallpaperEnabled
                )
            }
        }

        return Result.success()
    }

    /**
     * Generates a Mondrian-style picture in a random manner.
     *
     * @return The generated bitmap
     */
    private fun generateImage(): Bitmap {
        val defaultDisplay =
            DisplayManagerCompat.getInstance(applicationContext).getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = applicationContext.createDisplayContext(defaultDisplay!!)

        val canvasSize = Size(
            displayContext.resources.displayMetrics.widthPixels,
            displayContext.resources.displayMetrics.heightPixels
        )

        val lineGenerator = RandomLineGenerator(canvasSize)
        return RandomImageGenerator(lineGenerator).generateImage()
    }

    /**
     * Sets the system and/or lock-screen wallpapers.
     *
     * @param bitmap The bitmap to set as wallpaper
     * @param systemWallpaperEnabled If true, the system wallpaper should be set
     * @param lockScreenWallpaperEnabled If true, the lock-screen wallpaper should be set
     */
    private fun setLockScreenWallpaper(
        bitmap: Bitmap, systemWallpaperEnabled: Boolean, lockScreenWallpaperEnabled: Boolean
    ) {
        Log.i(
            TAG, "Updating wallpaper to random-generated Mondrian picture. " + //
                    "System: $systemWallpaperEnabled. " + //
                    "Lock screen: $lockScreenWallpaperEnabled"
        )

        fun Boolean.toFlag(flag: Int) = if (this) flag else 0

        val flags = systemWallpaperEnabled.toFlag(WallpaperManager.FLAG_SYSTEM) or //
                lockScreenWallpaperEnabled.toFlag(WallpaperManager.FLAG_LOCK)

        WallpaperManager.getInstance(applicationContext).setBitmap(bitmap, null, true, flags)
    }
}

/**
 * Schedules the worker that sets the wallpaper to run immediately and also periodically, once every hour.
 *
 * @param context The application context
 */
fun schedulePeriodicSetWallpaperWorker(context: Context) {
    val interval = Duration.ofHours(1)
    val initialDelay = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis() + interval.toMillis()
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.let { Duration.ofMillis(it.timeInMillis - System.currentTimeMillis()) }

    val workManager = WorkManager.getInstance(context)

    Log.d(
        SetWallpaperWorker.TAG,
        "Scheduling worker to run once every hour. First execution will be in ${initialDelay.toMinutes()} minutes"
    )

    workManager.enqueueUniquePeriodicWork(
        SetWallpaperWorker.PERIODIC_WORKER_TAG,
        ExistingPeriodicWorkPolicy.KEEP,
        PeriodicWorkRequestBuilder<SetWallpaperWorker>(interval).setInitialDelay(initialDelay)
            .build()
    )

    Log.d(SetWallpaperWorker.TAG, "Scheduling worker to run now")

    workManager.enqueueUniqueWork(
        SetWallpaperWorker.ONE_TIME_WORKER_TAG,
        ExistingWorkPolicy.KEEP,
        OneTimeWorkRequestBuilder<SetWallpaperWorker>().build()
    )
}