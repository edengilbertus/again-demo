package org.carthigan.again

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import org.carthigan.again.data.Photo
import org.carthigan.again.ui.PhotoDetailScreen
import org.carthigan.again.ui.PhotoGalleryScreen
import org.carthigan.again.ui.UploadScreen
import org.carthigan.again.viewmodel.PhotoViewModel

/**
 * App navigation screens
 */
sealed class Screen {
    data object Gallery : Screen()
    data class Detail(val photo: Photo) : Screen()
    data object Upload : Screen()
}

/**
 * Custom theme colors
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF1F6E8C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9E5F2),
    onPrimaryContainer = Color(0xFF0B2F3A),
    secondary = Color(0xFFF29C2B),
    secondaryContainer = Color(0xFFFFE1C2),
    onSecondaryContainer = Color(0xFF4B2A00),
    tertiary = Color(0xFF2B8A3E),
    tertiaryContainer = Color(0xFFCBEFD3),
    background = Color(0xFFF7F3EE),
    surface = Color(0xFFF7F3EE),
    surfaceVariant = Color(0xFFE7DED3),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF76C6DD),
    onPrimary = Color(0xFF0B2F3A),
    primaryContainer = Color(0xFF104A5B),
    onPrimaryContainer = Color(0xFFB9E5F2),
    secondary = Color(0xFFF4B36A),
    secondaryContainer = Color(0xFF633B00),
    onSecondaryContainer = Color(0xFFFFE1C2),
    tertiary = Color(0xFF8BD49A),
    tertiaryContainer = Color(0xFF135C27),
    background = Color(0xFF151515),
    surface = Color(0xFF151515),
    surfaceVariant = Color(0xFF2A2A2A),
)

@Composable
@Preview
fun App() {
    val viewModel: PhotoViewModel = viewModel { PhotoViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    
    var currentScreen: Screen by remember { mutableStateOf(Screen.Gallery) }
    
    MaterialTheme(
        colorScheme = LightColors
    ) {
        Scaffold(
            floatingActionButton = {
                // Only show FAB on gallery screen with animation
                AnimatedVisibility(
                    visible = currentScreen is Screen.Gallery,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.88f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )

                    FloatingActionButton(
                        onClick = { currentScreen = Screen.Upload },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        interactionSource = interactionSource,
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        ) { padding ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    when {
                        targetState is Screen.Detail -> {
                            (fadeIn() + slideInHorizontally { it }) togetherWith
                                    (fadeOut() + slideOutHorizontally { -it })
                        }
                        targetState is Screen.Upload -> {
                            (fadeIn() + slideInHorizontally { it }) togetherWith
                                    (fadeOut() + slideOutHorizontally { -it })
                        }
                        else -> {
                            (fadeIn() + slideInHorizontally { -it }) togetherWith
                                    (fadeOut() + slideOutHorizontally { it })
                        }
                    }
                },
                modifier = Modifier.padding(padding)
            ) { screen ->
                when (screen) {
                    is Screen.Gallery -> {
                        PhotoGalleryScreen(
                            uiState = uiState,
                            onPhotoClick = { photo ->
                                currentScreen = Screen.Detail(photo)
                            },
                            onRefresh = { viewModel.refreshPhotos() },
                            onLoadMore = { viewModel.loadMorePhotos() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is Screen.Detail -> {
                        PhotoDetailScreen(
                            photo = screen.photo,
                            onBack = { currentScreen = Screen.Gallery },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is Screen.Upload -> {
                        UploadScreen(
                            onUploadRandom = { viewModel.uploadNewPhoto() },
                            onUploadFromUrl = { url -> viewModel.uploadPhotoFromUrl(url) },
                            onBack = { currentScreen = Screen.Gallery },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
