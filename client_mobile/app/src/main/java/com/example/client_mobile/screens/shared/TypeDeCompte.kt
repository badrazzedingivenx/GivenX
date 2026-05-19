package com.example.client_mobile.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onNavigateToRegister: (String) -> Unit
) {
    val darkGreen = Color(0xFF1B3124)
    AppScaffold(
        showBackground = showBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Adaptive top spacer: smaller weight shifts the entire block upwards
            Spacer(modifier = Modifier.weight(0.5f))
            
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "HAQ Logo",
                modifier = Modifier.size(235.dp),
                contentScale = ContentScale.Fit
            )
            
            // Spacing optimization: reduced top margin between Logo and Bienvenue
            Spacer(modifier = Modifier.height(0.dp))
            
            Text(
                text = "Bienvenue sur HAQQI",
                fontSize = 32.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = darkGreen, // Color updated to Gold for consistency
                textAlign = TextAlign.Center
            )
            
            // Spacing optimization: tightened space between Title and Subtitle
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Choisissez votre espace pour commencer",
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            // Spacing optimization: brought cards closer to the text block
            Spacer(modifier = Modifier.weight(0.6f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AccountOptionCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.logo_user,
                    title = "Utilisateur",
                    description = "Je cherche un conseil juridique",
                    onClick = { onNavigateToRegister("user") }
                )
                AccountOptionCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.logo_avocat,
                    title = "Avocat",
                    description = "Je souhaite offrir mes services",
                    onClick = { onNavigateToRegister("lawyer") }
                )
            }
            // Adaptive bottom spacer: larger weight firmly pushes content upwards
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

@Composable
fun AccountOptionCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(350.dp),
        shape = RoundedCornerShape(35.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(25.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(170.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3124)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3124)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Accéder",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}            