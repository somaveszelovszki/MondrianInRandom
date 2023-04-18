package hu.soma.veszelovszki.mondrianinrandom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.judemanutd.autostarter.AutoStartPermissionHelper

/**
 * Fragment for asking permission for auto-starting the application.
 * If supported, the user will be navigated to the phone's settings to give permission.
 * When they return to this fragment, sets the result for the 'AutoStartPermission' fragment request.
 */
class AutoStartPermissionFragment : Fragment() {
    private var permissionChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        return ComposeView(context).apply {
            setContent {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Please enable ${getString(R.string.app_name)} to start up automatically when you restart your phone.",
                        modifier = Modifier.padding(20.dp),
                        color = Color.DarkGray,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "This is necessary so that the application can update the wallpaper in the background.",
                        modifier = Modifier.padding(30.dp),
                        color = Color.DarkGray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(modifier = Modifier.padding(30.dp), colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.DarkGray, contentColor = Color.White
                    ), onClick = {
                        permissionChecked = false
                        val started = AutoStartPermissionHelper.getInstance()
                            .getAutoStartPermission(context, open = true, newTask = true)
                        if (started) {
                            // The user has been navigated to the settings to enable the auto-start permission.
                            // When they return to this fragment, the onResume() will be called and this flag will be checked.
                            permissionChecked = true
                        } else {
                            finishRequest()
                        }
                    }) {
                        Text("ENABLE")
                    }
                }


            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (permissionChecked) {
            finishRequest()
        }
    }

    /**
     * Sets the result for the 'AutoStartPermission' fragment request.
     */
    private fun finishRequest() {
        setFragmentResult(MainActivity.FragmentRequests.AutoStartPermission.name, Bundle())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment.
         *
         * @return A new instance of AutoStartPermissionFragment.
         */
        @JvmStatic
        fun newInstance() = AutoStartPermissionFragment()
    }
}