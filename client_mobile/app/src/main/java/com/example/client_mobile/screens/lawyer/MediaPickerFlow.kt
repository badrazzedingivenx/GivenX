package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// ─── Media Type ────────────────────────────────────────────────────────────────
enum class MediaPostType { Story, Reel }

// ─── Media Picker Flow ─────────────────────────────────────────────────────────
//
//  Step 0 : Action sheet   – camera vs gallery
//  Step 1 : Preview        – show selected media + caption input + Publier button
//  Step 2 : Publishing     – animated progress bar
//  Step 3 : Done           – success banner then callback
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerFlow(
    postType:    MediaPostType,
    onPublished: () -> Unit,          // called when publish completes
    onCancel:    () -> Unit
) {
    val context = LocalContext.current

    // ── Internal state ────────────────────────────────────────────────────────
    var step              by remember { mutableStateOf(0) }   // 0=picker, 1=preview, 2=publishing, 3=done
    var selectedUri       by remember { mutableStateOf<Uri?>(null) }
    var isVideo           by remember { mutableStateOf(false) }
    var caption           by remember { mutableStateOf("") }
    var cameraPhotoUri    by remember { mutableStateOf<Uri?>(null) }
    var permissionNeeded  by remember { mutableStateOf<String?>(null) }
    var showPermDenied    by remember { mutableStateOf(false) }
    var publishProgress   by remember { mutableFloatStateOf(0f) }
    var redirectCountdown by remember { mutableIntStateOf(3) }   // seconds shown in step 3

    // Helper – resets all transient state and triggers the parent callback
    fun finishAndReturn() {
        selectedUri    = null
        cameraPhotoUri = null
        caption        = ""
        isVideo        = false
        step           = 0
        redirectCountdown = 3
        onPublished()
    }

    // ── Launchers ─────────────────────────────────────────────────────────────

    // Gallery – image
    val galleryImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) { selectedUri = uri; isVideo = false; step = 1 }
    }

    // Gallery – video
    val galleryVideoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) { selectedUri = uri; isVideo = true; step = 1 }
    }

    // Camera – take photo (needs a pre-created URI)
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraPhotoUri != null) {
            selectedUri = cameraPhotoUri; isVideo = false; step = 1
        }
    }

    // Camera – record video
    val videoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && cameraPhotoUri != null) {
            selectedUri = cameraPhotoUri; isVideo = true; step = 1
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            permissionNeeded = null
            // Retry the requested action — stored in permissionNeeded label
        } else {
            showPermDenied = true
        }
    }

    // ── Helper – check & request permission then run action ──────────────────
    fun withPermissions(perms: Array<String>, action: () -> Unit) {
        val denied = perms.filter {
            ContextCompat.checkSelfPermission(context, it) != PermissionChecker.PERMISSION_GRANTED
        }
        if (denied.isEmpty()) action()
        else permissionLauncher.launch(denied.toTypedArray())
    }

    // ── Helper – create camera output URI ────────────────────────────────────
    fun createCameraUri(forVideo: Boolean): Uri? {
        val cv = ContentValues().apply {
            put(if (forVideo) MediaStore.Video.Media.DISPLAY_NAME else MediaStore.Images.Media.DISPLAY_NAME,
                "HAQ_${System.currentTimeMillis()}.${if (forVideo) "mp4" else "jpg"}")
        }
        return if (forVideo)
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv)
        else
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    // ── Publish simulation ────────────────────────────────────────────────────
    LaunchedEffect(step) {
        if (step == 2) {
            publishProgress = 0f
            repeat(50) {
                delay(40)
                publishProgress += 0.02f
            }
            // Commit to shared state
            if (postType == MediaPostType.Story) {
                CreatorRepository.postStory(
                    lawyerName = LawyerSession.fullName,
                    specialty  = LawyerSession.title
                )
            } else {
                CreatorRepository.uploadReel(
                    lawyerName = LawyerSession.fullName,
                    specialty  = LawyerSession.title,
                    title      = caption.ifBlank { "Conseil juridique" }
                )
            }
            step = 3
        }
        // ── Step 3 : countdown 3-2-1 then auto-navigate ──────────────────────
        if (step == 3) {
            redirectCountdown = 3
            repeat(3) {
                delay(1000)
                redirectCountdown--
            }
            finishAndReturn()
        }
    }

    // ── Permission denied dialog ──────────────────────────────────────────────
    if (showPermDenied) {
        AlertDialog(
            onDismissRequest = { showPermDenied = false },
            title = { Text("Autorisation requise", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen) },
            text  = { Text("Veuillez autoriser l'accès à la caméra et à la galerie dans les paramètres.", fontFamily = FontFamily.Serif, fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = { showPermDenied = false }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = AppDarkGreen)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── Step rendering ────────────────────────────────────────────────────────
    when (step) {

        // ────── Step 0 : Action Sheet ──────────────────────────────────────────
        0 -> {
            ModalBottomSheet(
                onDismissRequest = onCancel,
                containerColor   = Color.White,
                shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text       = if (postType == MediaPostType.Story) "Nouvelle Story" else "Uploader un Reel",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = AppDarkGreen,
                        modifier   = Modifier.padding(bottom = 8.dp)
                    )

                    // ── Camera option ────────────────────────────────────────
                    MediaPickerOption(
                        icon     = Icons.Default.CameraAlt,
                        title    = if (postType == MediaPostType.Reel) "Tourner une vidéo" else "Prendre une photo",
                        subtitle = if (postType == MediaPostType.Reel) "Vidéo max. 60 secondes" else "Depuis la caméra",
                        onClick  = {
                            val cameraPerms = buildList {
                                add(Manifest.permission.CAMERA)
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }.toTypedArray()

                            withPermissions(cameraPerms) {
                                if (postType == MediaPostType.Reel) {
                                    val uri = createCameraUri(forVideo = true)
                                    cameraPhotoUri = uri
                                    if (uri != null) videoLauncher.launch(uri)
                                } else {
                                    val uri = createCameraUri(forVideo = false)
                                    cameraPhotoUri = uri
                                    if (uri != null) cameraLauncher.launch(uri)
                                }
                            }
                        }
                    )

                    HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.07f))

                    // ── Gallery option ───────────────────────────────────────
                    MediaPickerOption(
                        icon     = Icons.Default.PhotoLibrary,
                        title    = if (postType == MediaPostType.Reel) "Choisir une vidéo" else "Choisir une image",
                        subtitle = if (postType == MediaPostType.Reel) "MP4 · max. 60 s" else "JPG, PNG",
                        onClick  = {
                            val galleryPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                if (postType == MediaPostType.Reel) Manifest.permission.READ_MEDIA_VIDEO
                                else Manifest.permission.READ_MEDIA_IMAGES
                            else
                                Manifest.permission.READ_EXTERNAL_STORAGE

                            withPermissions(arrayOf(galleryPerm)) {
                                if (postType == MediaPostType.Reel) galleryVideoLauncher.launch("video/*")
                                else galleryImageLauncher.launch("image/*")
                            }
                        }
                    )
                }
            }
        }

        // ────── Step 1 : Preview + Caption ────────────────────────────────────
        1 -> {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                if (postType == MediaPostType.Story) "Aperçu Story" else "Aperçu Reel",
                                fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                                fontSize = 17.sp, color = AppDarkGreen
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { step = 0; selectedUri = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                    )
                },
                containerColor = Color.White
            ) { pv ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(4.dp))

                    // ── Media preview ────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppDarkGreen.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedUri != null) {
                            AsyncImage(
                                model              = selectedUri,
                                contentDescription = "Aperçu",
                                modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                                contentScale       = ContentScale.Crop
                            )
                            // Video overlay indicator
                            if (isVideo) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(34.dp))
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.BrokenImage, contentDescription = null,
                                    tint = AppDarkGreen.copy(alpha = 0.30f), modifier = Modifier.size(48.dp))
                                Text("Aucun média sélectionné", fontFamily = FontFamily.Serif,
                                    fontSize = 13.sp, color = AppDarkGreen.copy(alpha = 0.45f))
                            }
                        }
                    }

                    // ── Media info chips ─────────────────────────────────────
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(10.dp),
                            color  = AppDarkGreen.copy(alpha = 0.08f),
                            border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.15f))) {
                            Text(
                                if (isVideo) "Vidéo · MP4" else "Image · JPG/PNG",
                                fontFamily  = FontFamily.Serif, fontSize = 11.sp, color = AppDarkGreen,
                                modifier    = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        if (isVideo) {
                            Surface(shape = RoundedCornerShape(10.dp),
                                color  = AppGoldColor.copy(alpha = 0.12f),
                                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.30f))) {
                                Text("max. 60 s",
                                    fontFamily = FontFamily.Serif, fontSize = 11.sp, color = AppGoldColor,
                                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                    }

                    // ── Caption input ────────────────────────────────────────
                    OutlinedTextField(
                        value         = caption,
                        onValueChange = { if (it.length <= 220) caption = it },
                        label         = { Text("Légende (optionnel)", fontFamily = FontFamily.Serif, fontSize = 13.sp) },
                        placeholder   = { Text(
                            if (postType == MediaPostType.Story) "Partagez un conseil rapide…"
                            else "Décrivez votre conseil juridique…",
                            fontFamily = FontFamily.Serif, fontSize = 13.sp, color = Color.Gray.copy(alpha = 0.55f))
                        },
                        modifier    = Modifier.fillMaxWidth(),
                        minLines    = 3,
                        maxLines    = 5,
                        shape       = RoundedCornerShape(16.dp),
                        colors      = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = AppDarkGreen,
                            unfocusedBorderColor    = AppDarkGreen.copy(alpha = 0.25f),
                            focusedLabelColor       = AppDarkGreen,
                            cursorColor             = AppDarkGreen
                        ),
                        supportingText = {
                            Text("${caption.length}/220", fontFamily = FontFamily.Serif,
                                fontSize = 11.sp, color = Color.Gray)
                        }
                    )

                    Spacer(Modifier.weight(1f))

                    // ── Publish button ───────────────────────────────────────
                    Button(
                        onClick  = { if (selectedUri != null) step = 2 },
                        enabled  = selectedUri != null,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = AppGoldColor,
                            contentColor   = AppDarkGreen,
                            disabledContainerColor = AppGoldColor.copy(alpha = 0.35f)
                        )
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (postType == MediaPostType.Story) "Publier la Story" else "Publier le Reel",
                            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // ────── Step 2 : Publishing progress ──────────────────────────────────
        2 -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(40.dp)
                ) {
                    // Spinning upload icon
                    val rotation by rememberInfiniteTransition(label = "uploadSpin").animateFloat(
                        initialValue  = 0f,
                        targetValue   = 360f,
                        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
                        label         = "uploadRotation"
                    )
                    Surface(modifier = Modifier.size(80.dp), shape = CircleShape,
                        color = AppGoldColor.copy(alpha = 0.12f),
                        border = BorderStroke(2.dp, AppGoldColor)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null,
                                tint = AppGoldColor, modifier = Modifier.size(38.dp))
                        }
                    }
                    Text(
                        "Publication en cours…",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = AppDarkGreen
                    )
                    Text(
                        "Traitement du média, veuillez patienter.",
                        fontFamily = FontFamily.Serif, fontSize = 13.sp,
                        color = AppDarkGreen.copy(alpha = 0.55f)
                    )
                    // Progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LinearProgressIndicator(
                            progress        = { publishProgress },
                            modifier        = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color           = AppGoldColor,
                            trackColor      = AppGoldColor.copy(alpha = 0.18f)
                        )
                        Text(
                            "${(publishProgress * 100).toInt()}%",
                            fontFamily = FontFamily.Serif, fontSize = 12.sp,
                            color = AppGoldColor, modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        // ────── Step 3 : Done ──────────────────────────────────────────────────
        3 -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    // Green checkmark circle
                    Surface(modifier = Modifier.size(88.dp), shape = CircleShape,
                        color  = Color(0xFF34A853).copy(alpha = 0.12f),
                        border = BorderStroke(2.dp, Color(0xFF34A853))) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null,
                                tint = Color(0xFF34A853), modifier = Modifier.size(44.dp))
                        }
                    }

                    Text(
                        if (postType == MediaPostType.Story) "Story publiée !" else "Reel publié !",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 22.sp, color = AppDarkGreen
                    )
                    Text(
                        "Votre contenu est maintenant visible dans le feed des utilisateurs.",
                        fontFamily = FontFamily.Serif, fontSize = 13.sp,
                        color = AppDarkGreen.copy(alpha = 0.60f)
                    )

                    // Countdown chip
                    Surface(
                        shape  = RoundedCornerShape(20.dp),
                        color  = AppDarkGreen.copy(alpha = 0.07f),
                        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.18f))
                    ) {
                        Text(
                            "Retour automatique dans ${redirectCountdown}s…",
                            fontFamily = FontFamily.Serif, fontSize = 12.sp,
                            color = AppDarkGreen.copy(alpha = 0.60f),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Manual return button
                    Button(
                        onClick  = { finishAndReturn() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = AppGoldColor,
                            contentColor   = AppDarkGreen
                        )
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Retour au Dashboard",
                            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// ─── Media Picker Option Row ───────────────────────────────────────────────────
@Composable
private fun MediaPickerOption(
    icon:    androidx.compose.ui.graphics.vector.ImageVector,
    title:   String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(modifier = Modifier.size(52.dp), shape = CircleShape,
            color = AppDarkGreen.copy(alpha = 0.08f)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null,
                    tint = AppDarkGreen, modifier = Modifier.size(26.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 15.sp, color = AppDarkGreen)
            Text(subtitle, fontFamily = FontFamily.Serif, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = AppDarkGreen.copy(alpha = 0.35f))
    }
}
