package com.dulun.compose.camera.ui.screen.start

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartPageView(onNextPage: () -> Unit) {
    var lineAlpha by remember {
        mutableFloatStateOf(0f)
    }
    val animateAble = remember {
        Animatable(0f)
    }
    val circleColor = MaterialTheme.colorScheme.primaryContainer
    val lineColor = MaterialTheme.colorScheme.onPrimaryContainer
    var contentVisible by remember {
        mutableStateOf(false)
    }
    var showLine by remember {
        mutableStateOf(false)
    }
    var iconVisible by remember {
        mutableStateOf(false)
    }
    val canvasRotation by animateFloatAsState(
        targetValue = if (lineAlpha == 1f) 360f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = ""
    )
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            delay(500)
            iconVisible = true
            delay(500)
            showLine = true
            animateAble.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }
        withContext(Dispatchers.IO) {
            contentVisible = true
        }
        delay(1500)
        onNextPage.invoke()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = iconVisible,
            enter = scaleIn()
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .rotate(canvasRotation)
            ) {
                val centerOffset = Offset(size.width / 2, size.height / 2)
                drawCircle(
                    color = circleColor,
                    radius = size.minDimension / 2,
                    center = centerOffset
                )
                if (showLine) {
                    lineAlpha = animateAble.value
                    drawLine(
                        color = lineColor,
                        start = centerOffset,
                        end = Offset(size.width / 2, size.height / 2 + (size.height / 3)),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        alpha = lineAlpha
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = contentVisible,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(
                initialAlpha = 0.1f
            )
        ) {
            Text(
                text = "Run",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartPagePreview() {
    StartPageView() {}
}
