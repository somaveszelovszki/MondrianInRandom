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

class ScreenStatusReceiver : BroadcastReceiver() {
    enum class JobId { RegisterScreenOffReceiver, SetWallpaper }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("ScreenStatusReceiver", "Boot completed")
            scheduleJob<RegisterScreenOffReceiverService>(context, JobId.RegisterScreenOffReceiver)
        } else {
            Log.d("ScreenStatusReceiver", "Screen off")
        }

        scheduleJob<SetWallpaperService>(context, JobId.SetWallpaper)
    }

    private inline fun <reified T> scheduleJob(context: Context, id: JobId) {
        val serviceComponent = ComponentName(context, T::class.java)
        val builder = JobInfo.Builder(id.ordinal, serviceComponent)
        context.getSystemService(JobScheduler::class.java).schedule(builder.build())
    }
}

class RegisterScreenOffReceiverService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("RegisterScreenOffReceiverService", "Job started")
        registerReceiver(ScreenStatusReceiver(), IntentFilter(Intent.ACTION_SCREEN_OFF))
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}

class SetWallpaperService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("SetWallpaperService", "Job started")
        generateImage().also { bitmap -> setLockScreenWallpaper(bitmap) }
        return true
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