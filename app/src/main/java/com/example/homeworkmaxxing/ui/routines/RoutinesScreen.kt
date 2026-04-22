package com.example.homeworkmaxxing.ui.routines

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homeworkmaxxing.data.model.CategorieRoutine
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Priorite
import com.example.homeworkmaxxing.data.model.Repetabilite
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.ui.routine.toColor
import com.example.homeworkmaxxing.ui.routine.toLabel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoutinesPage(
    viewModel: RoutinesViewModel,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddRoutineClick: () -> Unit,
    onRoutineClick: (Routine) -> Unit,
    onRoutineEdit: (Routine) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var routinePendingDelete by remember { mutableStateOf<Routine?>(null) }

    Scaffold(
        topBar = {
            RoutinesTopBar(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRoutineClick,
                containerColor = Color(0xFFEADFFF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter une routine")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            RoutinesScreen(
                uiState = uiState,
                onCategorieSelected = viewModel::setCategorieFilter,
                onCoursSelected = viewModel::setCoursFilter,
                onRepetabiliteSelected = viewModel::setRepetabiliteFilter,
                onShowCompletedChanged = viewModel::setShowCompleted,
                onToggleCompleted = viewModel::toggleRoutineCompletion,
                onRoutineClick = onRoutineClick,
                onRoutineEdit = onRoutineEdit,
                onRoutineDeleteRequest = { routinePendingDelete = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }

    routinePendingDelete?.let { routine ->
        AlertDialog(
            onDismissRequest = { routinePendingDelete = null },
            title = { Text("Supprimer la routine") },
            text = { Text("Voulez-vous vraiment supprimer \"${routine.nom}\" ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        routinePendingDelete = null
                        viewModel.deleteRoutine(routine)
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { routinePendingDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun RoutinesTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFB388FF)),
        modifier = Modifier
            .statusBarsPadding()
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
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
            Text(
                text = "Les Routines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Parametres")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RoutinesScreen(
    uiState: RoutinesUiState,
    onCategorieSelected: (CategorieRoutine?) -> Unit,
    onCoursSelected: (Long?) -> Unit,
    onRepetabiliteSelected: (Repetabilite?) -> Unit,
    onShowCompletedChanged: (Boolean) -> Unit,
    onToggleCompleted: (Routine) -> Unit,
    onRoutineClick: (Routine) -> Unit,
    onRoutineEdit: (Routine) -> Unit,
    onRoutineDeleteRequest: (Routine) -> Unit,
    modifier: Modifier = Modifier
) {
    val coursById = remember(uiState.cours) { uiState.cours.associateBy { it.id } }
    val groupedRoutines = remember(uiState.routines) {
        uiState.routines.groupBy { routine ->
            routine.date.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }
    }

    LazyColumn(
        modifier = modifier.background(Color(0xFFF7F2FF)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Toutes tes routines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            RoutinesFilters(
                uiState = uiState,
                onCategorieSelected = onCategorieSelected,
                onCoursSelected = onCoursSelected,
                onRepetabiliteSelected = onRepetabiliteSelected,
                onShowCompletedChanged = onShowCompletedChanged
            )
        }

        if (uiState.routines.isEmpty()) {
            item {
                EmptyRoutinesMessage()
            }
        } else {
            groupedRoutines.forEach { (weekStart, routines) ->
                item(key = "week-${weekStart}") {
                    Text(
                        text = weekLabel(weekStart),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(
                    items = routines,
                    key = { routine -> routine.id ?: "${routine.nom}-${routine.date}" }
                ) { routine ->
                    SwipeRoutineCard(
                        routine = routine,
                        cours = routine.coursId?.let { coursById[it] },
                        onClick = { onRoutineClick(routine) },
                        onEdit = { onRoutineEdit(routine) },
                        onDelete = { onRoutineDeleteRequest(routine) },
                        onToggleCompleted = { onToggleCompleted(routine) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun RoutinesFilters(
    uiState: RoutinesUiState,
    onCategorieSelected: (CategorieRoutine?) -> Unit,
    onCoursSelected: (Long?) -> Unit,
    onRepetabiliteSelected: (Repetabilite?) -> Unit,
    onShowCompletedChanged: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = uiState.selectedCategorie == null,
                    onClick = { onCategorieSelected(null) },
                    label = { Text("Toutes") }
                )
            }
            items(CategorieRoutine.entries) { categorie ->
                FilterChip(
                    selected = uiState.selectedCategorie == categorie,
                    onClick = { onCategorieSelected(categorie) },
                    label = { Text(categorie.toLabel()) }
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = uiState.selectedCoursId == null,
                    onClick = { onCoursSelected(null) },
                    label = { Text("Tous les cours") }
                )
            }
            items(uiState.cours, key = { it.id }) { cours ->
                FilterChip(
                    selected = uiState.selectedCoursId == cours.id,
                    onClick = { onCoursSelected(cours.id) },
                    label = { Text(cours.nom) }
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = uiState.selectedRepetabilite == null,
                    onClick = { onRepetabiliteSelected(null) },
                    label = { Text("Toutes les repetitions") }
                )
            }
            items(Repetabilite.entries) { repetabilite ->
                FilterChip(
                    selected = uiState.selectedRepetabilite == repetabilite,
                    onClick = { onRepetabiliteSelected(repetabilite) },
                    label = { Text(repetabilite.toLabel()) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Afficher les taches completees",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = uiState.showCompleted,
                onCheckedChange = onShowCompletedChanged
            )
        }
    }
}

@Composable
private fun SwipeRoutineCard(
    routine: Routine,
    cours: Cours?,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val actionThreshold = 92.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (dragOffset < 0) Color(0xFFFFDAD6) else Color(0xFFEADDFF),
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(routine.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            dragOffset > actionThreshold.toPx() -> onEdit()
                            dragOffset < -actionThreshold.toPx() -> onDelete()
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset = (dragOffset + dragAmount)
                            .coerceIn(-actionThreshold.toPx() * 1.35f, actionThreshold.toPx() * 1.35f)
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF6750A4))
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFB3261E))
        }

        RoutineCard(
            routine = routine,
            cours = cours,
            onClick = onClick,
            onToggleCompleted = onToggleCompleted,
            modifier = Modifier.graphicsLayer {
                translationX = dragOffset
                alpha = 1f - (abs(dragOffset) / 450f).coerceAtMost(0.12f)
            }
        )
    }
}

@Composable
private fun RoutineCard(
    routine: Routine,
    cours: Cours?,
    onClick: () -> Unit,
    onToggleCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM - HH:mm", Locale.FRENCH) }
    val coursColor = cours?.couleurHex?.toCoursColor() ?: Color(0xFFD7C4F2)
    val titleDecoration = if (routine.estCompletee) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (routine.estCompletee) Color(0xFFEDE7F2) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, Color(0x33000000), CircleShape)
                    .background(coursColor.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = routine.categorie.toLabel().firstOrNull()?.uppercase() ?: "R",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routine.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = titleDecoration,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = buildString {
                        append(routine.date.format(formatter))
                        cours?.let { append(" - ${it.nom}") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${routine.categorie.toLabel()} - ${routine.repetabilite.toLabel()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = routine.priorite.priorityColor()
                )
            }

            IconButton(onClick = onToggleCompleted) {
                Icon(
                    imageVector = if (routine.estCompletee) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = if (routine.estCompletee) {
                        "Marquer incomplete"
                    } else {
                        "Marquer completee"
                    },
                    tint = if (routine.estCompletee) Color(0xFF6750A4) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyRoutinesMessage() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Aucune routine a afficher",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Modifie les filtres ou cree une nouvelle routine depuis le dashboard.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun weekLabel(weekStart: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH)
    val weekEnd = weekStart.plusDays(6)
    return "Semaine du ${weekStart.format(formatter)} au ${weekEnd.format(formatter)}"
}

private fun Priorite.priorityColor(): Color {
    return toColor()
}

private fun Long.toCoursColor(): Color {
    val argb = if (this <= 0xFFFFFF) {
        0xFF000000L or this
    } else {
        this
    }
    return Color(argb)
}
