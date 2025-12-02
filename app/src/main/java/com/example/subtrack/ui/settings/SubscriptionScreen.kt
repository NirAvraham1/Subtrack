package com.example.subtrack.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    viewModel: SubscriptionViewModel = hiltViewModel() // הזרקת ה-ViewModel
) {

    // קוראים את המנוי הנוכחי מהזיכרון
    val currentPlanId by viewModel.currentPlan.collectAsState()

    // משתנה מקומי לבחירה הזמנית במסך (לפני שלוחצים "המשך")
    var selectedPlanId by remember(currentPlanId) { mutableStateOf(currentPlanId) }

    val plans = listOf(
        SubscriptionPlan("free", "Starter", "Free", listOf("Basic Tracking", "Monthly Summary"), color = Color.Gray),
        SubscriptionPlan("premium", "Premium", "$5 / month", listOf("History Archive", "Smart Notifications", "No Ads"), color = TealAccent),
        SubscriptionPlan("ai", "Ultimate AI", "$10 / month", listOf("AI Advisor", "History", "Notifications"), isRecommended = true, color = Color(0xFFE91E63))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Your Plan", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {

            Text("Select a plan to unlock features", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
                items(plans) { plan ->
                    PlanCard(
                        plan = plan,
                        isSelected = selectedPlanId == plan.id,
                        onSelect = { selectedPlanId = plan.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // --- כאן אנחנו שומרים את הבחירה לזיכרון ---
                    viewModel.selectPlan(selectedPlanId)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (selectedPlanId == currentPlanId) "Close" else "Update Plan", fontSize = 18.sp)
            }
        }
    }
}

// ... (ה-PlanCard נשאר זהה לקוד הקודם, תשאיר אותו למטה)
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

                // כותרת ומחיר
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

                    // אם נבחר - מציג וי
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

                // רשימת הפיצ'רים
                plan.features.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50), // ירוק לפיצ'רים
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

        // תגית "מומלץ" (Recommended) צפה למעלה
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