package org.carthigan.again

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PhotoShare - Random Photos",
        state = rememberWindowState(width = 1000.dp, height = 800.dp)
    ) {
        App()
    }
}