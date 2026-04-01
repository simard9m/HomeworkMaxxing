package com.example.homeworkmaxxing.ui.cours

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homeworkmaxxing.data.model.Cours

@Composable
fun MesCoursPage(
    viewModel: MesCoursViewModel,
    onOpenDrawer: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAddCoursClick: () -> Unit = {},
    onEditCoursClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MesCoursScreen(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer,
        onSettingsClick = onSettingsClick,
        onAddCoursClick = onAddCoursClick,
        onEditCoursClick = { cours -> onEditCoursClick(cours.id) },
        onDeleteCoursClick = viewModel::onDeleteClicked,
        onDismissDeleteDialog = viewModel::dismissDeleteDialog,
        onConfirmDeleteCours = viewModel::confirmDeleteCours
    )
}

@Composable
fun MesCoursScreen(
    uiState: MesCoursUiState,
    onOpenDrawer: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddCoursClick: () -> Unit,
    onEditCoursClick: (Cours) -> Unit,
    onDeleteCoursClick: (Cours) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDeleteCours: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF8F8F8),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCoursClick,
                containerColor = Color(0xFFD9C8F4),
                contentColor = Color(0xFF6B4FB3)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter un cours"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Ouvrir le menu"
                    )
                }

                Text(
                    text = "HomeWork Maxxing",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Paramètres"
                    )
                }
            }

            Text(
                text = "Mes Cours",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFE4D9F3),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(10.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.cours.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Aucun cours pour le moment")
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = uiState.cours,
                                key = { it.id }
                            ) { cours ->
                                CoursItem(
                                    cours = cours,
                                    onEditClick = { onEditCoursClick(cours) },
                                    onDeleteClick = { onDeleteCoursClick(cours) }
                                )
                            }
                        }
                    }
                }
            }
        }

        uiState.coursToDelete?.let { cours ->
            DeleteCoursDialog(
                cours = cours,
                onDismiss = onDismissDeleteDialog,
                onConfirm = onConfirmDeleteCours
            )
        }
    }
}

@Composable
private fun CoursItem(
    cours: Cours,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F1F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(cours.couleurHex.toComposeColor(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cours.nom.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = Color(0xFF5A4A85),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = cours.nom,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier ${cours.nom}"
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer ${cours.nom}"
                )
            }
        }
    }
}

@Composable
private fun DeleteCoursDialog(
    cours: Cours,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Supprimer")
        },
        text = {
            Text("Êtes-vous sûr de vouloir supprimer le cours \"${cours.nom}\" ?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

private fun Long.toComposeColor(): Color {
    val argb = if (this <= 0xFFFFFF) {
        0xFF000000L or this
    } else {
        this
    }
    return Color(argb)
}