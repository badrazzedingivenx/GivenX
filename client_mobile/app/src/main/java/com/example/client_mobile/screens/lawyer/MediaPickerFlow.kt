package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// ─── Media Type ────────────────────────────────────────────────────────────────
enum class MediaPostType { Story, Reel }

// ─── Media Picker Flow ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerFlow(
    postType:    MediaPostType,
    onPublished: () -> Unit,          // called when publish completes
    onCancel:    () -> Unit,
    lawyerName:  String = "",
    specialty:   String = ""
) {
    val context = LocalContext.current

    // ── Internal state ────────────────────────────────────────────────────────
    var step              by remember { mutableStateOf(0) }   // 0=picker, 1=preview, 2=publishing, 3=done
    var selectedUri       by remember { mutableStateOf<Uri?>(null) }
    var isVideo           by remember { mutableStateOf(false) }
    var caption           by remember { mutableStateOf("") }
    
    var publishProgress   by remember { mutableFloatStateOf(0f) }
    var redirectCountdown by remember { mutableIntStateOf(3) }   // seconds shown in step 3

    // Helper – resets all transient state and triggers the parent callback
    fun finishAndReturn() {
        selectedUri    = null
        caption        = ""
        isVideo        = false
        step           = 0
        redirectCountdown = 3
        onPublished()
    }

    // ── Step Content ──────────────────────────────────────────────────────────
    AppScaffold(
        topBar = {
            if (step < 2) { // Only show top bar in picker and preview
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (step == 0) "Créer un ${postType.name}" else "Aperçu",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = AppDarkGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { if (step == 0) onCancel() else step = 0 }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (step) {
                0 -> MediaPickerStep0(
                    postType = postType,
                    onMediaSelected = { uri, video ->
                        selectedUri = uri
                        isVideo = video
                        step = 1
                    }
                )
                1 -> MediaPickerStep1(
                    uri = selectedUri!!,
                    isVideo = isVideo,
                    caption = caption,
                    onCaptionChange = { caption = it },
                    onPublish = { step = 2 }
                )
                2 -> {
                    // Start publishing simulation
                    LaunchedEffect(Unit) {
                        for (i in 1..100) {
                            delay(20)
                            publishProgress = i / 100f
                        }
                        // Perform actual repository update
                        if (postType == MediaPostType.Story) {
                            CreatorRepository.postStory(lawyerName, specialty)
                        } else {
                            CreatorRepository.uploadReel(lawyerName, specialty, caption.ifBlank { "Conseil juridique" })
                        }
                        step = 3
                    }
                    MediaPickerStep2(progress = publishProgress)
                }
                3 -> {
                    // Countdown and return
                    LaunchedEffect(Unit) {
                        while (redirectCountdown > 0) {
                            delay(1000)
                            redirectCountdown--
                        }
                        finishAndReturn()
                    }
                    MediaPickerStep3(postType = postType, redirectCountdown = redirectCountdown, onFinish = { finishAndReturn() })
                }
            }
        }
    }
}

// ─── Step 0: Picker ───────────────────────────────────────────────────────────
@Composable
private fun MediaPickerStep0(
    postType: MediaPostType,
    onMediaSelected: (Uri, Boolean) -> Unit
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) onMediaSelected(uri, false)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DarkDashCard(
            onClick = { galleryLauncher.launch("image/*") }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = AppGoldColor.copy(alpha = 0.1f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(24.dp))
                    }
                }
                Column {
                    Text("Choisir une image", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Depuis votre galerie", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                }
            }
        }
        
        if (postType == MediaPostType.Reel) {
            Spacer(modifier = Modifier.height(16.dp))
            DarkDashCard(
                onClick = { galleryLauncher.launch("video/*") }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = AppGoldColor.copy(alpha = 0.1f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(24.dp))
                        }
                    }
                    Column {
                        Text("Choisir une vidéo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Pour votre Reel professionnel", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ─── Step 1: Preview ──────────────────────────────────────────────────────────
@Composable
private fun MediaPickerStep1(
    uri: Uri,
    isVideo: Boolean,
    caption: String,
    onCaptionChange: (String) -> Unit,
    onPublish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        DashCard(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!isVideo) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Aperçu",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                            Text("Aperçu Vidéo", color = AppDarkGreen.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        LegalInputField(
            value = caption,
            onValueChange = onCaptionChange,
            label = "Légende de votre publication",
            leadingIcon = Icons.Default.Edit
        )

        LegalButton(
            text = "PUBLIER MAINTENANT",
            onClick = onPublish
        )
    }
}

// ─── Step 2: Publishing ───────────────────────────────────────────────────────
@Composable
private fun MediaPickerStep2(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = AppGoldColor.copy(alpha = 0.1f)) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = { progress }, strokeWidth = 4.dp, color = AppGoldColor, trackColor = AppGoldColor.copy(alpha = 0.2f))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Publication en cours…", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AppDarkGreen)
        Text("${(progress * 100).toInt()}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = AppGoldColor)
    }
}

// ─── Step 3: Done ────────────────────────────────────────────────────────────
@Composable
private fun MediaPickerStep3(postType: MediaPostType, redirectCountdown: Int, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = StatusGreenBg,
            border = BorderStroke(2.dp, StatusGreen)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            if (postType == MediaPostType.Story) "Story publiée !" else "Reel publié !",
            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
            fontSize = 24.sp, color = AppDarkGreen,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Votre contenu est maintenant visible dans le feed des utilisateurs.",
            fontFamily = FontFamily.Serif, fontSize = 14.sp,
            color = AppDarkGreen.copy(alpha = 0.60f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Countdown chip
        Surface(
            shape  = RoundedCornerShape(20.dp),
            color  = AppDarkGreen.copy(alpha = 0.05f),
            border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.15f))
        ) {
            Text(
                "Retour automatique dans ${redirectCountdown}s…",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 12.sp, fontFamily = FontFamily.Serif, color = AppDarkGreen.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGoldColor, contentColor = AppDarkGreen)
        ) {
            Text("RETOUR AU STUDIO", fontWeight = FontWeight.Bold)
        }
    }
}
