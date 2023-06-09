package hu.soma.veszelovszki.mondrianinrandom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays checkboxes for the user to select target screen for wallpaper.
 *
 * @param defaultSystemEnabled The default value for the system wallpaper checkbox
 * @param defaultLockScreenEnabled The default value for the lock-screen wallpaper checkbox
 * @param onSetButtonClick The callback to be invoked when the 'Set' button is being clicked
 */
@Preview
@Composable
fun WallpaperTargetScreenSelector(
    defaultSystemEnabled: Boolean = true,
    defaultLockScreenEnabled: Boolean = false,
    onSetButtonClick: ((Boolean, Boolean) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Set wallpaper for:",
            fontSize = 20.sp,
            modifier = Modifier.padding(20.dp),
            textAlign = TextAlign.Center,
            color = Color.DarkGray
        )


        var systemEnabled by remember { mutableStateOf(defaultSystemEnabled) }
        var lockScreenEnabled by remember { mutableStateOf(defaultLockScreenEnabled) }

        LabelledCheckbox(
            systemEnabled,
            "Home screen",
            onCheckedChange = { enabled -> systemEnabled = enabled })

        LabelledCheckbox(lockScreenEnabled,
            "Lock screen",
            onCheckedChange = { enabled -> lockScreenEnabled = enabled })

        Button(modifier = Modifier.padding(20.dp), colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.DarkGray, contentColor = Color.White
        ), onClick = {
            onSetButtonClick?.invoke(systemEnabled, lockScreenEnabled)
        }) {
            Text("Set")
        }
    }
}
