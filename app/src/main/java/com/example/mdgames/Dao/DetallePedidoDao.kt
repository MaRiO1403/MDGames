package com.example.mdgames.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mdgames.entity.DetalleConVjuego
import com.example.mdgames.entity.DetallePedidoEntity

@Dao
interface DetallePedidoDao {
    @Insert
    suspend fun insertar(vararg detalles: DetallePedidoEntity)

    @Query("SELECT * FROM DetallePedidoEntity WHERE pedidoId = :pedidoId")
    suspend fun obtenerDetalles(pedidoId: Int): List<DetallePedidoEntity>

    @Query("SELECT * FROM DetallePedidoEntity WHERE pedidoId = :pedidoId")
    suspend fun obtenerDetalleConVjuego(pedidoId: Int): List<DetalleConVjuego>
}