package com.dulun.compose.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dulun.compose.camera.ui.screen.start.StartPageView
import com.dulun.compose.camera.ui.theme.CameraComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraComposeTheme {
                //是否闪屏页
                var isSplash by remember { mutableStateOf(true) }
                if (isSplash) {
                    StartPageView() {
                        isSplash = false
                    }
                } else {
                    CameraCapturePage()
                }
            }
        }
    }


}