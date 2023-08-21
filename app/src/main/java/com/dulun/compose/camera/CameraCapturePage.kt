package com.dulun.compose.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.dulun.compose.camera.di.FileUtils
import com.dulun.compose.camera.di.FileUtilsImpl
import java.nio.ByteBuffer

@Composable
fun CameraCapturePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val context = LocalContext.current
        var cameraPermissionState by remember {
            mutableStateOf(PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED)
        }
        val permissionRequest = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            cameraPermissionState = it
        }

        if (cameraPermissionState.not()) {
            // 无权限 展示底部解锁按钮
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f)) {}
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        permissionRequest.launch(Manifest.permission.CAMERA)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(58.dp)
                        )
                    }
                }
            }
        } else {
            // 有权限 展示拍照功能
            val context = LocalContext.current

            val imageCapture = remember {
                ImageCapture.Builder().build()
            }
            var captureSide by remember {
                mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA)
            }
            Surface(
                color = MaterialTheme.colorScheme.background, shape = CircleShape, modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .align(Alignment.Center)
            ) {
                CameraX(imageCapture, captureSide)
            }
            val spaceValue = remember {
                mutableIntStateOf(0)
            }
            val imageByteData = remember {
                mutableStateOf<ByteBuffer?>(null)
            }
            val imageUri = remember {
                mutableStateOf<Uri?>(null)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                imageUri.value?.let {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context).data(it).apply {
                                transformations(CircleCropTransformation())
                            }.build()
                        ), contentDescription = "", modifier = Modifier.size(60.dp)
                    )
                }
                imageByteData.value?.let {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context).data(it).apply {
                                transformations(CircleCropTransformation())
                            }.build()
                        ), contentDescription = "", modifier = Modifier.size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.width(spaceValue.value.dp))
                IconButton(onClick = {
//                    takeAndSavePhoto(imageCapture, context,  imageUri,spaceValue)
                    takePhoto(imageCapture, context, imageByteData, spaceValue)
                }) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {

                IconButton(
                    onClick = {
                        captureSide = if (captureSide == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
                    },
                ) {
                    Icon(painter = painterResource(id = R.mipmap.icon_turn), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(20.dp))
            }


        }
    }
}


private fun takeAndSavePhoto(
    imageCapture: ImageCapture,
    context: Context,
    imageUri: MutableState<Uri?>,
    spaceValue: MutableState<Int>
) {
    val fileUtils: FileUtils by lazy { FileUtilsImpl() }
    fileUtils.createDirectoryIfNotExist(context)
    val file = fileUtils.createFile(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                imageUri.value = outputFileResults.savedUri
                spaceValue.value = 24
                Toast.makeText(context, outputFileResults.savedUri?.path, Toast.LENGTH_SHORT).show()
                Log.d("--onImageSaved--", "onImageSaved: ${outputFileResults.savedUri?.path}")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("--onError--", exception.toString())
            }
        }
    )
}


private fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    imageByteData: MutableState<ByteBuffer?>,
    spaceValue: MutableState<Int>
) {

    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        @ExperimentalGetImage object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                imageByteData.value = imageProxy.image?.planes?.get(0)?.buffer
                spaceValue.value = 24
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("--onError--", exception.toString())
            }
        })
}

@Composable
fun CameraX(imageCapture: ImageCapture, captureSide: CameraSelector) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val previewView = remember {
        PreviewView(context)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize()) {
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val previewFunc = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
//                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        captureSide,
                        previewFunc,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("Exception", e.toString())
                }
            }, ContextCompat.getMainExecutor(context))
        }


    }
}