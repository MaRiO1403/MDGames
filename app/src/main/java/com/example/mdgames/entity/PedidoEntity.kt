package com.example.mdgames.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PedidoEntity")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val fecha: Long = System.currentTimeMillis(),
    val total: Int
)

