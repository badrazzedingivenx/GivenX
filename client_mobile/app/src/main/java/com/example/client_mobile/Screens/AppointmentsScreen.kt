package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

<<<<<<< HEAD
// ─── Appointments Screen ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onBack: () -> Unit = {}
) {
    val upcomingAppointments = listOf(
        Triple("Maître Yassine El Amrani", "Droit Pénal", "Mardi 7 Avril — 10:30"),
        Triple("Maître Sara Benali", "Droit de la Famille", "Jeudi 9 Avril — 14:00")
=======
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(onBack: () -> Unit = {}) {
    val appointments = listOf(
        Triple("Maître Yassine El Amrani", "Droit Pénal",        "Mardi 7 Avril — 10:30"),
        Triple("Maître Sara Benali",       "Droit de la Famille","Jeudi 9 Avril — 14:00")
>>>>>>> developer_mobile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mes Rendez-vous",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
<<<<<<< HEAD
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppDarkGreen
                        )
=======
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
>>>>>>> developer_mobile
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

<<<<<<< HEAD
                item { SectionHeader(title = "À venir (${upcomingAppointments.size})") }

                items(upcomingAppointments.size) { idx ->
                    val (name, specialty, datetime) = upcomingAppointments[idx]
                    AppointmentCard(
                        lawyerName = name,
                        specialty = specialty,
                        datetime = datetime
                    )
=======
                item { SectionHeader(title = "À venir (${appointments.size})") }

                items(appointments.size) { idx ->
                    val (name, specialty, datetime) = appointments[idx]
                    AppointmentCard(lawyerName = name, specialty = specialty, datetime = datetime)
>>>>>>> developer_mobile
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.80f),
                        border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.45f)),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = AppDarkGreen.copy(alpha = 0.45f),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "Prendre un nouveau rendez-vous",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AppDarkGreen,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Consultez nos avocats disponibles et réservez votre créneau.",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = AppDarkGreen.copy(alpha = 0.55f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Button(
                                onClick = {},
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.55f))
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Trouver un avocat",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AppGoldColor
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
<<<<<<< HEAD
private fun AppointmentCard(
    lawyerName: String,
    specialty: String,
    datetime: String
) {
=======
private fun AppointmentCard(lawyerName: String, specialty: String, datetime: String) {
>>>>>>> developer_mobile
    val initials = lawyerName
        .removePrefix("Maître ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.40f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen),
                contentAlignment = Alignment.Center
            ) {
<<<<<<< HEAD
                Text(
                    initials,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppGoldColor
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    lawyerName,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppDarkGreen
                )
                Text(
                    specialty,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = AppGoldColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = AppDarkGreen.copy(alpha = 0.45f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        datetime,
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        color = AppDarkGreen.copy(alpha = 0.60f)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF34A853).copy(alpha = 0.12f)
            ) {
                Text(
                    "Confirmé",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color(0xFF34A853),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
=======
                Text(initials, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppGoldColor)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(lawyerName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppDarkGreen)
                Text(specialty, fontFamily = FontFamily.Serif, fontSize = 11.sp, color = AppGoldColor)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.45f), modifier = Modifier.size(12.dp))
                    Text(datetime, fontFamily = FontFamily.Serif, fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.60f))
                }
            }
            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF34A853).copy(alpha = 0.12f)) {
                Text("Confirmé", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF34A853), modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
>>>>>>> developer_mobile
            }
        }
    }
}
