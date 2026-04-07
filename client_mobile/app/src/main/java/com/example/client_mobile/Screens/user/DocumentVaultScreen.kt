package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVaultScreen(onBack: () -> Unit = {}) {
    val documents = listOf(
        Triple("Contrat de Bail.pdf",     "20 Fev 2025", Icons.Default.Description),
        Triple("Piece d'Identite.jpg",    "15 Jan 2025", Icons.Default.Badge),
        Triple("Attestation Travail.pdf", "10 Jan 2025", Icons.Default.Work),
        Triple("Jugement Tribunal.pdf",   "03 Dec 2024", Icons.Default.Gavel)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Coffre-fort Numerique",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 18.sp, color = AppDarkGreen)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = AppDarkGreen,
                        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.55f)),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = AppGoldColor.copy(alpha = 0.18f),
                                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null,
                                        tint = AppGoldColor, modifier = Modifier.size(24.dp))
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ajouter un document", fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                Text("PDF, JPG, PNG jusqu'a 10 MB", fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp, color = Color.White.copy(alpha = 0.60f))
                            }
                            Icon(Icons.Default.Add, contentDescription = null,
                                tint = AppGoldColor, modifier = Modifier.size(22.dp))
                        }
                    }
                }
                item { SectionHeader(title = "Documents (${documents.size})") }
                items(documents.size) { idx ->
                    val (name, date, icon) = documents[idx]
                    DocumentCard(name = name, date = date, icon = icon)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun DocumentCard(name: String, date: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = AppDarkGreen.copy(alpha = 0.08f),
                border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null,
                        tint = AppDarkGreen, modifier = Modifier.size(22.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(name, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, color = AppDarkGreen)
                Text("Ajoute le $date", fontFamily = FontFamily.Serif,
                    fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.45f))
            }
            Icon(Icons.Default.MoreVert, contentDescription = "Options",
                tint = AppDarkGreen.copy(alpha = 0.40f), modifier = Modifier.size(18.dp))
        }
    }
}
