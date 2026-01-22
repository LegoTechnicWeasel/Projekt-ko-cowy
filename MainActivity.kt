package com.example.myapplication

import StartScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.myapplication.model.Receipt
import com.example.myapplication.ui.theme.ReceiptAppTheme
import com.example.myapplication.viewmodel.ReceiptViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Rejestracja zapytania o uprawnienia do kamery
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
            } else {
            }
        }

        // Zapytanie o uprawnienia przy starcie aplikacji
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)

        setContent {
            ReceiptAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Tworzymy instancję ViewModelu
    val viewModel: ReceiptViewModel = viewModel()

    NavHost(navController = navController, startDestination = "start") {

        // EKRAN STARTOWY (Wymóg projektu)
        composable("start") {
            StartScreen(
                onNavigateToMain = { navController.navigate("home") }
            )
        }

        // EKRAN GŁÓWNY (Lista paragonów)
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAddReceipt = { navController.navigate("add_receipt") }
            )
        }

        // EKRAN DODAWANIA (Formularz + Kamera)
        composable("add_receipt") {
            AddReceiptScreen(
                viewModel = viewModel,
                onSaveSuccess = {
                    navController.popBackStack() // Wróć do listy po zapisaniu
                }
            )
        }
    }
}

// --- Definicja Ekranu Głównego (Lista) ---

@Composable
fun HomeScreen(
    viewModel: ReceiptViewModel,
    onAddReceipt: () -> Unit
) {
    // Obserwujemy strumień danych z ViewModelu (podłączonego do Firebase)
    val receipts by viewModel.receipts.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddReceipt) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { padding ->
        if (receipts.isEmpty()) {
            // Widok, gdy lista jest pusta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Brak paragonów.\nKliknij + aby dodać.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            // Lista paragonów
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(receipts) { receipt ->
                    ReceiptItem(receipt)
                }
            }
        }
    }
}

// --- Komponent pojedynczego elementu na liście ---

@Composable
fun ReceiptItem(receipt: Receipt) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura zdjęcia
            if (receipt.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = receipt.imageUrl,
                    contentDescription = "Zdjęcie paragonu",
                    modifier = Modifier
                        .size(70.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Informacje tekstowe
            Column {
                Text(
                    text = receipt.storeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Data: ${receipt.date}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Kat: ${receipt.category}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${receipt.amount} PLN",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}