package hu.soma.veszelovszki.mondrianinrandom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Displays a checkbox with a label.
 *
 * @param checked If true, the checkbox is checked
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