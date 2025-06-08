package com.example.mdgames.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey


@Entity(
    tableName = "CestaEntity",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["usuarioId"],
            onDelete = CASCADE
        ),
        ForeignKey(entity = VJuegoEntity::class, parentColumns = ["id"], childColumns = ["vjuegoId"],
            onDelete = CASCADE
        )
    ]
)
data class CestaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val vjuegoId: Int,
    var cantidad: Int = 1
)

