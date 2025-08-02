package com.example.expensetracker.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    
    val totalAmount: Flow<Double?> = expenseDao.getTotalAmount()
    
    fun getExpensesByCategory(category: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }
    
    fun getTotalAmountByCategory(category: String): Flow<Double?> {
        return expenseDao.getTotalAmountByCategory(category)
    }
    
    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }
    
    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }
    
    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
    
    suspend fun deleteExpenseById(expenseId: Long) {
        expenseDao.deleteExpenseById(expenseId)
    }
    
    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }
} 