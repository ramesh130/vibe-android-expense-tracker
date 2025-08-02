package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.data.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = ExpenseDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(database.expenseDao())
        
        setContent {
            ExpenseTrackerTheme {
                ExpenseTrackerApp(repository)
            }
        }
    }
}

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    
    val expenses: Flow<List<Expense>> = repository.allExpenses
    val totalAmount: Flow<Double?> = repository.totalAmount
    
    fun addExpense(description: String, amount: Double, category: String) {
        viewModelScope.launch {
            val expense = Expense(
                description = description,
                amount = amount,
                category = category
            )
            repository.insertExpense(expense)
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}

class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun categoryColor(category: String): Color = when (category) {
    "Groceries" -> Color(0xFF4CAF50)
    "Entertainment" -> Color(0xFF9C27B0)
    "Eat Out" -> Color(0xFFFF9800)
    "Take Out" -> Color(0xFFFF5722)
    "Transport" -> Color(0xFF2196F3)
    "Utilities" -> Color(0xFF607D8B)
    else -> Color(0xFF795548)
}

fun categoryEmoji(category: String): String = when (category) {
    "Groceries" -> "🛒"
    "Entertainment" -> "🎬"
    "Eat Out" -> "🍽️"
    "Take Out" -> "🍕"
    "Transport" -> "🚗"
    "Utilities" -> "⚡"
    else -> "📝"
}

@Composable
fun SpendingChart(expenses: List<Expense>) {
    if (expenses.isEmpty()) return
    
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { expense -> expense.amount } }
        .toList()
        .sortedByDescending { it.second }
    
    val total = categoryTotals.sumOf { it.second }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0f3460).copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 Spending by Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFe94560),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pie Chart
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        var startAngle = 0f
                        categoryTotals.forEach { (category, amount) ->
                            val sweepAngle = (amount.toFloat() / total.toFloat() * 360f)
                            val color = categoryColor(category)
                            
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                            
                            startAngle += sweepAngle
                        }
                    }
                }
                
                // Legend
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryTotals.forEach { (category, amount) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = categoryColor(category),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${categoryEmoji(category)} $category",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$${"%.2f".format(amount)} (${"%.1f".format(amount / total * 100)}%)",
                                    fontSize = 10.sp,
                                    color = Color(0xFFa8a8a8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp(repository: ExpenseRepository) {
    val categories = listOf("Groceries", "Entertainment", "Eat Out", "Take Out", "Transport", "Utilities", "Other")
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    
    val viewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(repository)
    )
    
    val expenses by viewModel.expenses.collectAsStateWithLifecycle(initialValue = emptyList())
    val total by viewModel.totalAmount.collectAsStateWithLifecycle(initialValue = 0.0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0f3460).copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💰 Expense Tracker",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFe94560)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total Balance",
                            fontSize = 14.sp,
                            color = Color(0xFFa8a8a8)
                        )
                        Text(
                            text = "$${"%.2f".format(total)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (total >= 0) Color(0xFF4ade80) else Color(0xFFf87171)
                        )
                    }
                }
            }

            item {
                // Spending Chart
                SpendingChart(expenses)
            }

            item {
                // Input Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0f3460).copy(alpha = 0.9f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "➕ Add New Expense",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFe94560),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Text("📝", fontSize = 18.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFe94560),
                                unfocusedBorderColor = Color(0xFF4a5568),
                                focusedLabelColor = Color(0xFFe94560),
                                unfocusedLabelColor = Color(0xFFa8a8a8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Text("💰", fontSize = 18.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFe94560),
                                unfocusedBorderColor = Color(0xFF4a5568),
                                focusedLabelColor = Color(0xFFe94560),
                                unfocusedLabelColor = Color(0xFFa8a8a8),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = "${categoryEmoji(selectedCategory)} $selectedCategory",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                leadingIcon = { Text("🏷️", fontSize = 18.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFe94560),
                                    unfocusedBorderColor = Color(0xFF4a5568),
                                    focusedLabelColor = Color(0xFFe94560),
                                    unfocusedLabelColor = Color(0xFFa8a8a8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color(0xFF0f3460))
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(categoryEmoji(category), fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(category, color = Color.White)
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = Color.White,
                                            leadingIconColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                val amt = amount.toDoubleOrNull()
                                if (!description.isBlank() && amt != null && selectedCategory.isNotBlank()) {
                                    viewModel.addExpense(description, amt, selectedCategory)
                                    description = ""
                                    amount = ""
                                    selectedCategory = categories[0]
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFe94560)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Expense", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item {
                // Transactions List Header
                if (expenses.isNotEmpty()) {
                    Text(
                        text = "📋 Recent Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFe94560),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            // Transactions List
            if (expenses.isNotEmpty()) {
                items(expenses) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0f3460).copy(alpha = 0.9f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(categoryColor(expense.category)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = categoryEmoji(expense.category),
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = expense.description,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = expense.category,
                                        fontSize = 12.sp,
                                        color = Color(0xFFa8a8a8)
                                    )
                                }
                            }
                            Text(
                                text = "-$${"%.2f".format(expense.amount)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFf87171)
                            )
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0f3460).copy(alpha = 0.9f))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No expenses yet!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = "Add your first expense to get started",
                                fontSize = 14.sp,
                                color = Color(0xFFa8a8a8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerTheme {
        Greeting("Android")
    }
}