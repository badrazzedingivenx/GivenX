package com.example.client_mobile.screens.user

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.screens.shared.AppGoldColor
import com.example.client_mobile.screens.shared.AppDarkGreen

private const val STORY_DURATION_MS = 5000

@Composable
fun HaqqiStoryViewer(
    stories: List<StoryDto>,
    startIndex: Int,
    onDismiss: () -> Unit
) {
    if (stories.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(startIndex.coerceIn(stories.indices)) }
    val story = stories[currentIndex]

    // Auto-advance progress animation
    val progress = remember { Animatable(0f) }
    LaunchedEffect(currentIndex) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = STORY_DURATION_MS, easing = LinearEasing)
        )
        // Animation finished -> next story or close
        if (currentIndex < stories.lastIndex) currentIndex++
        else onDismiss()
    }

    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    // Parse (Prefix + Name) for Insta-style header
    val parts = story.lawyerName.split(" ", limit = 2)
    val prefix = if(parts.isNotEmpty()) parts[0] else "Me."
    val lastName = if(parts.size > 1) parts[1] else ""

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
                .background(Color.Black)
                .graphicsLayer { translationY = dragOffsetY }
                // Swipe down tracker
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
                // Tap left = Previous, Tap right = Next
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
            // Main Story Content (Photo)
            AsyncImage(
                model = story.imageUrl,
                contentDescription = "Story of ${story.lawyerName}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay at the top purely for text visibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )

            // Top HUD: Segmented progress bars + Insta-style Lawyer info + Close
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                // Segmented Progress Bar
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

                Spacer(Modifier.height(16.dp))

                // Lawyer Avatar (Top left) + Name + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .border(1.5.dp, AppGoldColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (story.lawyerAvatar.isNotBlank()) {
                            AsyncImage(
                                model = story.lawyerAvatar,
                                contentDescription = story.lawyerName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    
                    // Name Format (Prefix + LastName)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                    append("$prefix ")
                                }
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(lastName)
                                }
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        // Time e.g "5h"
                        Text(
                            text = story.timeLeft,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Light,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Close Box
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
