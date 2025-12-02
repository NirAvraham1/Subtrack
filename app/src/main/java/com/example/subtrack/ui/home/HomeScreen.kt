package com.example.subtrack.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy // אייקון הרובוט
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddExpenseClick: () -> Unit,
    onExpenseClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onAiAdvisorClick: () -> Unit, // <-- הוספנו את הפרמטר הזה!
    viewModel: HomeViewModel = hiltViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val totalCost by viewModel.totalMonthlyCost.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState() // בדיקת המנוי

    // דיאלוג שדרוג (אם לוחצים על ה-AI בלי מנוי)
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
        // --- השינוי הגדול: שני כפתורים צפים (AI + Add) ---
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // מרווח בין הצדדים
                verticalAlignment = Alignment.Bottom
            ) {
                // 1. כפתור AI (צד שמאל) 🤖
                FloatingActionButton(
                    onClick = {
                        if (currentPlan == "ai") {
                            onAiAdvisorClick()
                        } else {
                            showUpgradeDialog = true
                        }
                    },
                    containerColor = if (currentPlan == "ai") MaterialTheme.colorScheme.primary else Color.LightGray,
                    contentColor = Color.White
                ) {
                    // --- השינוי כאן: בדיקה איזה אייקון להציג ---
                    if (currentPlan == "ai") {
                        // מצב פעיל: מציג את האייקון המיוחד שלך
                        Icon(
                            painter = painterResource(id = R.drawable.ai_icon),
                            contentDescription = "AI Advisor",
                            modifier = Modifier.size(24.dp), // גודל סטנדרטי לאייקון
                            tint = Color.White // צובע את האייקון בלבן כדי שיראה יפה על הרקע הכחול
                        )
                    } else {
                        // מצב נעול: מציג מנעול רגיל
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White
                        )
                    }
                }

                // 2. כפתור הוספה רגיל (צד ימין) ➕
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
            // --- הבאנר העליון ---
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

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(expenses) { expense ->
                    Box(modifier = Modifier.clickable { onExpenseClick(expense.id) }) {
                        ExpenseCard(expense = expense)
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
                        onSettingsClick() // שולח להגדרות כדי לשדרג
                    }) { Text("Upgrade Plan") }
                },
                dismissButton = {
                    TextButton(onClick = { showUpgradeDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}