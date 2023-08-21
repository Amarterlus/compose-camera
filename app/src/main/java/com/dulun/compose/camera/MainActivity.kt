package com.dulun.compose.camera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
                /*val picker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()){

                }
                picker.launch(PickVisualMediaRequest())

                val multiPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)){

                }
                multiPicker.launch(PickVisualMediaRequest())

                val taker = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()){

                }
                taker.launch(tempFileUri)*/
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