package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AuthenticatorAccount::class], version = 1, exportSchema = false)
abstract class AuthenticatorDatabase : RoomDatabase() {
    abstract fun authenticatorDao(): AuthenticatorDao

    companion object {
        @Volatile
        private var INSTANCE: AuthenticatorDatabase? = null

        fun getDatabase(context: Context): AuthenticatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuthenticatorDatabase::class.java,
                    "secure_authenticator_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
