package com.example.client_mobile.presentation.lawyer.screens

import com.example.client_mobile.presentation.common.components.*

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.client_mobile.screens.shared.AppDarkGreen
import com.example.client_mobile.screens.shared.AppGoldColor
import androidx.compose.material3.*

// ─── Constants ────────────────────────────────────────────────────────────────
private val CameraGold = Color(0xFFC5A059)
private val OverlayDark = Color(0x99000000)

private enum class CaptureMode(val label: String) {
    Post("POST"),
    Reel("REEL"),
    Live("LIVE")
}

// ─── Camera Capture Screen ────────────────────────────────────────────────────

private val requiredPermissions = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
)

private fun allPermissionsGranted(context: Context): Boolean =
    requiredPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

@Composable
fun CameraCaptureScreen(
    onClose: () -> Unit,
    onNavigateToLive: () -> Unit = {}
) {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(allPermissionsGranted(context)) }
    var hasRequestedOnce by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.values.all { it }
        hasRequestedOnce = true
    }

    // Auto-request on first composition
    LaunchedEffect(Unit) {
        if (!permissionsGranted) {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    if (!permissionsGranted) {
        PermissionGate(
            onClose = onClose,
            onRetry = {
                if (hasRequestedOnce) {
                    // User already denied — open app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } else {
                    permissionLauncher.launch(requiredPermissions)
                }
            }
        )
        // Re-check permissions when user returns from Settings
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                    permissionsGranted = allPermissionsGranted(context)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }
        return
    }

    CameraContent(onClose = onClose, onNavigateToLive = onNavigateToLive)
}

// ─── Permission Gate ──────────────────────────────────────────────────────────

@Composable
private fun PermissionGate(
    onClose: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Caméra & Micro requis",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Autorisez l'accès pour enregistrer.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onClose,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                ) {
                    Text("Fermer")
                }
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CameraGold,
                        contentColor = AppDarkGreen
                    )
                ) {
                    Text("Autoriser")
                }
            }
        }
    }
}

// ─── Camera Content (Full-screen preview + overlays) ──────────────────────────

@Composable
private fun CameraContent(
    onClose: () -> Unit,
    onNavigateToLive: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var useFrontCamera by remember { mutableStateOf(true) }
    var flashEnabled by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(CaptureMode.Reel) }
    var isRecording by remember { mutableStateOf(false) }

    val modes = remember { CaptureMode.entries }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Live Camera Preview ─────────────────────────────────────────────
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            useFrontCamera = useFrontCamera,
            flashEnabled = flashEnabled
        )

        // ── Top Overlay ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(40.dp)
                    .background(OverlayDark, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Flash toggle
                IconButton(
                    onClick = { flashEnabled = !flashEnabled },
                    modifier = Modifier
                        .size(40.dp)
                        .background(OverlayDark, CircleShape)
                ) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (flashEnabled) CameraGold else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                // Flip camera
                IconButton(
                    onClick = { useFrontCamera = !useFrontCamera },
                    modifier = Modifier
                        .size(40.dp)
                        .background(OverlayDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraFront,
                        contentDescription = "Retourner",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ── Bottom Controls ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Capture Button ──────────────────────────────────────────────
            CaptureButton(
                mode = selectedMode,
                isRecording = isRecording,
                onClick = {
                    when (selectedMode) {
                        CaptureMode.Live -> onNavigateToLive()
                        CaptureMode.Reel -> isRecording = !isRecording
                        CaptureMode.Post -> {
                            // Photo capture — placeholder
                        }
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            // ── Snapping Mode Picker ────────────────────────────────────────
            SnapModePicker(
                modes = modes,
                selectedMode = selectedMode,
                onModeChanged = { selectedMode = it }
            )
        }
    }
}

// ─── Camera Preview (CameraX) ─────────────────────────────────────────────────

@Composable
private fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    useFrontCamera: Boolean,
    flashEnabled: Boolean
) {
    val cameraSelector = if (useFrontCamera) {
        CameraSelector.DEFAULT_FRONT_CAMERA
    } else {
        CameraSelector.DEFAULT_BACK_CAMERA
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                    camera.cameraControl.enableTorch(flashEnabled && !useFrontCamera)
                } catch (e: Exception) {
                    Log.e("CameraCapture", "Camera bind failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        },
        modifier = Modifier.fillMaxSize()
    )
}

// ─── Capture Button ───────────────────────────────────────────────────────────

@Composable
private fun CaptureButton(
    mode: CaptureMode,
    isRecording: Boolean,
    onClick: () -> Unit
) {
    val outerColor by animateColorAsState(
        targetValue = when {
            mode == CaptureMode.Live -> Color.Red
            isRecording -> Color.Red
            else -> CameraGold
        },
        animationSpec = tween(300),
        label = "capture_ring"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(4.dp, outerColor, CircleShape)
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isRecording) {
            // Red stop square
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Red)
            )
        } else {
            // Inner fill + mode icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        when (mode) {
                            CaptureMode.Live -> Color.Red.copy(alpha = 0.9f)
                            else -> Color.White
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (mode) {
                    CaptureMode.Post -> Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Photo",
                        tint = AppDarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    CaptureMode.Reel -> Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Vidéo",
                        tint = AppDarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    CaptureMode.Live -> Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = "Live",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// ─── Snapping Mode Picker (Instagram-style) ─────────────────────────────────

@Composable
private fun SnapModePicker(
    modes: List<CaptureMode>,
    selectedMode: CaptureMode,
    onModeChanged: (CaptureMode) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = modes.indexOf(selectedMode))
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Detect which item is centered after scrolling settles
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull {
                kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
            }?.index
        }.collect { centerIdx ->
            if (centerIdx != null && centerIdx in modes.indices) {
                val mode = modes[centerIdx]
                if (mode != selectedMode) {
                    onModeChanged(mode)
                }
            }
        }
    }

    // Scroll to the selected mode when it changes via tap
    val selectedIdx = modes.indexOf(selectedMode)
    LaunchedEffect(selectedIdx) {
        listState.animateScrollToItem(selectedIdx)
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = 80.dp
    val sidePadding = (screenWidthDp - itemWidth) / 2

    LazyRow(
        state = listState,
        flingBehavior = flingBehavior,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(horizontal = sidePadding),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(modes) { _, mode ->
            val isActive = mode == selectedMode
            val textColor by animateColorAsState(
                targetValue = if (isActive) CameraGold else Color.White.copy(alpha = 0.4f),
                animationSpec = tween(200),
                label = "mode_text"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(itemWidth)
                    .clickable { onModeChanged(mode) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = mode.label,
                    color = textColor,
                    fontSize = if (isActive) 15.sp else 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(Modifier.height(6.dp))
                // Gold dot under the active mode
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isActive) CameraGold else Color.Transparent)
                )
            }
        }
    }
}
