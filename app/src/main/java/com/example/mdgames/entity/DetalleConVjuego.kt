package com.example.mdgames.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DetalleConVjuego(
    @Embedded val detalle: DetallePedidoEntity,

    @Relation(
        parentColumn = "vjuegoId",
        entityColumn = "id"
    )
    val juego: VJuegoEntity
)
