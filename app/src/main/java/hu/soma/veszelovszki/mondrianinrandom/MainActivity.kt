package hu.soma.veszelovszki.mondrianinrandom

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import hu.soma.veszelovszki.mondrianinrandom.ui.theme.MondrianInRandomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MondrianInRandomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {

                    val dynamicImagePainter = DynamicImagePainter()
                    Column {
                        Button(onClick = { finish() }) {
                            Text("Unlock")
                        }

                        Image(painter = dynamicImagePainter,
                            contentDescription = "Generated Mondrian image",
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { coordinates ->
                                    val lineGenerator = RandomLineGenerator(
                                        coordinates.size.width, coordinates.size.height
                                    )
                                    dynamicImagePainter.image = ImageGenerator(
                                        coordinates.size.width,
                                        coordinates.size.height,
                                        lineGenerator
                                    )
                                        .generateImage()
                                        .asImageBitmap()
                                })
                    }

                }
            }
        }
    }
}
