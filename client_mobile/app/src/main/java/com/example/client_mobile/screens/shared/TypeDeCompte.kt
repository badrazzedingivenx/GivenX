package com.example.client_mobile.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R

@Composable
fun TypeCompteScreen(
    showBackground: Boolean = true,
    onNavigateToLogin: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (showBackground) {
            Image(
                painter            = painterResource(id = R.drawable.background_app),
                contentDescription = null,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            Image(
                painter            = painterResource(id = R.drawable.logo_app),
                contentDescription = "HAQ Logo",
                modifier           = Modifier.size(220.dp),
                contentScale       = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text       = "Bienvenue sur HAQ",
                fontSize   = 30.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color      = AppDarkGreen,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text       = "Choisissez votre espace pour commencer",
                fontSize   = 14.sp,
                fontFamily = FontFamily.Serif,
                color      = AppDarkGreen.copy(alpha = 0.55f),
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Account option cards
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AccountOptionCard(
                    modifier    = Modifier.weight(1f),
                    iconRes     = R.drawable.logo_user,
                    title       = "Utilisateur",
                    description = "Je cherche un conseil juridique",
                    onClick     = { onNavigateToLogin("user") }
                )
                AccountOptionCard(
                    modifier    = Modifier.weight(1f),
                    iconRes     = R.drawable.logo_avocat,
                    title       = "Avocat",
                    description = "Je souhaite offrir mes services",
                    onClick     = { onNavigateToLogin("lawyer") }
                )
            }

            Spacer(modifier = Modifier.weight(1.6f))
        }
    }
}

// ─── Account Option Card ──────────────────────────────────────────────────────
@Composable
fun AccountOptionCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier       = modifier.height(340.dp),
        shape          = RoundedCornerShape(32.dp),
        color          = Color.White.copy(alpha = 0.92f),
        shadowElevation = 4.dp,
        border         = androidx.compose.foundation.BorderStroke(
            1.dp,
            AppGoldColor.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Illustration box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(AppDarkGreen.copy(alpha = 0.04f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier           = Modifier.size(160.dp)
                )
            }

            // Text block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text       = title,
                    fontSize   = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color      = AppDarkGreen
                )
                Text(
                    text       = description,
                    fontSize   = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color      = AppDarkGreen.copy(alpha = 0.55f),
                    textAlign  = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }

            // CTA button  — uses AppDarkGreen with gold accent
            Button(
                onClick          = onClick,
                colors           = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                shape            = RoundedCornerShape(16.dp),
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentPadding   = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.Center
                ) {
                    Text(
                        text       = "Accéder",
                        fontSize   = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier           = Modifier.size(16.dp),
                        tint               = AppGoldColor
                    )
                }
            }
        }
    }
}