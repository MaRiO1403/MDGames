package com.example.mdgames.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "DetallePedidoEntity",
    foreignKeys = [
        ForeignKey(entity = PedidoEntity::class, parentColumns = ["id"], childColumns = ["pedidoId"],
            onDelete = CASCADE
        ),
        ForeignKey(entity = VJuegoEntity::class, parentColumns = ["id"], childColumns = ["vjuegoId"],
            onDelete = CASCADE
        )
    ]
)
data class DetallePedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val vjuegoId: Int,
    val cantidad: Int,
    val precioUnitario: Int
)

