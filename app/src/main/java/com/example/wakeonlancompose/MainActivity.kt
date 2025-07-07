package com.example.wakeonlancompose

import android.os.Bundle

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle


class MainActivity : ComponentActivity() {
    private val viewModel: PowerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            WolApp(viewModel)
        }
    }
}

@Composable
fun WolApp(viewModel: PowerViewModel) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsStateWithLifecycle(null)

    LaunchedEffect(events) {
        when (val event = events) {
            is WakeOnLanEvent.Success -> {
                Toast.makeText(context, "Computador ligado!", Toast.LENGTH_SHORT).show()
            }
            is WakeOnLanEvent.Error -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
            null -> {}
        }
    }

    PowerButtonScreen(onPowerButtonClick = { viewModel.sendWakeOnLanPacket() })
}

@Composable
fun PowerButtonScreen(onPowerButtonClick: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.green_energy),
            contentDescription = "Botão Power",
            modifier = Modifier
                .size(100.dp)
                .clickable {
                    if (!isLoading) {
                        isLoading = true
                        onPowerButtonClick()
                    }
                }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center),
                color = Color.Green
            )
        }
    }
}