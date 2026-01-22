package com.example.myapplication.repository

import android.net.Uri
import android.util.Log
import com.example.myapplication.model.Receipt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReceiptRepository {
    // Instancje serwisów Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Kolekcja w bazie danych
    private val receiptsCollection = firestore.collection("receipts")

    /**
     * Funkcja do pobierania listy paragonów (Real-time updates)
     * Zwraca Flow, który emituje nową listę za każdym razem, gdy coś zmieni się w bazie.
     */
    fun getReceipts(): Flow<List<Receipt>> = callbackFlow {
        val subscription = receiptsCollection
            .orderBy("date", Query.Direction.DESCENDING) // Sortowanie po dacie
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val receipts = snapshot.toObjects(Receipt::class.java)
                    trySend(receipts)
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Funkcja dodająca paragon:
     * 1. Wysyła zdjęcie (jeśli jest).
     * 2. Zapisuje dane w Firestore.
     */
    suspend fun addReceipt(
        receipt: Receipt,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val finalImageUrl = ""
            Log.w("ReceiptRepository", "Pominięto upload zdjęcia - brak planu Blaze. Zapisuję tylko dane tekstowe.")

            // KROK 2: Przygotowanie obiektu (bez adresu URL z chmury)
            val newReceipt = receipt.copy(imageUrl = finalImageUrl)

            // KROK 3: Zapis do Firestore (To powinno działać na darmowym planie!)
            receiptsCollection.add(newReceipt).await()

            onSuccess()

        } catch (e: Exception) {
            Log.e("ReceiptRepository", "Błąd dodawania paragonu", e)
            onError(e.message ?: "Wystąpił nieznany błąd")
        }
    }

    // Opcjonalnie: Usuwanie paragonu
    suspend fun deleteReceipt(receiptId: String) {
        try {
            receiptsCollection.document(receiptId).delete().await()
        } catch (e: Exception) {
            Log.e("ReceiptRepository", "Błąd usuwania", e)
        }
    }
}