package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVaultScreen(onBack: () -> Unit = {}) {

    // ── Single source of truth from shared DocumentRepository ──────────────
    val documents = DocumentRepository.documents

    var searchQuery        by remember { mutableStateOf("") }
    var showAddDialog      by remember { mutableStateOf(false) }
    var editTarget         by remember { mutableStateOf<VaultDocument?>(null) }
    var deleteTarget       by remember { mutableStateOf<VaultDocument?>(null) }

    val filtered = remember(searchQuery, documents.toList()) {
        if (searchQuery.isBlank()) documents.toList()
        else documents.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
    }

    // ── Dialogs ────────────────────────────────────────────────────────────
    if (showAddDialog) {
        AddDocumentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                if (name.isNotBlank()) DocumentRepository.add(name)
                showAddDialog = false
            }
        )
    }

    editTarget?.let { doc ->
        EditDocumentDialog(
            currentName = doc.name,
            onDismiss   = { editTarget = null },
            onConfirm   = { newName ->
                if (newName.isNotBlank()) DocumentRepository.rename(doc.id, newName)
                editTarget = null
            }
        )
    }

    deleteTarget?.let { doc ->
        DeleteConfirmDialog(
            documentName = doc.name,
            onDismiss    = { deleteTarget = null },
            onConfirm    = {
                DocumentRepository.delete(doc.id)
                deleteTarget = null
            }
        )
    }

    // ── Layout ─────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Coffre-fort Numérique",
                        fontFamily   = FontFamily.Serif,
                        fontWeight   = FontWeight.Bold,
                        fontSize     = 18.sp,
                        color        = AppDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppDarkGreen
                        )
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
                item { Spacer(Modifier.height(4.dp)) }

                // ── Search bar ────────────────────────────────────────────
                item { VaultSearchBar(query = searchQuery, onQueryChange = { searchQuery = it }) }

                // ── Add button ────────────────────────────────────────────
                item {
                    Surface(
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(18.dp),
                        color         = AppDarkGreen,
                        border        = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.55f)),
                        shadowElevation = 4.dp,
                        onClick       = { showAddDialog = true }
                    ) {
                        Row(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment   = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape    = RoundedCornerShape(14.dp),
                                color    = AppGoldColor.copy(alpha = 0.18f),
                                border   = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint     = AppGoldColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Ajouter un document",
                                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                                    fontSize   = 14.sp, color = Color.White
                                )
                                Text(
                                    "PDF, JPG, PNG jusqu'à 10 MB",
                                    fontFamily = FontFamily.Serif,
                                    fontSize   = 11.sp, color = Color.White.copy(alpha = 0.60f)
                                )
                            }
                            Icon(
                                Icons.Default.Add, contentDescription = null,
                                tint = AppGoldColor, modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // ── List header ───────────────────────────────────────────
                item {
                    SectionHeader(
                        title = if (searchQuery.isBlank()) "Documents (${documents.size})"
                                else "Résultats (${filtered.size})"
                    )
                }

                // ── Document cards ────────────────────────────────────────
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment  = Alignment.Center
                        ) {
                            Text(
                                "Aucun document trouvé",
                                fontFamily = FontFamily.Serif,
                                color      = AppDarkGreen.copy(alpha = 0.45f),
                                fontSize   = 14.sp
                            )
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { doc ->
                        DocumentCard(
                            doc      = doc,
                            onEdit   = { editTarget   = doc },
                            onDelete = { deleteTarget = doc }
                        )
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

@Composable
private fun VaultSearchBar(query: String, onQueryChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        modifier      = Modifier.fillMaxWidth(),
        placeholder   = {
            Text(
                "Rechercher un document…",
                fontFamily = FontFamily.Serif,
                fontSize   = 13.sp,
                color      = AppDarkGreen.copy(alpha = 0.45f)
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = AppGoldColor)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Effacer", tint = AppDarkGreen.copy(alpha = 0.55f))
                }
            }
        },
        singleLine    = true,
        shape         = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AppGoldColor,
            unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.20f),
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor          = AppDarkGreen
        )
    )
}

// ─── Document Card ────────────────────────────────────────────────────────────

@Composable
private fun DocumentCard(
    doc     : VaultDocument,
    onEdit  : () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White,
        border          = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon badge
            Surface(
                modifier = Modifier.size(44.dp),
                shape    = RoundedCornerShape(12.dp),
                color    = AppDarkGreen.copy(alpha = 0.08f),
                border   = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = doc.icon,
                        contentDescription = null,
                        tint               = AppDarkGreen,
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }

            // Name + date
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    doc.name,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp, color = AppDarkGreen
                )
                Text(
                    "Ajouté le ${doc.addedDate}",
                    fontFamily = FontFamily.Serif,
                    fontSize   = 11.sp, color = AppDarkGreen.copy(alpha = 0.45f)
                )
            }

            // Overflow menu
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint               = AppDarkGreen.copy(alpha = 0.50f),
                        modifier           = Modifier.size(20.dp)
                    )
                }
                DropdownMenu(
                    expanded        = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text         = { Text("Modifier", fontFamily = FontFamily.Serif, fontSize = 13.sp) },
                        leadingIcon  = { Icon(Icons.Default.Edit, contentDescription = null, tint = AppDarkGreen, modifier = Modifier.size(17.dp)) },
                        onClick      = { menuExpanded = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text        = { Text("Supprimer", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = Color(0xFFD32F2F)) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(17.dp)) },
                        onClick     = { menuExpanded = false; onDelete() }
                    )
                }
            }
        }
    }
}

