package com.example.myapplication.model

import com.google.firebase.firestore.DocumentId

data class Receipt(
    @DocumentId
    val id: String = "",

    val storeName: String = "",
    val date: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val imageUrl: String = "",
    val warrantyEndDate: String = "",
    val userId: String = ""
)