package com.example.homeworkmaxxing.ui.routine

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.util.ValidationRules
import java.util.Calendar

// ─────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutineFormScreen(
    viewModel: RoutineFormViewModel,
    existingRoutine: Routine? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coursList by viewModel.coursList.collectAsStateWithLifecycle()
    val maxSelectableDateMillis by viewModel.maxSelectableDateMillis.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isEditing = existingRoutine != null
    var showDeleteConfirm by remember { mutableStateOf(false) }

    //Charger la routine existante
    LaunchedEffect(existingRoutine) {
        existingRoutine?.let { viewModel.loadRoutine(it) }
    }

    //Naviguer après sauvegarde / suppression
    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onSaved() }
    LaunchedEffect(uiState.isDeleted) { if (uiState.isDeleted) onDelete?.invoke() }

    //Date picker
    if (uiState.showDatePicker) {
        val cal = Calendar.getInstance()
        val startOfTodayMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        DatePickerDialog(
            context,
            { _, year, month, day -> viewModel.onDateSelected(year, month + 1, day) },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = startOfTodayMillis
            maxSelectableDateMillis?.let { maxDate ->
                datePicker.maxDate = maxDate
            }
            setOnDismissListener { viewModel.dismissDatePicker() }
        }.show()
    }

    //Time picker
    if (uiState.showTimePicker) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute -> viewModel.onTimeSelected(hour, minute) },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).apply {
            setOnDismissListener { viewModel.dismissTimePicker() }
        }.show()
    }

    //Dialog de confirmation de suppression
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer la routine") },
            text = { Text("Cette action est irréversible. Voulez-vous vraiment supprimer cette routine ?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.onDelete()
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            RoutineFormTopBar(
                isEditing = isEditing,
                onBack = onBack,
                onDelete = if (isEditing && onDelete != null) {
                    { showDeleteConfirm = true }
                } else null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            //Nom
            OutlinedTextField(
                value = uiState.nom,
                onValueChange = viewModel::onNomChange,
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("${uiState.nom.length}/${ValidationRules.MAX_ROUTINE_NOM_LENGTH}")
                },
                trailingIcon = {
                    if (uiState.nom.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onNomChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            //Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                supportingText = {
                    Text("${uiState.description.length}/${ValidationRules.MAX_ROUTINE_DESCRIPTION_LENGTH}")
                },
                trailingIcon = {
                    if (uiState.description.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onDescriptionChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            //Date & Heure
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.dateText,
                    onValueChange = {},
                    label = { Text("Date") },
                    placeholder = { Text("JJ/MM/AA") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleDatePicker) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Choisir date")
                        }
                    }
                )
                OutlinedTextField(
                    value = uiState.heureText,
                    onValueChange = {},
                    label = { Text("Heure") },
                    placeholder = { Text("HH:MM") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleTimePicker) {
                            Icon(Icons.Default.Schedule, contentDescription = "Choisir heure")
                        }
                    }
                )
            }

            Spacer(Modifier.height(4.dp))

            //Répétition
            FormDropdownRow(
                label = "Répétition",
                value = uiState.repetabilite.toLabel(),
                leadingIcon = Icons.Default.Refresh,
                expanded = uiState.showRepetitionDropdown,
                onToggle = viewModel::toggleRepetitionDropdown
            ) {
                Repetabilite.entries.forEach { rep ->
                    DropdownMenuItem(
                        text = { Text(rep.toLabel()) },
                        onClick = { viewModel.onRepetabiliteSelected(rep) },
                        trailingIcon = if (uiState.repetabilite == rep) {
                            {
                                Icon(
                                    Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            //Catégorie
            FormDropdownRow(
                label = "Catégorie",
                value = uiState.categorie?.toLabel() ?: "",
                leadingIcon = Icons.Default.Category,
                expanded = uiState.showCategorieDropdown,
                onToggle = viewModel::toggleCategorieDropdown
            ) {
                CategorieRoutine.entries.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.toLabel()) },
                        onClick = { viewModel.onCategorieSelected(cat) },
                        trailingIcon = if (uiState.categorie == cat) {
                            {
                                Icon(
                                    Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            //Cours
            FormDropdownRow(
                label = "Cours",
                value = coursList.find { it.id == uiState.coursId }?.nom ?: "",
                leadingIcon = Icons.Default.School,
                expanded = uiState.showCoursDropdown,
                onToggle = viewModel::toggleCoursDropdown
            ) {
                DropdownMenuItem(
                    text = { Text("Aucun cours") },
                    onClick = { viewModel.onCoursSelected(null) },
                    trailingIcon = if (uiState.coursId == null) {
                        {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    } else null
                )
                coursList.forEach { cours ->
                    DropdownMenuItem(
                        text = { Text(cours.nom) },
                        onClick = { viewModel.onCoursSelected(cours.id) },
                        trailingIcon = if (uiState.coursId == cours.id) {
                            {
                                Icon(
                                    Icons.Default.Circle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(Modifier.height(20.dp))

            //Priorité
            PrioriteToggleRow(
                selected = uiState.priorite,
                onSelected = viewModel::onPrioriteSelected
            )

            Spacer(Modifier.height(20.dp))

            //Message d'erreur
            uiState.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = viewModel::onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4)
                )
            ) {
                Text(
                    text = if (isEditing) "Enregistrer les modifications" else "Créer la routine",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────

@Composable
private fun RoutineFormTopBar(
    isEditing: Boolean,
    onBack: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFB388FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = if (isEditing) "Modifier la routine" else "Nouvelle routine",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//Dropdown
// ─────────────────────────────────────────────

@Composable
private fun FormDropdownRow(
    label: String,
    value: String,
    leadingIcon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(modifier = Modifier.weight(1f)) {
                if (value.isNotEmpty()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onToggle
        ) {
            content()
        }
    }
}

// ─────────────────────────────────────────────
// Priorité
// ─────────────────────────────────────────────

@Composable
private fun PrioriteToggleRow(
    selected: Priorite?,
    onSelected: (Priorite) -> Unit
) {
    val options = listOf(
        Priorite.BASSE   to "Basse",
        Priorite.HAUTE   to "Haute",
        Priorite.URGENTE to "Urgente"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF7F4FB),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Priorité",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { (priorite, label) ->
                    PrioriteIconButton(
                        priorite = priorite,
                        label = label,
                        isSelected = selected == priorite,
                        onClick = { onSelected(priorite) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrioriteIconButton(
    priorite: Priorite,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = priorite.toColor()
    val bgColor = if (isSelected) activeColor.copy(alpha = 0.12f) else Color(0xFFF3EDF7)
    val borderColor = if (isSelected) activeColor else Color(0xFFCAC4D0)
    val iconColor = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
    val labelColor = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = bgColor, shape = CircleShape)
                .border(width = 1.5.dp, color = borderColor, shape = CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────
// Extensions
// ─────────────────────────────────────────────

fun Repetabilite.toLabel() = when (this) {
    Repetabilite.AUCUNE       -> "Ne pas répéter"
    Repetabilite.QUOTIDIEN    -> "Chaque jour"
    Repetabilite.HEBDOMADAIRE -> "Chaque semaine"
    Repetabilite.MENSUEL      -> "Chaque mois"
}

fun CategorieRoutine.toLabel() = when (this) {
    CategorieRoutine.EXAMEN  -> "Examen"
    CategorieRoutine.DEVOIR  -> "Devoir"
    CategorieRoutine.PROJET  -> "Projet"
    CategorieRoutine.ETUDE   -> "Étude"
    CategorieRoutine.AUTRE   -> "Autre"
}

fun Priorite.toLabel() = when (this) {
    Priorite.BASSE   -> "Basse"
    Priorite.MOYENNE -> "Moyenne"
    Priorite.HAUTE   -> "Haute"
    Priorite.URGENTE -> "Urgente"
}

fun Priorite.toColor() = when (this) {
    Priorite.BASSE   -> Color(0xFF00897B)
    Priorite.MOYENNE -> Color(0xFF1E88E5)
    Priorite.HAUTE   -> Color(0xFFFB8C00)
    Priorite.URGENTE -> Color(0xFFE53935)
}
