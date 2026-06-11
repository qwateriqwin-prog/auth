package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthenticatorDao {
    @Query("SELECT * FROM authenticator_accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<AuthenticatorAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AuthenticatorAccount): Long

    @Update
    suspend fun updateAccount(account: AuthenticatorAccount)

    @Query("DELETE FROM authenticator_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: Int)
}