// ─── Add Dialog ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDocumentDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape           = RoundedCornerShape(24.dp),
            color           = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Header
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape    = RoundedCornerShape(12.dp),
                        color    = AppDarkGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text(
                        "Ajouter un document",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp, color = AppDarkGreen
                    )
                }

                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.08f))

                // Name field
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Nom du document", fontFamily = FontFamily.Serif, fontSize = 12.sp) },
                    placeholder   = { Text("ex: Contrat de Bail.pdf", fontFamily = FontFamily.Serif, fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = AppGoldColor,
                        unfocusedBorderColor  = AppDarkGreen.copy(alpha = 0.20f),
                        cursorColor           = AppDarkGreen,
                        focusedLabelColor     = AppGoldColor,
                        unfocusedLabelColor   = AppDarkGreen.copy(alpha = 0.55f)
                    )
                )

                // Simulated file picker hint
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    color    = AppDarkGreen.copy(alpha = 0.05f),
                    border   = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier              = Modifier.padding(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(18.dp))
                        Text(
                            "Sélectionner un fichier (PDF, JPG, PNG)",
                            fontFamily = FontFamily.Serif,
                            fontSize   = 12.sp,
                            color      = AppDarkGreen.copy(alpha = 0.55f)
                        )
                    }
                }

                // Buttons
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick  = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.25f))
                    ) { Text("Annuler", fontFamily = FontFamily.Serif, color = AppDarkGreen, fontSize = 13.sp) }

                    Button(
                        onClick  = { onConfirm(name) },
                        enabled  = name.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = AppDarkGreen,
                            disabledContainerColor = AppDarkGreen.copy(alpha = 0.35f)
                        )
                    ) { Text("Ajouter", fontFamily = FontFamily.Serif, color = AppGoldColor, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }
        }
    }
}

// ─── Edit Dialog ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDocumentDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape           = RoundedCornerShape(24.dp),
            color           = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape    = RoundedCornerShape(12.dp),
                        color    = AppDarkGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text(
                        "Modifier le nom",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp, color = AppDarkGreen
                    )
                }

                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.08f))

                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it },
                    label         = { Text("Nouveau nom", fontFamily = FontFamily.Serif, fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = AppGoldColor,
                        unfocusedBorderColor  = AppDarkGreen.copy(alpha = 0.20f),
                        cursorColor           = AppDarkGreen,
                        focusedLabelColor     = AppGoldColor,
                        unfocusedLabelColor   = AppDarkGreen.copy(alpha = 0.55f)
                    )
                )

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick  = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.25f))
                    ) { Text("Annuler", fontFamily = FontFamily.Serif, color = AppDarkGreen, fontSize = 13.sp) }

                    Button(
                        onClick  = { onConfirm(name) },
                        enabled  = name.isNotBlank() && name != currentName,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = AppDarkGreen,
                            disabledContainerColor = AppDarkGreen.copy(alpha = 0.35f)
                        )
                    ) { Text("Enregistrer", fontFamily = FontFamily.Serif, color = AppGoldColor, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }
        }
    }
}

// ─── Delete Confirmation Dialog ───────────────────────────────────────────────

@Composable
private fun DeleteConfirmDialog(documentName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape           = RoundedCornerShape(24.dp),
            color           = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape    = RoundedCornerShape(12.dp),
                        color    = Color(0xFFD32F2F).copy(alpha = 0.10f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        }
                    }
                    Text(
                        "Supprimer le document",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp, color = AppDarkGreen
                    )
                }

                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.08f))

                Text(
                    "Voulez-vous vraiment supprimer\n\"$documentName\" ?\nCette action est irréversible.",
                    fontFamily  = FontFamily.Serif,
                    fontSize    = 13.sp,
                    color       = AppDarkGreen.copy(alpha = 0.75f),
                    lineHeight  = 20.sp
                )

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick  = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        border   = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.25f))
                    ) { Text("Annuler", fontFamily = FontFamily.Serif, color = AppDarkGreen, fontSize = 13.sp) }

                    Button(
                        onClick  = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) { Text("Supprimer", fontFamily = FontFamily.Serif, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }
        }
    }
}
