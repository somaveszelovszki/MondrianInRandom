package hu.soma.veszelovszki.mondrianinrandom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult

/**
 * Fragment for displaying the feature selector, where the user decides
 * whether to enable the home and lock-screen wallpaper generation.
 * When the 'SAVE & CLOSE' button is clicked, sets the result for the 'FeatureSelector' fragment request.
 */
class FeatureSelectorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        return ComposeView(requireContext()).apply {
            setContent {
                val prefs = PreferenceManager(context)

                FeatureSelector(defaultSystemEnabled = prefs.systemWallpaperEnabled,
                    defaultLockScreenEnabled = prefs.lockScreenWallpaperEnabled,
                    onSaved = { systemEnabled, lockScreenEnabled ->
                        prefs.systemWallpaperEnabled = systemEnabled
                        prefs.lockScreenWallpaperEnabled = lockScreenEnabled

                        schedulePeriodicSetWallpaperWorker(context)

                        setFragmentResult(
                            MainActivity.FragmentRequests.FeatureSelector.name,
                            Bundle()
                        )
                    })
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment.
         *
         * @return A new instance of FeatureSelectorFragment.
         */
        @JvmStatic
        fun newInstance() = FeatureSelectorFragment()
    }
}