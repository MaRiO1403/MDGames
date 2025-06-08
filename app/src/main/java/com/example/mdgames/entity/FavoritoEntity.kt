package com.example.mdgames.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "FavoritoEntity",
    primaryKeys = ["usuarioId", "vjuegoId"],
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"],
            onDelete = CASCADE
        ),
        ForeignKey(entity = VJuegoEntity::class, parentColumns = ["id"], childColumns = ["vjuegoId"],
            onDelete = CASCADE
        )
    ]
)

data class FavoritoEntity(
    val usuarioId: Int,
    val vjuegoId: Int
)
