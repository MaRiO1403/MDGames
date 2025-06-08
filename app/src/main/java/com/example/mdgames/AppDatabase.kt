package com.example.mdgames

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mdgames.Dao.CestaDao
import com.example.mdgames.Dao.DetallePedidoDao
import com.example.mdgames.Dao.FavoritoDao
import com.example.mdgames.Dao.PedidoDao
import com.example.mdgames.Dao.UserDao
import com.example.mdgames.Dao.VJuegoDao
import com.example.mdgames.entity.CestaEntity
import com.example.mdgames.entity.DetallePedidoEntity
import com.example.mdgames.entity.FavoritoEntity
import com.example.mdgames.entity.PedidoEntity
import com.example.mdgames.entity.UserEntity
import com.example.mdgames.entity.VJuegoEntity

@Database(entities = [VJuegoEntity::class, UserEntity::class, FavoritoEntity::class, CestaEntity::class, PedidoEntity::class, DetallePedidoEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    // DAOs to access each table in the database
    abstract fun vjuegoDao(): VJuegoDao
    abstract fun userDao(): UserDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun cestaDao(): CestaDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detallePedidoDao(): DetallePedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton to get the single instance of the database
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "BaseDeDatos"  // Database name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}