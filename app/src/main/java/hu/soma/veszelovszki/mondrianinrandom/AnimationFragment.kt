package hu.soma.veszelovszki.mondrianinrandom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult

/**
 * Fragment for displaying an animation with the text 'MONDRIAN'.
 * When the animation is finished, sets the result for the 'Animation' fragment request.
 */
class AnimationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        return ComposeView(context).apply {
            setContent {
                AnimatedLetters(context, onAnimationFinished = {
                    Handler(Looper.getMainLooper()).postDelayed({
                        setFragmentResult(MainActivity.FragmentRequests.Animation.name, Bundle())
                    }, 1000)
                })
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment.
         *
         * @return A new instance of AnimationFragment.
         */
        @JvmStatic
        fun newInstance() = AnimationFragment()
    }
}