package com.example.subtrack.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subtrack.ui.theme.TealAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onYearlySummaryClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentPlan by viewModel.currentPlan.collectAsState()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsState()
    val selectedDays by viewModel.notificationAdvanceDays.collectAsState() // קריאת הימים

    var showUpgradeDialog by remember { mutableStateOf(false) }

    fun checkAccess(action: () -> Unit) {
        if (currentPlan == "free") {
            showUpgradeDialog = true
        } else {
            action()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SettingsButton(title = "Subscription: ${currentPlan.uppercase()}", color = TealAccent) {
                onSubscriptionClick()
            }

            SettingsButton(title = "Yearly Summary", color = TealAccent) {
                onYearlySummaryClick()
            }

            SettingsButton(
                title = "History Archive",
                color = if (currentPlan == "free") Color.Gray else MaterialTheme.colorScheme.secondary,
                isLocked = currentPlan == "free"
            ) {
                checkAccess { onHistoryClick() }
            }

            HorizontalDivider()

            // מתג ההתראות
            NotificationSwitchCard(
                isEnabled = isNotificationsEnabled,
                isLocked = currentPlan == "free",
                onToggle = { newValue ->
                    checkAccess {
                        viewModel.toggleNotifications(newValue)
                    }
                }
            )

            // --- בחירת תזמון (רק אם ההתראות דולקות) ---
            if (isNotificationsEnabled) {
                NotificationTimingSelector(
                    selectedDays = selectedDays,
                    onOptionSelected = { days -> viewModel.setNotificationAdvanceDays(days) }
                )
            }
        }

        if (showUpgradeDialog) {
            AlertDialog(
                onDismissRequest = { showUpgradeDialog = false },
                title = { Text("Premium Feature") },
                text = { Text("This feature is available only for Premium users. Upgrade now to unlock!") },
                confirmButton = {
                    Button(onClick = {
                        showUpgradeDialog = false
                        onSubscriptionClick()
                    }) { Text("Upgrade") }
                },
                dismissButton = {
                    TextButton(onClick = { showUpgradeDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun SettingsButton(title: String, color: Color, isLocked: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(60.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Icon(
                imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun NotificationSwitchCard(
    isEnabled: Boolean,
    isLocked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (isLocked) Color.Gray else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Renewal Notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isLocked) "Premium only" else "Get notified before renewal",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                enabled = !isLocked
            )
        }
    }
}

// --- רכיב חדש: בחירת זמן ---
@Composable
fun NotificationTimingSelector(
    selectedDays: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(
            text = "Notify me:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimingChip(label = "1 Day", days = 1, isSelected = selectedDays == 1, onSelect = onOptionSelected)
            TimingChip(label = "1 Week", days = 7, isSelected = selectedDays == 7, onSelect = onOptionSelected)
            TimingChip(label = "1 Month", days = 30, isSelected = selectedDays == 30, onSelect = onOptionSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.TimingChip(
    label: String,
    days: Int,
    isSelected: Boolean,
    onSelect: (Int) -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = { onSelect(days) },
        label = { Text(label) },
        modifier = Modifier.weight(1f),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}