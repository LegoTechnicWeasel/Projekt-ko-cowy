import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(onNavigateToMain: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tytuł projektu
            Text(
                text = "Menedżer Paragonów\ni Gwarancji",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Dane studenta
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Jakub Kasprzyk", fontSize = 20.sp)
                    Text(text = "Indeks: 275744", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Przycisk przejścia do aplikacji
            Button(
                onClick = onNavigateToMain,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(text = "Uruchom aplikację")
            }
        }
    }
}