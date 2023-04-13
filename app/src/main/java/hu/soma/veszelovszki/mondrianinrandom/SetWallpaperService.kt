package hu.soma.veszelovszki.mondrianinrandom

import android.app.WallpaperManager
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.*
import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.Display
import androidx.core.hardware.display.DisplayManagerCompat

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed")

            val serviceComponent = ComponentName(context, SetWallpaperService::class.java)
            val builder =
                JobInfo.Builder(1, serviceComponent).setPeriodic(1000) // runs every hour
            context.getSystemService(JobScheduler::class.java).schedule(builder.build())
        }
    }
}

class SetWallpaperService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Job started")
        generateImage().also { bitmap -> setLockScreenWallpaper(bitmap) }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun generateImage(): Bitmap {
        val defaultDisplay =
            DisplayManagerCompat.getInstance(this).getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = createDisplayContext(defaultDisplay!!)

        val canvasSize = Size(
            displayContext.resources.displayMetrics.widthPixels,
            displayContext.resources.displayMetrics.heightPixels
        )

        val lineGenerator = RandomLineGenerator(canvasSize)
        return ImageGenerator(lineGenerator).generateImage()
    }

    private fun setLockScreenWallpaper(bitmap: Bitmap) {
        WallpaperManager.getInstance(this).setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
    }
}