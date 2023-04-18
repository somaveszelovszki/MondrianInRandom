package hu.soma.veszelovszki.mondrianinrandom

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme

/**
 * This is the launcher activity of the application.
 * First it displays an animation with the text 'MONDRIAN'.
 * Then it asks for auto-start permissions from the user, by navigating them to the phone's settings.
 * Then it displays the feature selector, where the user decides whether to enable the home and lock-screen wallpaper generation.
 * When the 'SAVE & CLOSE' button is clicked, the application finishes.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Represents a fragment request that is implemented by one of the activity's fragments.
     */
    enum class FragmentRequests { Animation, AutoStartPermission, FeatureSelector }

    /**
     * The id of the content view that will store the fragments.
     */
    private val contentViewId = ViewCompat.generateViewId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.setFragmentResultListener(
            FragmentRequests.Animation.name, this
        ) { _, _ -> showFragment(AutoStartPermissionFragment.newInstance()) }

        supportFragmentManager.setFragmentResultListener(
            FragmentRequests.AutoStartPermission.name, this
        ) { _, _ -> showFragment(FeatureSelectorFragment.newInstance()) }

        supportFragmentManager.setFragmentResultListener(
            FragmentRequests.FeatureSelector.name, this
        ) { _, _ -> finishAndRemoveTask() }

        setContent {
            MondrianInRandomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    AndroidView(modifier = Modifier.fillMaxSize(),
                        factory = { context -> FrameLayout(context).apply { id = contentViewId } },
                        update = { showFragment(AnimationFragment.newInstance(), animate = false) })
                }

            }
        }
    }

    /**
     * Shows the fragment with an optional animation.
     *
     * @param fragment The fragment
     * @param animate If true, an animation will be shown when switching to the fragment
     */
    private fun showFragment(fragment: Fragment, animate: Boolean = true) {
        supportFragmentManager.commit {
            if (animate) {
                setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }

            replace(contentViewId, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}
