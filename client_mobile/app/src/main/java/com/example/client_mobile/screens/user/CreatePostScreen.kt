package com.example.client_mobile.screens.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.screens.shared.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Public entry point ───────────────────────────────────────────────────────
/**
 * Full-screen "create post" flow.
 * Step 0 = Compose  |  Step 1 = Preview  |  Step 2 = Publishing  |  Step 3 = Done
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPublished: () -> Unit,
    onCancel:    () -> Unit,
    viewModel:   PostViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    // ── Internal flow state ────────────────────────────────────────────────────
    var step          by remember { mutableIntStateOf(0) }
    var content       by remember { mutableStateOf("") }
    var selectedUri   by remember { mutableStateOf<Uri?>(null) }
    var hashtagInput  by remember { mutableStateOf("") }
    var confirmedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var progress      by remember { mutableFloatStateOf(0f) }
    var countdown     by remember { mutableIntStateOf(3) }
    var errorMsg      by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) selectedUri = uri }

    fun finish() { onPublished() }

    AppScaffold(
        topBar = {
            if (step < 2) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text       = if (step == 0) "Nouvelle publication" else "Aperçu",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color      = AppDarkGreen
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { if (step == 0) onCancel() else step = 0 }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = AppDarkGreen)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (step) {
                0 -> ComposeStep(
                    content              = content,
                    onContentChange      = { content = it },
                    selectedUri          = selectedUri,
                    hashtagInput         = hashtagInput,
                    onHashtagInputChange = { hashtagInput = it },
                    confirmedTags        = confirmedTags,
                    onAddTag             = {
                        val tag = hashtagInput.trim().trimStart('#').lowercase()
                        if (tag.isNotBlank() && confirmedTags.size < 10) {
                            confirmedTags = confirmedTags + "#$tag"
                            hashtagInput = ""
                        }
                    },
                    onRemoveTag          = { tag -> confirmedTags = confirmedTags - tag },
                    onPickImage          = { galleryLauncher.launch("image/*") },
                    onRemoveImage        = { selectedUri = null },
                    errorMsg             = errorMsg,
                    onNext               = { step = 1 }
                )

                1 -> PreviewStep(
                    content     = content,
                    selectedUri = selectedUri,
                    hashtags    = confirmedTags,
                    onPublish   = { step = 2 }
                )

                2 -> {
                    LaunchedEffect(Unit) {
                        progress = 0f
                        val progressJob = scope.launch {
                            for (i in 1..100) {
                                delay(25)
                                progress = i / 100f
                            }
                        }
                        val ok = MainRepository.createPost(
                            context  = context,
                            content  = content,
                            mediaUri = selectedUri,
                            hashtags = confirmedTags.joinToString(",") { it.trimStart('#') }
                                .takeIf { it.isNotBlank() },
                            mentions = null
                        )
                        progressJob.join()
                        if (ok) {
                            viewModel.loadPosts(isRefresh = true)
                            step = 3
                        } else {
                            errorMsg = "La publication a échoué. Veuillez réessayer."
                            step = 0
                        }
                    }
                    PublishingStep(progress = progress)
                }

                3 -> {
                    LaunchedEffect(Unit) {
                        while (countdown > 0) {
                            delay(1000)
                            countdown--
                        }
                        finish()
                    }
                    DoneStep(countdown = countdown, onFinish = { finish() })
                }
            }
        }
    }
}

// ─── Step 0: Compose ──────────────────────────────────────────────────────────
@Composable
private fun ComposeStep(
    content:              String,
    onContentChange:      (String) -> Unit,
    selectedUri:          Uri?,
    hashtagInput:         String,
    onHashtagInputChange: (String) -> Unit,
    confirmedTags:        List<String>,
    onAddTag:             () -> Unit,
    onRemoveTag:          (String) -> Unit,
    onPickImage:          () -> Unit,
    onRemoveImage:        () -> Unit,
    errorMsg:             String?,
    onNext:               () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Error banner ──────────────────────────────────────────────────────
        if (errorMsg != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFEBEE)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                    Text(errorMsg, color = Color(0xFFB71C1C), fontSize = 13.sp)
                }
            }
        }

        // ── Content text field ────────────────────────────────────────────────
        DashCard {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "Votre publication",
                    color      = AppDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    modifier   = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value         = content,
                    onValueChange = onContentChange,
                    modifier      = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    placeholder   = {
                        Text(
                            "Partagez vos connaissances juridiques…",
                            color = AppDarkGreen.copy(alpha = 0.4f)
                        )
                    },
                    maxLines = 10,
                    colors   = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppGoldColor,
                        unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.2f),
                        cursorColor          = AppDarkGreen,
                        focusedTextColor     = AppDarkGreen,
                        unfocusedTextColor   = AppDarkGreen
                    )
                )
            }
        }

        // ── Hashtags ──────────────────────────────────────────────────────────
        DashCard {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "Hashtags",
                    color      = AppDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    modifier   = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = hashtagInput,
                        onValueChange = onHashtagInputChange,
                        modifier      = Modifier.weight(1f),
                        placeholder   = { Text("#droit-civil", color = AppDarkGreen.copy(alpha = 0.4f)) },
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions(onDone = { onAddTag() }),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AppGoldColor,
                            unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.2f),
                            cursorColor          = AppDarkGreen,
                            focusedTextColor     = AppDarkGreen,
                            unfocusedTextColor   = AppDarkGreen
                        )
                    )
                    FilledTonalIconButton(
                        onClick = onAddTag,
                        colors  = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = AppGoldColor.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(Icons.Default.Add, "Ajouter", tint = AppGoldColor)
                    }
                }
                if (confirmedTags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(confirmedTags) { tag ->
                            HashtagChip(tag = tag, onRemove = { onRemoveTag(tag) })
                        }
                    }
                }
            }
        }

        // ── Media picker ──────────────────────────────────────────────────────
        DashCard {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "Image (optionnel)",
                    color      = AppDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    modifier   = Modifier.padding(bottom = 8.dp)
                )
                if (selectedUri == null) {
                    OutlinedButton(
                        onClick  = onPickImage,
                        modifier = Modifier.fillMaxWidth(),
                        border   = BorderStroke(1.5.dp, AppGoldColor.copy(alpha = 0.5f)),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary, null,
                            tint     = AppGoldColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Choisir depuis la galerie", color = AppDarkGreen, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        AsyncImage(
                            model              = selectedUri,
                            contentDescription = "Image sélectionnée",
                            modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                            contentScale       = ContentScale.Crop
                        )
                        IconButton(
                            onClick  = onRemoveImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Close, "Supprimer",
                                tint     = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        OutlinedButton(
                            onClick  = onPickImage,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                            border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Changer", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Next button ───────────────────────────────────────────────────────
        Button(
            onClick  = onNext,
            enabled  = content.isNotBlank() || selectedUri != null,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = AppDarkGreen,
                disabledContainerColor = AppDarkGreen.copy(alpha = 0.3f)
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Aperçu & Publier", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ─── Step 1: Preview ──────────────────────────────────────────────────────────
@Composable
private fun PreviewStep(
    content:     String,
    selectedUri: Uri?,
    hashtags:    List<String>,
    onPublish:   () -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Aperçu de la publication",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color      = AppDarkGreen,
            fontSize   = 16.sp
        )
        DashCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier            = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Author stub
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape    = CircleShape,
                        color    = AppGoldColor.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("V", color = AppGoldColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Column {
                        Text("Vous", color = AppDarkGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("À l'instant", color = AppDarkGreen.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
                // Content
                if (content.isNotBlank()) {
                    Text(content, color = AppDarkGreen, fontSize = 14.sp, lineHeight = 20.sp)
                }
                // Media
                if (selectedUri != null) {
                    AsyncImage(
                        model              = selectedUri,
                        contentDescription = "Image de la publication",
                        modifier           = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale       = ContentScale.Crop
                    )
                }
                // Hashtags
                if (hashtags.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(hashtags) { tag ->
                            Text(
                                tag,
                                color      = AppGoldColor,
                                fontWeight = FontWeight.Medium,
                                fontSize   = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick  = onPublish,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
        ) {
            Text("Publier maintenant", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// ─── Step 2: Publishing ───────────────────────────────────────────────────────
@Composable
private fun PublishingStep(progress: Float) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CircularProgressIndicator(
                progress    = { progress },
                modifier    = Modifier.size(72.dp),
                color       = AppGoldColor,
                strokeWidth = 6.dp,
                trackColor  = AppDarkGreen.copy(alpha = 0.15f)
            )
            Text(
                "Publication en cours…",
                color      = AppDarkGreen,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
            Text(
                "${(progress * 100).toInt()}%",
                color    = AppDarkGreen.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
    }
}

// ─── Step 3: Done ─────────────────────────────────────────────────────────────
@Composable
private fun DoneStep(countdown: Int, onFinish: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier            = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape    = CircleShape,
                color    = AppGoldColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.CheckCircle, null,
                        tint     = AppGoldColor,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Text(
                "Publication réussie !",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color      = AppDarkGreen,
                fontSize   = 22.sp
            )
            Text(
                "Votre publication est maintenant visible dans le fil d'actualité.",
                color     = AppDarkGreen.copy(alpha = 0.7f),
                fontSize  = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "Retour dans ${countdown}s…",
                color    = AppDarkGreen.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
            OutlinedButton(
                onClick = onFinish,
                border  = BorderStroke(1.5.dp, AppDarkGreen),
                shape   = RoundedCornerShape(12.dp)
            ) {
                Text("Voir le fil d'actualité", color = AppDarkGreen, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ─── Hashtag Chip ─────────────────────────────────────────────────────────────
@Composable
private fun HashtagChip(tag: String, onRemove: () -> Unit) {
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = AppGoldColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(tag, color = AppDarkGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Retirer",
                tint               = AppDarkGreen.copy(alpha = 0.6f),
                modifier           = Modifier.size(14.dp).clickable { onRemove() }
            )
        }
    }
}
