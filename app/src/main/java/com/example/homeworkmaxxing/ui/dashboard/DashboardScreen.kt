package com.example.homeworkmaxxing.ui.dashboard

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onMesCoursClick: () -> Unit = {},
    onRoutinesClick: () -> Unit = {},
    onSessionDateChosen: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showCreateSessionDatePicker by remember { mutableStateOf(false) }
    var showPostponeSessionDatePicker by remember { mutableStateOf(false) }

    if (showCreateSessionDatePicker) {
        val calendar = Calendar.getInstance()
        val tomorrowStartMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        DatePickerDialog(
            context,
            { _, year, month, day ->
                showCreateSessionDatePicker = false
                viewModel.createSessionDate(year, month + 1, day)
                onSessionDateChosen()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = tomorrowStartMillis
            setOnDismissListener { showCreateSessionDatePicker = false }
        }.show()
    }

    if (showPostponeSessionDatePicker) {
        val calendar = Calendar.getInstance()
        val tomorrowStartMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        DatePickerDialog(
            context,
            { _, year, month, day ->
                showPostponeSessionDatePicker = false
                viewModel.postponeSessionDate(year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = tomorrowStartMillis
            setOnDismissListener { showPostponeSessionDatePicker = false }
        }.show()
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                onMesCoursClick = onMesCoursClick,
                onRoutinesClick = onRoutinesClick,
                onSettingsClick = onSettingsClick
            )
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
            DashboardContent(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    if (!uiState.isLoading) {
        when (uiState.sessionState) {
            SessionState.NO_SESSION -> {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Fin de session") },
                    text = {
                        Text("Veuillez sélectionner le dernier jour de votre session scolaire")
                    },
                    confirmButton = {
                        TextButton(onClick = { showCreateSessionDatePicker = true }) {
                            Text("Choisir une date")
                        }
                    }
                )
            }

            SessionState.SESSION_EXPIRED -> {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Bravo ! Ta session est terminée") },
                    text = {
                        Text("Votre session scolaire semble terminée, veuillez choisir une action. \nTerminer la session supprimera vos cours/routines afin de recommencer la prochaine session à neuf !")
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.terminateSession() }) {
                            Text("Terminer la session")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPostponeSessionDatePicker = true }) {
                            Text("Reporter la date")
                        }
                    }
                )
            }

            SessionState.SESSION_ACTIVE -> Unit
        }
    }
}

@Composable
private fun DashboardTopBar(
    onMesCoursClick: () -> Unit,
    onRoutinesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB388FF)),
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Mes Cours") },
                        onClick = {
                            isMenuExpanded = false
                            onMesCoursClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Les Routines") },
                        onClick = {
                            isMenuExpanded = false
                            onRoutinesClick()
                        }
                    )
                }
            }

            Text(
                text = "HomeWork Maxxing",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold
            )

            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    val coursById = remember(uiState.cours) { uiState.cours.associateBy { it.id } }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        uiState.sessionDateFin?.let { sessionDateFin ->
            item {
                Text(
                    text = "Date de fin de session : ${formatSessionEndDate(sessionDateFin)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val upcomingRoutines = uiState.routines
            .filterNot { it.estCompletee }
            .filterNot { it.date.isBefore(java.time.LocalDateTime.now()) }
            .sortedBy { it.date }
            .take(10)

        if (upcomingRoutines.isEmpty()) {
            item {
                EmptyRoutinesCard()
            }
        } else {
            items(
                upcomingRoutines,
                key = { routine -> routine.id ?: "${routine.nom}-${routine.date}" }
            ) { routine ->
                RoutineRow(
                    routine = routine,
                    cours = routine.coursId?.let { coursById[it] }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mes cours cette session",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            CoursesCard(cours = uiState.cours)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RoutineRow(
    routine: Routine,
    cours: Cours?
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM - HH:mm", Locale.FRENCH) }

    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFF7F4FB)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val coursColor = cours?.couleurHex?.toCoursColor() ?: Color(0xFF9E9E9E)
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(14.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF5F5F5F),
                        shape = CircleShape
                    )
                    .background(
                        color = coursColor,
                        shape = CircleShape
                    )
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routine.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = buildString {
                        append(routine.date.format(formatter))
                        cours?.let { append(" - ${it.nom}") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CoursesCard(cours: List<Cours>) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFEDE7F2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (cours.isEmpty()) {
                Text(
                    text = "Aucun cours pour cette session",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Accède a l'onglet \"Cours\" dans le menu pour gérer tes cours",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                cours.forEach { c ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF5F5F5F),
                                    shape = CircleShape
                                )
                                .background(
                                    color = c.couleurHex.toCoursColor(),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = c.nom,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRoutinesCard() {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Aucune routine pour le moment",
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Appuie sur + pour ajouter un devoir, examen ou projet.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatSessionEndDate(sessionDateFin: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH)
    return Instant.ofEpochMilli(sessionDateFin)
        .atZone(ZoneId.of("America/Toronto"))
        .toLocalDate()
        .format(formatter)
}

private fun Long.toCoursColor(): Color {
    val argb = if (this <= 0xFFFFFF) {
        0xFF000000L or this
    } else {
        this
    }
    return Color(argb)
}
