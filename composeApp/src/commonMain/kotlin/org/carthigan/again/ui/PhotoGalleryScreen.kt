package org.carthigan.again.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.runtime.setValue
import org.carthigan.again.data.Photo
import org.carthigan.again.data.PhotosUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGalleryScreen(
    uiState: PhotosUiState,
    onPhotoClick: (Photo) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyStaggeredGridState()

    // Detect when we're near the end of the list to load more
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= uiState.photos.size - 6
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !uiState.isLoading && uiState.error == null) {
            onLoadMore()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Pinterest-style adaptive columns based on screen width
        val columns = when {
            maxWidth < 360.dp -> 2  // Very small phones
            maxWidth < 600.dp -> 2  // Phones portrait
            maxWidth < 840.dp -> 3  // Large phones / small tablets
            maxWidth < 1200.dp -> 4 // Tablets
            maxWidth < 1600.dp -> 5 // Desktop
            else -> 6               // Large desktop
        }

        val spacing = when {
            maxWidth < 600.dp -> 8.dp
            maxWidth < 840.dp -> 10.dp
            maxWidth < 1200.dp -> 12.dp
            else -> 16.dp
        }

        val contentPadding = PaddingValues(
            horizontal = when {
                maxWidth < 600.dp -> 12.dp
                maxWidth < 840.dp -> 16.dp
                maxWidth < 1200.dp -> 24.dp
                maxWidth < 1600.dp -> 32.dp
                else -> 48.dp
            },
            vertical = when {
                maxWidth < 600.dp -> 12.dp
                else -> 16.dp
            }
        )

        val cornerRadius = when {
            maxWidth < 600.dp -> 16.dp
            else -> 20.dp
        }

        PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.photos.isEmpty(),
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.error != null && uiState.photos.isEmpty() -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.photos.isEmpty() && uiState.isLoading -> {
                    LoadingContent(modifier = Modifier.fillMaxSize())
                }
                else -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(columns),
                        state = gridState,
                        contentPadding = contentPadding,
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalItemSpacing = spacing,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.photos,
                            key = { it.id }
                        ) { photo ->
                            PhotoGridItem(
                                photo = photo,
                                onClick = { onPhotoClick(photo) },
                                cornerRadius = cornerRadius
                            )
                        }

                        // Loading indicator at the bottom
                        if (uiState.isLoading && uiState.photos.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGridItem(
    photo: Photo,
    onClick: () -> Unit,
    cornerRadius: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 450)
    )
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isVisible -> 1f
            else -> 0.93f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 6f,
        animationSpec = tween(durationMillis = 150)
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Vary aspect ratio slightly for Pinterest-style layout
    val aspectRatio = remember {
        when ((photo.id.toIntOrNull() ?: 0) % 5) {
            0 -> 0.75f  // Tall
            1 -> 1.0f   // Square
            2 -> 0.85f  // Slightly tall
            3 -> 1.2f   // Slightly wide
            else -> 0.9f // Default
        }
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Box {
            AsyncImage(
                model = photo.getThumbnailUrl(),
                contentDescription = "Photo by ${photo.author}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay with author name
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = photo.author,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading photos...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium
            )
            androidx.compose.material3.Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
