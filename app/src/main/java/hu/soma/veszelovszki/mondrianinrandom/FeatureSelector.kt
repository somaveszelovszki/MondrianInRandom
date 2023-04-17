package hu.soma.veszelovszki.mondrianinrandom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays a checkbox with a label.
 *
 * @param checked Indicates if the checkbox is checked
 * @param text The text to display next to the checkbox
 * @param onCheckedChange Callback to be invoked when the checkbox is being clicked
 */
@Composable
fun LabelledCheckbox(checked: Boolean, text: String, onCheckedChange: ((Boolean) -> Unit)?) {
    Row(
        modifier = Modifier.width(155.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(
                uncheckedColor = Color.DarkGray,
                checkedColor = Color.DarkGray,
                checkmarkColor = Color.White
            )
        )
        Text(text, modifier = Modifier.padding(start = 2.dp))
    }
}

/**
 * Displays checkboxes for the user to enable/disable home screen and lock screen wallpaper generation.
 *
 * @param defaultSystemEnabled The default value for the system wallpaper checkbox
 * @param defaultLockScreenEnabled The default value for the lock-screen wallpaper checkbox
 * @param onSaved The callback to be invoked when the 'SAVE & CLOSE' button is being clicked
 */
@Preview
@Composable
fun FeatureSelector(
    defaultSystemEnabled: Boolean = true,
    defaultLockScreenEnabled: Boolean = false,
    onSaved: ((Boolean, Boolean) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Enable random-generated Mondrian-style wallpaper for:",
            fontSize = 22.sp,
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
            onSaved?.invoke(systemEnabled, lockScreenEnabled)
        }) {
            Text("SAVE & CLOSE")
        }
    }
}
