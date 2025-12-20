package com.example.subtrack.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subtrack.ui.theme.TealAccent

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val price: String,
    val features: List<String>,
    val isRecommended: Boolean = false,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val currentPlanId by viewModel.currentPlan.collectAsState()
    var selectedPlanId by remember(currentPlanId) { mutableStateOf(currentPlanId) }

    // --- משתנה לניהול הפופ-אפ ---
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val plans = listOf(
        SubscriptionPlan("free", "Starter", "Free", listOf("Basic Tracking", "Monthly Summary"), color = Color.Gray),
        SubscriptionPlan("premium", "Premium", "$5 / month", listOf("History Archive", "Smart Notifications", "No Ads"), color = TealAccent),
        SubscriptionPlan("ai", "Ultimate AI", "$10 / month", listOf("AI Advisor", "History", "Notifications"), isRecommended = true, color = Color(0xFFE91E63))
    )

    // מציאת אובייקט התוכנית שנבחרה (כדי להציג את שמה בדיאלוג)
    val selectedPlan = plans.find { it.id == selectedPlanId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Your Plan", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Unlock the full potential of SubTrack",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(plans) { plan ->
                    PlanCard(
                        plan = plan,
                        isSelected = selectedPlanId == plan.id,
                        onSelect = { selectedPlanId = plan.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- הכפתור הראשי ---
            Button(
                onClick = {
                    if (selectedPlanId == currentPlanId) {
                        // אם לא שינינו כלום, פשוט סוגרים את המסך
                        onNavigateBack()
                    } else {
                        // אם שינינו תוכנית -> פותחים את הדיאלוג לאישור
                        showConfirmationDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPlanId == "free") Color.Gray else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (selectedPlanId == currentPlanId) "Close" else "Update Plan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- דיאלוג אישור החלפת מנוי ---
        if (showConfirmationDialog && selectedPlan != null) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Confirm Subscription",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to switch your plan to ${selectedPlan.title}?\n\nPrice: ${selectedPlan.price}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // --- כאן קורית השמירה האמיתית ---
                            viewModel.selectPlan(selectedPlanId)
                            showConfirmationDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmationDialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) plan.color else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(borderWidth, borderColor),
            elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) plan.color else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = plan.price,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = plan.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                plan.features.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (plan.isRecommended) {
            Surface(
                color = plan.color,
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RECOMMENDED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}