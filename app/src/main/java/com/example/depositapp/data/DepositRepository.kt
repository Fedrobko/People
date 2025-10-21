package com.example.depositapp.data

import kotlinx.coroutines.flow.Flow

class DepositRepository(private val depositDao: DepositDao) {
    val allDeposits: Flow<List<DepositEntity>> = depositDao.getAllDeposits()

    suspend fun insertDeposit(deposit: DepositEntity) {
        depositDao.insertDeposit(deposit)
    }

    suspend fun deleteDeposit(deposit: DepositEntity) {
        depositDao.deleteDeposit(deposit)
    }
}