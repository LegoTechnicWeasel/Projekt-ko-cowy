package com.example.myapplication.viewmodel // Upewnij się co do pakietu!

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Receipt
import com.example.myapplication.repository.ReceiptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReceiptViewModel : ViewModel() {
    private val repository = ReceiptRepository()

    // Stan listy paragonów
    private val _receipts = MutableStateFlow<List<Receipt>>(emptyList())
    val receipts: StateFlow<List<Receipt>> = _receipts

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadReceipts()
    }

    private fun loadReceipts() {
        viewModelScope.launch {
            repository.getReceipts().collect { list ->
                _receipts.value = list
            }
        }
    }

    fun addReceipt(receipt: Receipt, imageUri: Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addReceipt(
                receipt = receipt,
                imageUri = imageUri,
                onSuccess = {
                    _isLoading.value = false
                    onComplete()
                },
                onError = {
                    _isLoading.value = false
                }
            )
        }
    }
}