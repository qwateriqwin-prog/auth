package com.example.db

import kotlinx.coroutines.flow.Flow

class AuthenticatorRepository(private val authenticatorDao: AuthenticatorDao) {
    val allAccounts: Flow<List<AuthenticatorAccount>> = authenticatorDao.getAllAccounts()

    suspend fun insert(account: AuthenticatorAccount): Long {
        return authenticatorDao.insertAccount(account)
    }

    suspend fun update(account: AuthenticatorAccount) {
        authenticatorDao.updateAccount(account)
    }

    suspend fun deleteById(id: Int) {
        authenticatorDao.deleteAccountById(id)
    }
}
