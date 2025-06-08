package com.example.mdgames.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mdgames.entity.CestaEntity

@Dao
interface CestaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(cesta: CestaEntity)

    @Query("SELECT * FROM CestaEntity WHERE usuarioId = :usuarioId")
    suspend fun obtenerPorUsuario(usuarioId: Int): List<CestaEntity>

    @Query("SELECT * FROM CestaEntity WHERE usuarioId = :usuarioId AND vjuegoId = :vjuegoId LIMIT 1")
    suspend fun obtenerPorUsuarioYJuego(usuarioId: Int, vjuegoId: Int): CestaEntity?

    @Update
    suspend fun actualizar(cesta: CestaEntity)

    @Delete
    suspend fun eliminar(cesta: CestaEntity)

    @Query("DELETE FROM CestaEntity WHERE usuarioId = :usuarioId")
    suspend fun eliminarTodoPorUsuario(usuarioId: Int)

}
