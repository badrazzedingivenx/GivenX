package com.example.client_mobile.screens.lawyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.screens.shared.AppDarkGreen
import com.example.client_mobile.screens.shared.AppGoldColor
import com.example.client_mobile.screens.shared.CreatorRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerCreatorManagementScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Stories", "Reels", "Lives")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Gestion Créateur",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AppDarkGreen,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AppGoldColor
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> StoriesList()
                    1 -> ReelsList()
                    2 -> LivesList()
                }
            }
        }
    }
}

@Composable
fun StoriesList() {
    val stories = CreatorRepository.stories
    if (stories.isEmpty()) {
        EmptyState(Icons.Default.History, "Aucune story publiée")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stories) { story ->
                CreatorContentCard(
                    title = "Story #${story.id.toString().takeLast(4)}",
                    subtitle = "Publié par ${story.lawyerName}",
                    icon = Icons.Default.History,
                    onDelete = { CreatorRepository.deleteStory(story.id) }
                )
            }
        }
    }
}

@Composable
fun ReelsList() {
    val reels = CreatorRepository.reels
    if (reels.isEmpty()) {
        EmptyState(Icons.Default.PlayCircle, "Aucun reel publié")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reels) { reel ->
                CreatorContentCard(
                    title = reel.title,
                    subtitle = "Reel • ${reel.specialty}",
                    icon = Icons.Default.PlayCircle,
                    onDelete = { CreatorRepository.deleteReel(reel.id) }
                )
            }
        }
    }
}

@Composable
fun LivesList() {
    val lives = CreatorRepository.liveSessions
    if (lives.isEmpty()) {
        EmptyState(Icons.Default.LiveTv, "Aucun direct enregistré")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(lives) { live ->
                CreatorContentCard(
                    title = live.topic,
                    subtitle = if (live.isLive) "En direct actuellement" else "Terminé",
                    icon = Icons.Default.LiveTv,
                    onDelete = { CreatorRepository.deleteLive(live.id) },
                    isLive = live.isLive
                )
            }
        }
    }
}

@Composable
fun CreatorContentCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onDelete: () -> Unit,
    isLive: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AppDarkGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AppDarkGreen)
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppDarkGreen)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = Color.Gray, fontSize = 16.sp)
    }
}
