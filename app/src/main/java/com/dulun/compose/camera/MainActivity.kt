package com.dulun.compose.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dulun.compose.camera.ui.theme.CameraComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraComposeTheme {
                // A surface container using the 'background' color from the theme
                CameraCapturePage()
            }
        }
    }


}