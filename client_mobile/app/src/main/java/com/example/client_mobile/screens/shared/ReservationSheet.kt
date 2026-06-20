package com.example.client_mobile.screens.shared

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Legal Domains List ──────────────────────────────────────────────────────
private val legalDomains = listOf(
    "Généraliste",
    "Droit des Affaires",
    "Droit Pénal",
    "Droit de la Famille",
    "Droit du Travail",
    "Droit Immobilier",
    "Droit Administratif",
    "Droit Commercial",
    "Droit Fiscal",
    "Autre"
)

// ═══════════════════════════════════════════════════════════════════════════════
//  PUBLIC API — Mount this from any parent screen
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Reusable Consultation Reservation BottomSheet.
 *
 * Usage:
 * ```
 * var showReservation by remember { mutableStateOf(false) }
 * if (showReservation) {
 *     ReservationSheet(
 *         lawyerId   = lawyerId,
 *         lawyerName = lawyer.name,
 *         onDismiss  = { showReservation = false },
 *         onPaymentValidated = { data -> /* handle */ }
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationSheet(
    lawyerId: String = "",
    lawyerName: String = "",
    prefillNom: String = "",
    prefillContact: String = "",
    onDismiss: () -> Unit,
    onPaymentValidated: (ReservationData) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Internal step: FORM → PAYMENT ────────────────────────────────────────
    var currentStep by remember { mutableStateOf(ReservationStep.FORM) }

    // ── Form fields ──────────────────────────────────────────────────────────
    var nom by remember { mutableStateOf(prefillNom) }
    var contact by remember { mutableStateOf(prefillContact) }
    var domaine by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf<ConsultationMode?>(null) }

    // ── Payment fields ───────────────────────────────────────────────────────
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // ── Validation flags ─────────────────────────────────────────────────────
    var formSubmitted by remember { mutableStateOf(false) }
    var paymentSubmitted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = AppCreamBg,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AppDarkGreen.copy(alpha = 0.20f))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState == ReservationStep.PAYMENT) {
                    (slideInHorizontally { it / 3 } + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally { -it / 3 } + fadeOut(tween(200)))
                } else {
                    (slideInHorizontally { -it / 3 } + fadeIn(tween(300))) togetherWith
                        (slideOutHorizontally { it / 3 } + fadeOut(tween(200)))
                }
            },
            label = "reservation_step"
        ) { step ->
            when (step) {
                ReservationStep.FORM -> ReservationFormContent(
                    nom = nom,
                    onNomChange = { nom = it },
                    contact = contact,
                    onContactChange = { contact = it },
                    domaine = domaine,
                    onDomaineChange = { domaine = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it },
                    lawyerName = lawyerName,
                    formSubmitted = formSubmitted,
                    onPayClick = {
                        formSubmitted = true
                        if (nom.isNotBlank() && contact.isNotBlank() &&
                            domaine.isNotBlank() && description.isNotBlank() &&
                            selectedMode != null
                        ) {
                            currentStep = ReservationStep.PAYMENT
                        }
                    }
                )

                ReservationStep.PAYMENT -> PaymentFormContent(
                    cardNumber = cardNumber,
                    onCardNumberChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                    expiryDate = expiryDate,
                    onExpiryDateChange = { raw ->
                        val digits = raw.filter { it.isDigit() }
                        expiryDate = when {
                            digits.length <= 2 -> digits
                            else -> digits.take(2) + "/" + digits.drop(2).take(2)
                        }
                    },
                    cvv = cvv,
                    onCvvChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) cvv = it },
                    total = selectedMode?.price ?: 0,
                    paymentSubmitted = paymentSubmitted,
                    onBack = { currentStep = ReservationStep.FORM },
                    onValidate = {
                        paymentSubmitted = true
                        if (cardNumber.length == 16 && expiryDate.length == 5 && cvv.length == 3) {
                            onPaymentValidated(
                                ReservationData(
                                    nom = nom,
                                    contact = contact,
                                    domaine = domaine,
                                    description = description,
                                    mode = selectedMode!!,
                                    cardNumber = cardNumber,
                                    expiryDate = expiryDate,
                                    cvv = cvv,
                                    lawyerId = lawyerId,
                                    lawyerName = lawyerName
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  STEP 1 — Reservation Form
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationFormContent(
    nom: String,
    onNomChange: (String) -> Unit,
    contact: String,
    onContactChange: (String) -> Unit,
    domaine: String,
    onDomaineChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedMode: ConsultationMode?,
    onModeSelected: (ConsultationMode) -> Unit,
    lawyerName: String,
    formSubmitted: Boolean,
    onPayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .navigationBarsPadding()
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        SheetHeader(
            icon = Icons.Default.EventNote,
            title = "Réservation",
            subtitle = if (lawyerName.isNotBlank()) "Consultation avec $lawyerName" else "Nouvelle consultation"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Nom ──────────────────────────────────────────────────────────────
        ReservationTextField(
            value = nom,
            onValueChange = onNomChange,
            label = "Nom complet",
            icon = Icons.Default.Person,
            isError = formSubmitted && nom.isBlank(),
            errorMessage = "Veuillez saisir votre nom"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Contact ──────────────────────────────────────────────────────────
        ReservationTextField(
            value = contact,
            onValueChange = onContactChange,
            label = "Contact (téléphone ou e-mail)",
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone,
            isError = formSubmitted && contact.isBlank(),
            errorMessage = "Veuillez saisir un contact"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Domaine (Dropdown) ───────────────────────────────────────────────
        DomainDropdown(
            selected = domaine,
            onSelected = onDomaineChange,
            isError = formSubmitted && domaine.isBlank()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Description ──────────────────────────────────────────────────────
        ReservationTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Description de votre besoin",
            icon = Icons.Default.Description,
            minLines = 4,
            singleLine = false,
            isError = formSubmitted && description.isBlank(),
            errorMessage = "Veuillez décrire votre besoin"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Documents (placeholder) ──────────────────────────────────────────
        DocumentPlaceholder()

        Spacer(modifier = Modifier.height(20.dp))

        // ── Mode Consultation ────────────────────────────────────────────────
        Text(
            text = "Mode de consultation",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = AppDarkGreen,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        ConsultationMode.entries.forEach { mode ->
            ConsultationModeCard(
                mode = mode,
                isSelected = selectedMode == mode,
                onClick = { onModeSelected(mode) }
            )
            if (mode != ConsultationMode.entries.last()) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (formSubmitted && selectedMode == null) {
            Text(
                text = "Veuillez sélectionner un mode de consultation",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Total ────────────────────────────────────────────────────────────
        TotalBar(price = selectedMode?.price)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Payer Button ─────────────────────────────────────────────────────
        LegalButton(
            text = "Payer",
            onClick = onPayClick
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  STEP 2 — Payment Card Input
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun PaymentFormContent(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit,
    total: Int,
    paymentSubmitted: Boolean,
    onBack: () -> Unit,
    onValidate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .navigationBarsPadding()
    ) {
        // ── Back + Header ────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = AppDarkGreen.copy(alpha = 0.08f),
                onClick = onBack
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = AppDarkGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Retour au formulaire",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = AppDarkGreen.copy(alpha = 0.55f)
            )
        }

        SheetHeader(
            icon = Icons.Default.CreditCard,
            title = "Paiement",
            subtitle = "Saisissez les informations de votre carte"
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Card Visual ──────────────────────────────────────────────────────
        CreditCardVisual(
            cardNumber = cardNumber,
            expiryDate = expiryDate,
            total = total
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Card Number ──────────────────────────────────────────────────────
        ReservationTextField(
            value = formatCardNumber(cardNumber),
            onValueChange = { onCardNumberChange(it.filter { c -> c.isDigit() }) },
            label = "Numéro de carte",
            icon = Icons.Default.CreditCard,
            keyboardType = KeyboardType.Number,
            isError = paymentSubmitted && cardNumber.length != 16,
            errorMessage = "Numéro de carte invalide (16 chiffres)"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Expiry + CVV Row ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ReservationTextField(
                    value = expiryDate,
                    onValueChange = onExpiryDateChange,
                    label = "Expiration (MM/YY)",
                    icon = Icons.Default.CalendarMonth,
                    keyboardType = KeyboardType.Number,
                    isError = paymentSubmitted && expiryDate.length != 5,
                    errorMessage = "Format MM/YY"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                ReservationTextField(
                    value = cvv,
                    onValueChange = onCvvChange,
                    label = "CVV",
                    icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.NumberPassword,
                    isPassword = true,
                    isError = paymentSubmitted && cvv.length != 3,
                    errorMessage = "3 chiffres"
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Total Reminder ───────────────────────────────────────────────────
        TotalBar(price = total)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Validate Button ──────────────────────────────────────────────────
        LegalButton(
            text = "Valider le paiement",
            onClick = onValidate
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  SHARED PRIVATE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

private enum class ReservationStep { FORM, PAYMENT }

/** Animated header with icon badge + title + subtitle. */
@Composable
private fun SheetHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = AppDarkGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = AppDarkGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            color = AppDarkGreen.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}

/** Styled text field matching the app's legal design system. */
@Composable
private fun ReservationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = AppDarkGreen.copy(alpha = 0.65f),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppDarkGreen,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppDarkGreen,
                unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.12f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorContainerColor = Color.White
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/** Exposed dropdown for legal domain selection. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DomainDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Domaine juridique",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = AppDarkGreen.copy(alpha = 0.65f),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Gavel,
                        contentDescription = null,
                        tint = AppDarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                placeholder = {
                    Text(
                        "Sélectionnez un domaine",
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                shape = RoundedCornerShape(16.dp),
                isError = isError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppDarkGreen,
                    unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.12f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                legalDomains.forEach { domain ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                domain,
                                fontFamily = FontFamily.Serif,
                                fontSize = 14.sp,
                                color = AppDarkGreen
                            )
                        },
                        onClick = {
                            onSelected(domain)
                            expanded = false
                        },
                        leadingIcon = {
                            if (domain == selected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AppGoldColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
        if (isError) {
            Text(
                text = "Veuillez sélectionner un domaine",
                color = MaterialTheme.colorScheme.error,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/** Visual placeholder for document attachment. */
@Composable
private fun DocumentPlaceholder() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .clickable { /* TODO: Implement file picker */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = AppGoldColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Ajouter un document",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = AppDarkGreen
                )
                Text(
                    "PDF, JPEG, PNG — 10 Mo max",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = AppDarkGreen.copy(alpha = 0.45f)
                )
            }
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = AppGoldColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/** Selectable consultation mode card with icon, label, and price. */
@Composable
private fun ConsultationModeCard(
    mode: ConsultationMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (mode) {
        ConsultationMode.VIDEO -> Icons.Default.Videocam
        ConsultationMode.CABINET -> Icons.Default.Business
        ConsultationMode.MESSAGE -> Icons.AutoMirrored.Filled.Chat
    }

    val borderColor = if (isSelected) AppGoldColor else AppDarkGreen.copy(alpha = 0.10f)
    val bgColor = if (isSelected) AppGoldColor.copy(alpha = 0.06f) else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = borderColor
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon badge
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) AppDarkGreen else AppDarkGreen.copy(alpha = 0.06f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) AppGoldColor else AppDarkGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Label
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mode.label,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppDarkGreen
                )
            }

            // Price
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) AppDarkGreen else AppDarkGreen.copy(alpha = 0.06f)
            ) {
                Text(
                    text = "${mode.price} DH",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) AppGoldColor else AppDarkGreen
                )
            }

            // Selection indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) AppGoldColor else Color.Transparent
                    )
                    .then(
                        if (!isSelected) Modifier.background(
                            Color.Transparent
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .then(
                                Modifier.background(Color.Transparent)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                                .then(
                                    Modifier.padding(2.dp)
                                )
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(1.5.dp, AppDarkGreen.copy(alpha = 0.25f))
                            ) {}
                        }
                    }
                }
            }
        }
    }
}

/** Displays the dynamic total price. */
@Composable
private fun TotalBar(price: Int?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppDarkGreen
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Total",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
            Text(
                text = if (price != null) "$price DH" else "— DH",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = AppGoldColor
            )
        }
    }
}

/** Mini credit card visual preview. */
@Composable
private fun CreditCardVisual(
    cardNumber: String,
    expiryDate: String,
    total: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AppDarkGreen,
                            Color(0xFF1B4332),
                            AppDarkGreen
                        )
                    )
                )
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = (-30).dp, y = (-30).dp)
                    .background(Color.White.copy(alpha = 0.04f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp)
                    .background(AppGoldColor.copy(alpha = 0.08f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: chip + amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip icon
                    Surface(
                        modifier = Modifier.size(36.dp, 26.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = AppGoldColor.copy(alpha = 0.70f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Memory,
                                contentDescription = null,
                                tint = AppDarkGreen.copy(alpha = 0.40f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        "$total DH",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppGoldColor
                    )
                }

                // Card number
                Text(
                    text = formatCardDisplay(cardNumber),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    letterSpacing = 3.sp,
                    color = Color.White
                )

                // Bottom row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            "EXPIRATION",
                            fontFamily = FontFamily.Serif,
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.45f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = expiryDate.ifBlank { "MM/YY" },
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = if (expiryDate.isBlank()) 0.30f else 0.90f)
                        )
                    }
                    Text(
                        "HAQQI PAY",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AppGoldColor.copy(alpha = 0.65f),
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

/** Format raw digits "1234567890123456" → "1234 5678 9012 3456" for display in the text field. */
private fun formatCardNumber(raw: String): String {
    return raw.chunked(4).joinToString(" ")
}

/** Format raw digits for the card visual — show dots for missing digits. */
private fun formatCardDisplay(raw: String): String {
    val padded = raw.padEnd(16, '•')
    return padded.chunked(4).joinToString("  ")
}
