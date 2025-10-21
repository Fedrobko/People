package com.example.depositapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val initialDeposit: Double,
    val annualRate: Double,
    val monthlyDeposit: Double,
    val months: Int,
    val totalAmount: Double,
    val income: Double,
    val incomePercentage: Double,
    val createdAt: Long = System.currentTimeMillis()
)