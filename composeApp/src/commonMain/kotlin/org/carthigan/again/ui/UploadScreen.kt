package org.carthigan.again.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

@Composable
fun UploadScreen(
    onUploadRandom: () -> Unit,
    onUploadFromUrl: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageUrl by remember { mutableStateOf("") }
    var isPreviewLoading by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var previewError by remember { mutableStateOf(false) }
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight
        val isWide = maxWidth >= 840.dp
        val horizontalPadding = when {
            maxWidth < 360.dp -> 12.dp
            maxWidth < 600.dp -> 16.dp
            maxWidth < 840.dp -> 24.dp
            maxWidth < 1200.dp -> 48.dp
            maxWidth < 1600.dp -> 64.dp
            else -> 80.dp
        }
        val verticalPadding = when {
            isLandscape && maxHeight < 600.dp -> 8.dp
            maxWidth < 600.dp -> 12.dp
            else -> 16.dp
        }
        val cardSpacing = when {
            maxWidth < 600.dp -> 16.dp
            maxWidth < 840.dp -> 20.dp
            maxWidth < 1200.dp -> 24.dp
            else -> 32.dp
        }
        val headerStyle = when {
            maxWidth < 600.dp -> MaterialTheme.typography.headlineSmall
            maxWidth < 1200.dp -> MaterialTheme.typography.headlineMedium
            else -> MaterialTheme.typography.headlineLarge
        }
        val headerPadding = when {
            isLandscape && maxHeight < 600.dp -> 8.dp
            maxWidth < 600.dp -> 16.dp
            else -> 24.dp
        }
        val cardCornerRadius = when {
            maxWidth < 600.dp -> 16.dp
            else -> 20.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Upload a Photo",
                style = headerStyle,
                modifier = Modifier.padding(vertical = headerPadding)
            )

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                    verticalAlignment = Alignment.Top
                ) {
                    QuickUploadCard(
                        onUploadRandom = onUploadRandom,
                        onBack = onBack,
                        cornerRadius = cardCornerRadius,
                        modifier = Modifier.weight(1f)
                    )
                    UrlUploadCard(
                        imageUrl = imageUrl,
                        onImageUrlChange = { value ->
                            imageUrl = value
                            showPreview = false
                            previewError = false
                        },
                        showPreview = showPreview,
                        onShowPreview = {
                            showPreview = imageUrl.isNotBlank()
                            previewError = false
                        },
                        isPreviewLoading = isPreviewLoading,
                        onPreviewState = { state ->
                            isPreviewLoading = state is AsyncImagePainter.State.Loading
                            previewError = state is AsyncImagePainter.State.Error
                        },
                        previewError = previewError,
                        onUploadFromUrl = onUploadFromUrl,
                        onBack = onBack,
                        cornerRadius = cardCornerRadius,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                QuickUploadCard(
                    onUploadRandom = onUploadRandom,
                    onBack = onBack,
                    cornerRadius = cardCornerRadius,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(cardSpacing))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(cardSpacing))

                UrlUploadCard(
                    imageUrl = imageUrl,
                    onImageUrlChange = { value ->
                        imageUrl = value
                        showPreview = false
                        previewError = false
                    },
                    showPreview = showPreview,
                    onShowPreview = {
                        showPreview = imageUrl.isNotBlank()
                        previewError = false
                    },
                    isPreviewLoading = isPreviewLoading,
                    onPreviewState = { state ->
                        isPreviewLoading = state is AsyncImagePainter.State.Loading
                        previewError = state is AsyncImagePainter.State.Error
                    },
                    previewError = previewError,
                    onUploadFromUrl = onUploadFromUrl,
                    onBack = onBack,
                    cornerRadius = cardCornerRadius,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Back button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun QuickUploadCard(
    onUploadRandom: () -> Unit,
    onBack: () -> Unit,
    cornerRadius: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quick Upload",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a random beautiful photo to your gallery instantly!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onUploadRandom()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Random Photo")
            }
        }
    }
}

@Composable
private fun UrlUploadCard(
    imageUrl: String,
    onImageUrlChange: (String) -> Unit,
    showPreview: Boolean,
    onShowPreview: () -> Unit,
    isPreviewLoading: Boolean,
    onPreviewState: (AsyncImagePainter.State) -> Unit,
    previewError: Boolean,
    onUploadFromUrl: (String) -> Unit,
    onBack: () -> Unit,
    cornerRadius: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 100)
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Upload from URL",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paste an image URL to add it to your gallery",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = imageUrl,
                onValueChange = onImageUrlChange,
                label = { Text("Image URL") },
                placeholder = { Text("https://example.com/image.jpg") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (imageUrl.isNotBlank()) {
                            onShowPreview()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preview button
            OutlinedButton(
                onClick = onShowPreview,
                enabled = imageUrl.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Preview Image")
            }

            // Image preview
            AnimatedVisibility(
                visible = showPreview && imageUrl.isNotBlank(),
                enter = fadeIn() + expandVertically(animationSpec = spring()) + scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
                exit = fadeOut() + shrinkVertically() + scaleOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 2.dp,
                                color = if (previewError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Preview",
                            contentScale = ContentScale.Fit,
                            onState = onPreviewState,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isPreviewLoading) {
                            CircularProgressIndicator()
                        }

                        if (previewError) {
                            Text(
                                text = "Failed to load image",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (!previewError && !isPreviewLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onUploadFromUrl(imageUrl)
                                onBack()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload This Image")
                        }
                    }
                }
            }
        }
    }
}
