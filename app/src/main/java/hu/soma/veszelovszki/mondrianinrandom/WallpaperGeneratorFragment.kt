package hu.soma.veszelovszki.mondrianinrandom

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.fragment.app.Fragment

/**
 * Fragment for generating random Mondrian-style pictures
 * and settings them as system and/or lock screen wallpaper.
 */
class WallpaperGeneratorFragment : Fragment() {
    var generatedPicture: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var showSetWallpaperPopup by remember { mutableStateOf(false) }

                val (screenWidth, screenHeight) = LocalConfiguration.current.run { screenWidthDp.dp to screenHeightDp.dp }

                WallpaperGenerator(imageWidth = screenWidth * 7 / 10,
                    imageHeight = screenHeight * 7 / 10,
                    onSetButtonClick = { showSetWallpaperPopup = true })

                if (showSetWallpaperPopup) {
                    SetWallpaperPopup(width = screenWidth * 8 / 10,
                        onDismissRequest = { showSetWallpaperPopup = false })
                }
            }
        }
    }

    @Composable
    private fun WallpaperGenerator(imageWidth: Dp, imageHeight: Dp, onSetButtonClick: () -> Unit) {
        var generatedPictureVersion by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(7f), contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(imageWidth)
                        .height(imageHeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (generatedPictureVersion == 0) {
                        Text(
                            helpText,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp
                        )
                    } else {
                        Image(
                            generatedPicture!!.asImageBitmap(),
                            contentDescription = "Generated Mondrian picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .border(5.dp, Color.Black)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val buttonColors = ButtonDefaults.buttonColors(
                    disabledBackgroundColor = Color.LightGray,
                    disabledContentColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    contentColor = Color.White
                )

                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(10.dp)

                Column(modifier = Modifier.weight(1f)) {
                    Button(modifier = buttonModifier, colors = buttonColors, onClick = {
                        val imageGenerator = RandomImageGenerator(RandomLineGenerator(windowSize))
                        generatedPicture = imageGenerator.generateImage()
                        generatedPictureVersion++
                    }) {
                        Text("Generate")
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Button(
                        modifier = buttonModifier,
                        colors = buttonColors,
                        enabled = generatedPictureVersion > 0,
                        onClick = onSetButtonClick
                    ) {
                        Text("Set as...")
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    private fun PreviewWallpaperGenerator() {
        WallpaperGenerator(300.dp, 500.dp) {}
    }

    @Composable
    private fun SetWallpaperPopup(width: Dp, onDismissRequest: () -> Unit) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            Column(
                modifier = Modifier
                    .width(width)
                    .wrapContentHeight()
                    .shadow(10.dp)
                    .background(Color.White)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val prefs = PreferenceManager(requireContext())
                WallpaperTargetScreenSelector(defaultSystemEnabled = prefs.systemWallpaperEnabled,
                    defaultLockScreenEnabled = prefs.lockScreenWallpaperEnabled,
                    onSetButtonClick = { systemEnabled, lockScreenEnabled ->
                        prefs.systemWallpaperEnabled = systemEnabled
                        prefs.lockScreenWallpaperEnabled = lockScreenEnabled
                        setCurrentPictureAsWallpaper(systemEnabled, lockScreenEnabled)
                        onDismissRequest()
                    })
            }
        }
    }

    @Preview
    @Composable
    private fun PreviewSetWallpaperPopup() {
        SetWallpaperPopup(250.dp) {}
    }

    private val windowSize: android.util.Size
        get() {
            val context = requireContext()

            val defaultDisplay =
                DisplayManagerCompat.getInstance(context).getDisplay(Display.DEFAULT_DISPLAY)
            val displayContext = context.createDisplayContext(defaultDisplay!!)

            return android.util.Size(
                displayContext.resources.displayMetrics.widthPixels,
                displayContext.resources.displayMetrics.heightPixels
            )
        }

    private val helpText: AnnotatedString
        get() = buildAnnotatedString {
            append("Now it's time for you to generate a nice Mondrian-style picture!\n\n")

            append("Choose the number of lines and tap the ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Generate")
            }
            append(" button.\n\n")

            append("If you like the result, tap the ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Set as...")
            }
            append(" button to set the picture as your home screen or lock screen wallpaper.")
        }

    /**
     * Sets the system and/or lock-screen wallpapers.
     *
     * @param systemWallpaperEnabled If true, the system wallpaper should be set
     * @param lockScreenWallpaperEnabled If true, the lock-screen wallpaper should be set
     */
    private fun setCurrentPictureAsWallpaper(
        systemWallpaperEnabled: Boolean, lockScreenWallpaperEnabled: Boolean
    ) {
        Log.i(
            TAG, "Updating wallpaper to random-generated Mondrian picture. " + //
                    "System: $systemWallpaperEnabled. " + //
                    "Lock screen: $lockScreenWallpaperEnabled"
        )

        fun Boolean.toFlag(flag: Int) = if (this) flag else 0

        val flags = systemWallpaperEnabled.toFlag(WallpaperManager.FLAG_SYSTEM) or //
                lockScreenWallpaperEnabled.toFlag(WallpaperManager.FLAG_LOCK)

        WallpaperManager.getInstance(context).setBitmap(generatedPicture!!, null, true, flags)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of this fragment.
         *
         * @return A new instance of [WallpaperGeneratorFragment].
         */
        @JvmStatic
        fun newInstance() = WallpaperGeneratorFragment()
    }
}