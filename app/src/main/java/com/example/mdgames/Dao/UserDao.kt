package com.example.mdgames.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mdgames.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity WHERE email = :email AND contrasena = :contrasena LIMIT 1")
    suspend fun obtenerUsuario(email: String, contrasena: String): UserEntity?

    @Query("SELECT * FROM UserEntity WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): UserEntity?

    @Query("SELECT * FROM UserEntity WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioEmail(email: String): UserEntity?

    @Insert
    suspend fun insertUser(userEntity: UserEntity)

    @Update
    suspend fun actualizarUsuario(userEntity: UserEntity)

    @Delete
    suspend fun borrarUsuario(userEntity: UserEntity)
}