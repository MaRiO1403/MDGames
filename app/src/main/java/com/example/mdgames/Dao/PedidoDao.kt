package com.example.mdgames.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mdgames.entity.PedidoEntity

@Dao
interface PedidoDao {
    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Query("SELECT * FROM PedidoEntity WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    suspend fun obtenerPedidos(usuarioId: Int): List<PedidoEntity>

    @Query("SELECT * FROM PedidoEntity WHERE usuarioId = :usuarioId")
    suspend fun obtenerPedidosPorUsuario(usuarioId: Int): List<PedidoEntity>
}