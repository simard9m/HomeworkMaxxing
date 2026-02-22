import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homeworkmaxxing.data.model.Cours
import com.example.homeworkmaxxing.data.model.Routine
import com.example.homeworkmaxxing.ui.dashboard.DashboardUiState
import com.example.homeworkmaxxing.ui.dashboard.DashboardViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAddRoutineClick: () -> Unit = {},
    onRoutineClick: (Routine) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DashboardTopBar(
                onMenuClick = onMenuClick,
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
        DashboardContent(
            uiState = uiState,
            onRoutineClick = onRoutineClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun DashboardTopBar(
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB388FF)),
        modifier = Modifier
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
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
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
    onRoutineClick: (Routine) -> Unit,
    modifier: Modifier = Modifier
) {
    val coursById = remember(uiState.cours) { uiState.cours.associateBy { it.id } }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (uiState.routines.isEmpty()) {
            item {
                EmptyRoutinesCard()
            }
        } else {
            items(uiState.routines.sortedBy { it.date }, key = { it.id }) { routine ->
                RoutineRow(
                    routine = routine,
                    cours = routine.coursId?.let { coursById[it] },
                    onClick = { onRoutineClick(routine) }
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
    cours: Cours?,
    onClick: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM • HH:mm", Locale.FRENCH) }

    ElevatedCard(
        onClick = onClick,
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
            Text(
                text = "⊕", // replace with real icon later
                modifier = Modifier.padding(end = 10.dp)
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
                        cours?.let { append("  •  ${it.nom}") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "›",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                                .size(10.dp)
                                .background(
                                    color = Color(c.couleurHex),
                                    shape = RoundedCornerShape(50)
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