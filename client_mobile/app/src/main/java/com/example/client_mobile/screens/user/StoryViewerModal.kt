package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Each story auto-advances after this many milliseconds
private const val STORY_DURATION_MS = 5_000

// ─── Full-Screen Story Viewer ─────────────────────────────────────────────────
@Composable
fun StoryViewerModal(
    stories: List<LegalStory>,
    startIndex: Int,
    onDismiss: () -> Unit
) {
    if (stories.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(startIndex.coerceIn(stories.indices)) }
    val story = stories[currentIndex]

    // ── Auto-advance progress animation ───────────────────────────────────────
    val progress = remember { Animatable(0f) }
    LaunchedEffect(currentIndex) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = STORY_DURATION_MS, easing = LinearEasing)
        )
        // Animation completed naturally → go to next or close
        if (currentIndex < stories.lastIndex) currentIndex++
        else onDismiss()
    }

    // ── Swipe-down tracking ────────────────────────────────────────────────────
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    // Compute initials from lawyerName (strip honorific prefixes)
    val initials = story.lawyerName
        .removePrefix("Maître ")
        .removePrefix("M. ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = dragOffsetY }
                // Swipe down → dismiss; ignore upward swipes
                .pointerInput("swipe") {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragOffsetY > 150f) onDismiss()
                            else dragOffsetY = 0f
                        },
                        onDragCancel = { dragOffsetY = 0f },
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount > 0f) {
                                dragOffsetY += dragAmount
                                change.consume()
                            }
                        }
                    )
                }
                // Tap left half → previous; tap right half → next
                .pointerInput(currentIndex, stories.size) {
                    detectTapGestures(onTap = { offset ->
                        dragOffsetY = 0f
                        if (offset.x < size.width / 2f) {
                            if (currentIndex > 0) currentIndex--
                            else onDismiss()
                        } else {
                            if (currentIndex < stories.lastIndex) currentIndex++
                            else onDismiss()
                        }
                    })
                }
        ) {
            // ── Background: dark green → near-black gradient ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(AppDarkGreen, Color(0xFF0A1A10))
                        )
                    )
            )

            // ── Center content: large initials ring + specialty label ─────────
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(AppGoldColor.copy(alpha = 0.12f))
                        .border(2.5.dp, AppGoldColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 52.sp,
                        color = AppGoldColor
                    )
                }
                Text(
                    story.specialty.uppercase(),
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppGoldColor.copy(alpha = 0.65f),
                    letterSpacing = 3.sp
                )
            }

            // ── Top HUD: segmented progress bars + lawyer info + close ────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // One thin bar per story; filled = past, animating = current, empty = future
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stories.forEachIndexed { index, _ ->
                        val fraction = when {
                            index < currentIndex  -> 1f
                            index == currentIndex -> progress.value
                            else                  -> 0f
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.30f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Lawyer avatar + name/specialty + close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AppGoldColor.copy(alpha = 0.18f))
                            .border(1.5.dp, AppGoldColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            initials,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AppGoldColor
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            story.lawyerName,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            story.specialty,
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ── Bottom overlay: full name + domain ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    )
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        story.lawyerName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Text(
                        "Avocat — ${story.specialty}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}