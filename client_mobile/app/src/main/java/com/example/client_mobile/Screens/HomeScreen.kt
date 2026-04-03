package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R

// ─── Data Model ───────────────────────────────────────────────────────────────
data class LegalCategory(
    val title: String,
    val icon: ImageVector
)

// ─── Home Screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCases: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        LegalCategory("Droit de la\nFamille", Icons.Default.Groups),
        LegalCategory("Droit des\nAffaires", Icons.Default.BusinessCenter),
        LegalCategory("Droit\nPénal", Icons.Default.Gavel),
        LegalCategory("Droit\nImmobilier", Icons.Default.Apartment),
        LegalCategory("Droit du\nTravail", Icons.Default.Work),
        LegalCategory("Droit\nFiscal", Icons.Default.AccountBalance)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_app),
                            contentDescription = "Logo",
                            modifier = Modifier.size(330.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = AppDarkGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            UserBottomBar(selectedTab = selectedTab) { index ->
                selectedTab = index
                when (index) {
                    1 -> onNavigateToCases()
                    3 -> onNavigateToProfile()
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── Hero Section ─────────────────────────────────────────────
                HomeHeroSection(onAbout = onNavigateToAbout)

                // ── Search Bar ───────────────────────────────────────────────
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Rechercher un avocat...",
                            fontFamily = FontFamily.Serif,
                            color = AppDarkGreen.copy(alpha = 0.45f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = AppDarkGreen
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppDarkGreen,
                        unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.25f),
                        focusedContainerColor = Color.White.copy(alpha = 0.92f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.92f)
                    ),
                    singleLine = true
                )

                // ── Service Grid ─────────────────────────────────────────────
                SectionHeader(title = "Domaines Juridiques")
                ServiceCategoryGrid(categories = categories)

                // ── Quick Stats ──────────────────────────────────────────────
                SectionHeader(title = "En chiffres")
                HomeQuickStats()

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ─── Hero Section ─────────────────────────────────────────────────────────────
@Composable
private fun HomeHeroSection(onAbout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(195.dp),
        shape = RoundedCornerShape(24.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f)),
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative gold circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.08f),
                    radius = 200.dp.toPx(),
                    center = Offset(size.width * 0.88f, -size.height * 0.15f)
                )
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.05f),
                    radius = 140.dp.toPx(),
                    center = Offset(0f, size.height * 1.05f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Votre droit,\nnos experts.",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp,
                        color = Color.White,
                        lineHeight = 33.sp
                    )
                    Text(
                        text = "Trouvez l'avocat qu'il vous faut en quelques secondes.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.68f),
                        lineHeight = 19.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGoldColor),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Consulter",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AppDarkGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AppDarkGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = onAbout,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.60f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "À Propos",
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─── Service Category Grid ────────────────────────────────────────────────────
@Composable
private fun ServiceCategoryGrid(categories: List<LegalCategory>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(212.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(categories) { category ->
            CategoryCard(category = category)
        }
    }
}

@Composable
private fun CategoryCard(category: LegalCategory) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = AppDarkGreen
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = AppGoldColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Text(
                text = category.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = AppDarkGreen,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

// ─── Quick Stats Row ──────────────────────────────────────────────────────────
@Composable
private fun HomeQuickStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple(Icons.Default.VerifiedUser, "240+", "Avocats certifiés"),
            Triple(Icons.Default.Gavel, "1 200+", "Dossiers traités"),
            Triple(Icons.Default.Star, "4.8 / 5", "Satisfaction")
        ).forEach { (icon, value, label) ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = AppDarkGreen,
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.35f)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        value,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        label,
                        color = Color.White.copy(alpha = 0.62f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
