package com.example.mdgames.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mdgames.entity.FavoritoEntity

@Dao
interface FavoritoDao {
    @Query("SELECT * FROM FavoritoEntity WHERE usuarioId = :userId")
    suspend fun obtenerTodosLosFavoritos(userId: Int): MutableList<FavoritoEntity>
    @Insert
    suspend fun agregarFavorito(favoritoEntity: FavoritoEntity)
    @Delete
    suspend fun borrarFavorito(favoritoEntity: FavoritoEntity)
}