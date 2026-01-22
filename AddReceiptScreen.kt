package com.example.myapplication // Dostosuj pakiet

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.model.Receipt
import com.example.myapplication.ui.CameraCaptureScreen // Importuj utworzony wcześniej plik
import com.example.myapplication.viewmodel.ReceiptViewModel

@Composable
fun AddReceiptScreen(
    viewModel: ReceiptViewModel,
    onSaveSuccess: () -> Unit
) {
    // Stany formularza
    var storeName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    // Stan zdjęcia
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showCamera by remember { mutableStateOf(false) }

    // Stan ładowania
    val isLoading by viewModel.isLoading.collectAsState()

    if (showCamera) {
        // Widok kamery (pełny ekran)
        CameraCaptureScreen(
            onImageCaptured = { uri ->
                imageUri = uri
                showCamera = false // Wróć do formularza
            },
            onError = { /* Obsługa błędu kamery */ }
        )
    } else {
        // Widok formularza
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Nowy Paragon", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Nazwa sklepu") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data (np. 2026-01-19)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategoria") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Kwota") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Podgląd zdjęcia lub przycisk
                if (imageUri != null) {
                    Text("Zdjęcie dołączone:")
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Zdjęcie paragonu",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Button(onClick = { showCamera = true }) {
                        Text("Zmień zdjęcie")
                    }
                } else {
                    Button(
                        onClick = { showCamera = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zrób zdjęcie paragonu")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Przycisk zapisu
                Button(
                    onClick = {
                        val receipt = Receipt(
                            storeName = storeName,
                            category = category,
                            date = date,
                            amount = amount.toDoubleOrNull() ?: 0.0
                        )
                        viewModel.addReceipt(receipt, imageUri) {
                            onSaveSuccess()
                        }
                    },
                    enabled = !isLoading && storeName.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Zapisz w chmurze")
                    }
                }
            }
        }
    }
}