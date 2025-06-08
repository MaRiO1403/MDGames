package com.example.mdgames.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "VJuegoEntity",
    indices = [Index(value = ["titulo", "plataforma"], unique = true)]
)

data class VJuegoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var titulo: String = "",
    var descripcion: String = "",
    var esFavorita: Boolean = false,
    val caratulaUrl: String = "",
    val imagenUrl: String = "",
    val precio: Int = 0,
    val plataforma: String = "",
    val disponible: Boolean = true
) : Serializable
