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
     * @param systemWallpaperEnabled Indicates if the system wallpaper should be set
     * @param lockScreenWallpaperEnabled Indicates if the lock-screen wallpaper should be set
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
 * Schedules the worker that sets the wallpaper to run immediately and also periodically at every midnight.
 *
 * @param context The application context
 */
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