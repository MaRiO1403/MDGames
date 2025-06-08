package com.example.mdgames.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "UserEntity")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nombre: String? = "",
    val email: String? = "",
    var contrasena: String? = "",
    val isAdmin: Boolean = false
) : Serializable
