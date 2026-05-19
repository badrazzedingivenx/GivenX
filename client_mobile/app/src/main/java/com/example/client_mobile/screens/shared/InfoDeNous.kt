package com.example.client_mobile.screens.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun ScreenSwipeInfo(
    onNavigateToLogin: (String) -> Unit
) {
    val onboardingPages = listOf(
        OnboardingPage(
            "Bienvenue sur HAQQI",
            "Votre partenaire juridique moderne, accessible partout et à tout moment.",
            R.drawable.illustat_premier
        ),
        OnboardingPage(
            "Espace Client",
            "Trouvez l'expert idéal pour vos besoins et obtenez des conseils personnalisés.",
            R.drawable.last
        ),
        OnboardingPage(
            "Espace Avocat",
            "Développez votre cabinet et gérez vos dossiers en toute simplicité.",
            R.drawable.illustat_first
        )
    )

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val goldColor = Color(0xFFD4AF37)

    LaunchedEffect(Unit) {
        com.example.client_mobile.network.TokenManager.saveHasSeenOnboarding(true)
    }

    // ── True edge-to-edge root — no Scaffold, no inset padding on the background ──
    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Full-screen background image — draws behind status bar & nav handle
        Image(
            painter = painterResource(id = R.drawable.background_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Layer 2: Dark scrim gradient — improves text legibility on lower half
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.75f)
                        ),
                        startY = 400f
                    )
                )
        )

        // Layer 3: Foreground content — inset only for safe interactive zones
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()          // keeps content away from notch & nav handle
        ) {
            // Swipeable pages — each fills remaining space above the bottom row
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { position ->
                OnboardingPageContent(page = onboardingPages[position])
            }

            // Bottom row: dots + Suivant button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page indicator dots
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 6.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "dotWidth_$index"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) goldColor
                                    else Color.White.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Next / Start button
                val nextInteractionSource = remember { MutableInteractionSource() }
                val isNextPressed by nextInteractionSource.collectIsPressedAsState()
                val nextScale by animateFloatAsState(
                    targetValue = if (isNextPressed) 0.92f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "nextButtonScale"
                )
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onNavigateToLogin("user")
                        }
                    },
                    interactionSource = nextInteractionSource,
                    modifier = Modifier.scale(nextScale),
                    colors = ButtonDefaults.buttonColors(containerColor = goldColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Suivant" else "Commencer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    val goldColor = Color(0xFFD4AF37)
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Illustration - Full Width, Floating effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = page.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Text Content Area — animates in with fade + slide
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(animationSpec = tween(500)) { it / 3 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = page.title,
                    fontSize = 34.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    color = goldColor,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 26.sp
                )
            }
        }
    }
}
