package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
<<<<<<< HEAD
import androidx.compose.foundation.background
=======
>>>>>>> developer_mobile
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
import androidx.compose.ui.geometry.Offset
<<<<<<< HEAD
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Billing Screen ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    onBack: () -> Unit = {}
) {
    val paidAmount = 2400f
    val pendingAmount = 800f
    val total = paidAmount + pendingAmount

    val invoices = listOf(
        BillingInvoice("FAC-2025-001", "Maître Yassine El Amrani", "1 200 MAD", "Payée", true),
        BillingInvoice("FAC-2025-002", "Maître Sara Benali", "1 200 MAD", "Payée", true),
        BillingInvoice("FAC-2025-003", "Maître Khalid Tazi", "800 MAD", "En attente", false)
=======
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class BillingInvoice(
    val number: String,
    val lawyerName: String,
    val amount: String,
    val status: String,
    val isPaid: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(onBack: () -> Unit = {}) {
    val paidAmount   = 2400f
    val pendingAmount = 800f
    val total        = paidAmount + pendingAmount

    val invoices = listOf(
        BillingInvoice("FAC-2025-001", "Maître Yassine El Amrani", "1 200 MAD", "Payée",      true),
        BillingInvoice("FAC-2025-002", "Maître Sara Benali",       "1 200 MAD", "Payée",      true),
        BillingInvoice("FAC-2025-003", "Maître Khalid Tazi",       "800 MAD",   "En attente", false)
>>>>>>> developer_mobile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Facturation",
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
                // ── Summary Card ───────────────────────────────────────────────
=======
                // Summary card
>>>>>>> developer_mobile
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = AppDarkGreen,
                        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.45f)),
                        shadowElevation = 6.dp
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                drawCircle(
                                    color = Color(0xFFD4AF37).copy(alpha = 0.07f),
                                    radius = 180.dp.toPx(),
                                    center = Offset(size.width * 0.90f, -size.height * 0.20f)
                                )
                            }
                            Column(
<<<<<<< HEAD
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "Résumé de Facturation",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = AppGoldColor
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    BillingStat("Total Facturé", "${total.toInt()} MAD", Color.White)
                                    BillingStat("Payé", "${paidAmount.toInt()} MAD", Color(0xFF34A853))
                                    BillingStat("En attente", "${pendingAmount.toInt()} MAD", AppGoldColor)
                                }
                                // Progress bar
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
=======
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("Résumé de Facturation", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppGoldColor)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    BillingStat("Total Facturé", "${total.toInt()} MAD", Color.White)
                                    BillingStat("Payé",          "${paidAmount.toInt()} MAD", Color(0xFF34A853))
                                    BillingStat("En attente",    "${pendingAmount.toInt()} MAD", AppGoldColor)
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
>>>>>>> developer_mobile
                                        Text("Progression", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = Color.White.copy(alpha = 0.60f))
                                        Text("${((paidAmount / total) * 100).toInt()}%", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AppGoldColor)
                                    }
                                    LinearProgressIndicator(
                                        progress = { paidAmount / total },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                        color = Color(0xFF34A853),
                                        trackColor = Color.White.copy(alpha = 0.18f)
                                    )
                                }
                            }
                        }
                    }
                }

                item { SectionHeader(title = "Historique des Factures") }

                items(invoices.size) { idx ->
                    InvoiceCard(invoice = invoices[idx])
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

<<<<<<< HEAD
private data class BillingInvoice(
    val number: String,
    val lawyerName: String,
    val amount: String,
    val status: String,
    val isPaid: Boolean
)

=======
>>>>>>> developer_mobile
@Composable
private fun BillingStat(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = valueColor)
        Text(label, fontFamily = FontFamily.Serif, fontSize = 10.sp, color = Color.White.copy(alpha = 0.55f))
    }
}

@Composable
private fun InvoiceCard(invoice: BillingInvoice) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
<<<<<<< HEAD
        border = BorderStroke(
            0.5.dp,
            if (invoice.isPaid) AppDarkGreen.copy(alpha = 0.10f) else AppGoldColor.copy(alpha = 0.45f)
        ),
=======
        border = BorderStroke(0.5.dp, if (invoice.isPaid) AppDarkGreen.copy(alpha = 0.10f) else AppGoldColor.copy(alpha = 0.45f)),
>>>>>>> developer_mobile
        shadowElevation = if (invoice.isPaid) 2.dp else 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (invoice.isPaid) Color(0xFF34A853).copy(alpha = 0.10f) else AppGoldColor.copy(alpha = 0.12f),
                border = BorderStroke(0.5.dp, if (invoice.isPaid) Color(0xFF34A853).copy(alpha = 0.25f) else AppGoldColor.copy(alpha = 0.35f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (invoice.isPaid) Icons.Default.CheckCircle else Icons.Default.HourglassBottom,
                        contentDescription = null,
                        tint = if (invoice.isPaid) Color(0xFF34A853) else AppGoldColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
<<<<<<< HEAD
                Text(
                    invoice.number,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = AppDarkGreen
                )
                Text(
                    invoice.lawyerName,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = AppDarkGreen.copy(alpha = 0.55f)
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    invoice.amount,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppDarkGreen
                )
=======
                Text(invoice.number,    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppDarkGreen)
                Text(invoice.lawyerName, fontFamily = FontFamily.Serif, fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.55f))
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(invoice.amount, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppDarkGreen)
>>>>>>> developer_mobile
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (invoice.isPaid) Color(0xFF34A853).copy(alpha = 0.12f) else AppGoldColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        invoice.status,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (invoice.isPaid) Color(0xFF34A853) else AppGoldColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
