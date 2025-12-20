package com.example.subtrack.ui.add_expense

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subtrack.data.local.entity.ExpenseFrequency
import com.example.subtrack.ui.theme.TealAccent
import com.example.subtrack.utils.InterstitialAdManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // --- קריאת המנוי הנוכחי מה-ViewModel ---
    val currentPlan by viewModel.currentPlan.collectAsState()

    // --- State ---
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf(ExpenseFrequency.MONTHLY) }

    var startDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var renewalDate by remember { mutableStateOf(System.currentTimeMillis()) }

    var isTrialEnabled by remember { mutableStateOf(false) }
    var trialMonths by remember { mutableStateOf("1") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val expenseToEdit by viewModel.expenseState.collectAsState()

    LaunchedEffect(expenseToEdit) {
        expenseToEdit?.let { expense ->
            name = expense.name
            amount = expense.amount.toString()
            category = expense.category
            selectedFrequency = expense.frequency
            startDate = expense.startDate
            endDate = expense.endDate
            renewalDate = expense.renewalDate
            if (expense.firstPaymentDate > expense.startDate) {
                isTrialEnabled = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (expenseToEdit == null) "Add Expense" else "Edit Expense",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    if (expenseToEdit != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            StyledTextField(value = name, onValueChange = { name = it }, label = "Name")
            StyledTextField(value = amount, onValueChange = { amount = it }, label = "Amount", keyboardType = KeyboardType.Number)
            StyledTextField(value = category, onValueChange = { category = it }, label = "Category")

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Is there a Free Trial?", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isTrialEnabled,
                    onCheckedChange = { isTrialEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TealAccent
                    )
                )
            }

            if (isTrialEnabled) {
                StyledTextField(value = trialMonths, onValueChange = { trialMonths = it }, label = "Trial Duration (Months)", keyboardType = KeyboardType.Number)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            DatePickerField(label = "Start Date", selectedDate = startDate, onDateSelected = { startDate = it })
            DatePickerField(label = "Next Renewal", selectedDate = renewalDate, onDateSelected = { renewalDate = it })
            DatePickerField(label = "End Date (Optional)", selectedDate = endDate, onDateSelected = { endDate = it })

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Billing Cycle:", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FrequencyButton(text = "Monthly", isSelected = selectedFrequency == ExpenseFrequency.MONTHLY, onClick = { selectedFrequency = ExpenseFrequency.MONTHLY }, modifier = Modifier.weight(1f))
                FrequencyButton(text = "Yearly", isSelected = selectedFrequency == ExpenseFrequency.YEARLY, onClick = { selectedFrequency = ExpenseFrequency.YEARLY }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val months = if (isTrialEnabled) trialMonths.toIntOrNull() ?: 0 else 0
                    viewModel.saveExpense(
                        name, amount, category, selectedFrequency, startDate, endDate, renewalDate,
                        trialMonths = months
                    )

                    // --- השינוי כאן: בדיקה אם המנוי חינמי לפני הצגת המודעה ---
                    if (currentPlan == "free") {
                        val activity = context as? Activity
                        if (activity != null) {
                            InterstitialAdManager.showAd(activity) {
                                onNavigateBack()
                            }
                        } else {
                            onNavigateBack()
                        }
                    } else {
                        // אם זה פרימיום/AI -> אין מודעה, יוצאים מיד
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                enabled = name.isNotEmpty() && amount.isNotEmpty()
            ) {
                Text(if (expenseToEdit == null) "SAVE EXPENSE" else "UPDATE EXPENSE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = Color.White,
                title = { Text("Delete Expense?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                text = { Text("Are you sure you want to delete this expense? This action cannot be undone.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteExpense()
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Delete", fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                }
            )
        }
    }
}

// --- רכיבים לשימוש חוזר ---
@Composable
fun StyledTextField(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, disabledContainerColor = Color.White,
            focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = Color.LightGray
        )
    )
}

@Composable
fun FrequencyButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick, modifier = modifier.height(50.dp), shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(label: String, selectedDate: Long?, onDateSelected: (Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = if (selectedDate != null) convertMillisToDate(selectedDate) else "", onValueChange = { },
        label = { Text(label) }, placeholder = { Text("Select Date") },
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, enabled = false, readOnly = true,
        shape = RoundedCornerShape(12.dp),
        trailingIcon = { Icon(Icons.Default.DateRange, null, tint = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = Color.White, disabledBorderColor = Color.LightGray,
            disabledTextColor = Color.Black, disabledLabelColor = Color.Gray, disabledPlaceholderColor = Color.Gray, disabledTrailingIconColor = Color.Gray
        )
    )
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { onDateSelected(it) }; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}