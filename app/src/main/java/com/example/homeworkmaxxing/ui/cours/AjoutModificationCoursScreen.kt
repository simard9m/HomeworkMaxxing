package com.example.homeworkmaxxing.ui.cours

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.homeworkmaxxing.util.ValidationRules

private val courseColors = listOf(
    0xFFF7C8D0,
    0xFFFFDCA8,
    0xFFD9F2B4,
    0xFFBEEAF3,
    0xFFDCD5F7,
    0xFFF8D3FF,
    0xFFCFE1FF,
    0xFFFFE7C7
)

@Composable
fun AjoutModificationCoursPage(
    viewModel: CoursFormViewModel,
    coursId: Long = -1L,
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(coursId) {
        if (coursId == -1L) {
            viewModel.resetForCreate()
        } else {
            viewModel.loadCoursForEdit(coursId)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onSaveSuccess()
        }
    }

    AjoutModificationCoursScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNomChange = viewModel::onNomChange,
        onColorSelected = viewModel::onColorSelected,
        onSaveClick = viewModel::saveCours
    )
}

@Composable
fun AjoutModificationCoursScreen(
    uiState: CoursFormUiState,
    onBackClick: () -> Unit,
    onNomChange: (String) -> Unit,
    onColorSelected: (Long) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF8F8F8),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8F8))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour"
                    )
                }

                Text(
                    text = if (uiState.isEditMode) {
                        "Modifier un cours"
                    } else {
                        "Ajouter un cours"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEDE4F8)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = if (uiState.isEditMode) {
                                "Modifiez les informations du cours"
                            } else {
                                "Entrez les informations du nouveau cours"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = uiState.nom,
                            onValueChange = onNomChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nom du cours") },
                            singleLine = true,
                            isError = uiState.nomError != null,
                            supportingText = {
                                val error = uiState.nomError
                                if (error != null) {
                                    Text(error)
                                } else {
                                    Text("${uiState.nom.length}/${ValidationRules.MAX_COURS_NOM_LENGTH}")
                                }
                            }
                        )

                        Text(
                            text = "Couleur",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )

                        ColorPickerSection(
                            selectedColor = uiState.couleurHex,
                            onColorSelected = onColorSelected
                        )

                        PreviewCoursCard(
                            nom = uiState.nom,
                            couleurHex = uiState.couleurHex
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = onSaveClick,
                                enabled = !uiState.isSaving,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD7C4F4),
                                    contentColor = Color(0xFF4B3B73)
                                )
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = if (uiState.isEditMode) {
                                            "Enregistrer"
                                        } else {
                                            "Créer"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerSection(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit
) {
    val rows = listOf(
        courseColors.take(4),
        courseColors.drop(4)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { rowColors ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowColors.forEach { colorHex ->
                    val isSelected = selectedColor == colorHex

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(colorHex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF4B3B73) else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorHex) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewCoursCard(
    nom: String,
    couleurHex: Long
) {
    val displayName = if (nom.isBlank()) "Aperçu du cours" else nom

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F1F8)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(couleurHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.firstOrNull()?.uppercase() ?: "?",
                    color = Color(0xFF4B3B73),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
