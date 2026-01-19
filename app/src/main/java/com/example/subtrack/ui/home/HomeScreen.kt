package com.example.subtrack.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subtrack.R
import com.example.subtrack.utils.NativeAdCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddExpenseClick: () -> Unit,
    onExpenseClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onAiAdvisorClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val totalCost by viewModel.totalMonthlyCost.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()

    var showUpgradeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SubTrack",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // 1. כפתור AI
                FloatingActionButton(
                    onClick = {
                        if (currentPlan == "ai") {
                            onAiAdvisorClick()
                        } else {
                            // --- התיקון כאן ---
                            // במקום analyticsManager ישירות, אנחנו קוראים דרך ה-ViewModel
                            viewModel.onFreeUserAiClicked()
                            showUpgradeDialog = true
                        }
                    },
                    containerColor = if (currentPlan == "ai") MaterialTheme.colorScheme.primary else Color.LightGray,
                    contentColor = Color.White
                ) {
                    if (currentPlan == "ai") {
                        Icon(
                            painter = painterResource(id = R.drawable.ai_icon),
                            contentDescription = "AI Advisor",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White
                        )
                    }
                }

                // 2. כפתור הוספה
                FloatingActionButton(
                    onClick = onAddExpenseClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- באנר הוצאות ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Monthly Expenses", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = String.format("%.2f ₪", totalCost),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text("YOUR SUBSCRIPTIONS", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))

            // --- רשימה עם פרסומות ---
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                itemsIndexed(expenses) { index, expense ->
                    Box(modifier = Modifier.clickable { onExpenseClick(expense.id) }) {
                        ExpenseCard(expense = expense)
                    }

                    if (currentPlan == "free" && (index + 1) % 3 == 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        NativeAdCard()
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        // --- דיאלוג שדרוג ---
        if (showUpgradeDialog) {
            AlertDialog(
                onDismissRequest = { showUpgradeDialog = false },
                title = { Text("Ultimate Feature 🤖") },
                text = { Text("The AI Financial Advisor is available exclusively for Ultimate AI subscribers.") },
                confirmButton = {
                    Button(onClick = {
                        showUpgradeDialog = false
                        onSettingsClick()
                    }) { Text("Upgrade Plan") }
                },
                dismissButton = {
                    TextButton(onClick = { showUpgradeDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}