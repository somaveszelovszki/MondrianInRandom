package hu.soma.veszelovszki.mondrianinrandom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, ScreenStateService::class.java)
        startService(intent)

        finishAndRemoveTask()
    }
}
