package com.example.mdgames.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mdgames.entity.VJuegoEntity

@Dao
interface VJuegoDao {
    @Query("SELECT * FROM VJuegoEntity WHERE disponible = 1")
    suspend fun obtenerTodosLosVJuegos(): MutableList<VJuegoEntity>
    @Query("SELECT * FROM VJuegoEntity WHERE disponible = 0")
    suspend fun obtenerJuegosOcultos(): List<VJuegoEntity>
    @Insert
    suspend fun agregarVJuego(vJuegoEntity: VJuegoEntity)
    @Insert
    suspend fun agregarVJuegos(vJuegos: List<VJuegoEntity>)
    @Update
    suspend fun actualizarVJuego(vJuegoEntity: VJuegoEntity)
    @Delete
    suspend fun borrarVJuego(vJuegoEntity: VJuegoEntity)
    @Query("DELETE FROM VJuegoEntity")
    suspend fun eliminarTodosLosVJuegos()

    @Query("SELECT * FROM VJuegoEntity WHERE plataforma = :plataforma AND disponible = 1")
    suspend fun obtenerPorPlataforma(plataforma: String): List<VJuegoEntity>
    @Query("SELECT * FROM VJuegoEntity WHERE titulo = :titulo AND plataforma = :plataforma LIMIT 1")
    suspend fun obtenerPorTituloYPlataforma(titulo: String, plataforma: String): VJuegoEntity?
    @Query("SELECT * FROM VJuegoEntity WHERE titulo LIKE :texto || '%' AND disponible = 1")
    suspend fun buscarPorTitulo(texto: String): List<VJuegoEntity>
    @Query("SELECT * FROM VJuegoEntity WHERE id = :id LIMIT 1")
    suspend fun obtenerJuegoPorId(id: Int): VJuegoEntity?


}