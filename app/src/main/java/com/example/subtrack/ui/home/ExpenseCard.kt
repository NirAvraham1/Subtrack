package com.example.subtrack.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subtrack.data.local.entity.Expense
import com.example.subtrack.ui.theme.ErrorRed
import com.example.subtrack.ui.theme.TealAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseCard(expense: Expense) {

    // בדיקה: האם אנחנו בתקופת ניסיון?
    val isTrial = System.currentTimeMillis() < expense.firstPaymentDate

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. האייקון
            CategoryIcon(category = expense.category)

            Spacer(modifier = Modifier.width(16.dp))

            // 2. הטקסט
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = expense.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // --- תווית FREE TRIAL ---
                    if (isTrial) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF3B8E8E), // כתום
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "FREE TRIAL",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // אם זה טריאל, אפשר להראות שהעלות היא 0 כרגע, או להשאיר את העלות העתידית
                Text(
                    text = if (isTrial) "Trial Period (Future: ${expense.amount} ₪)" else "Monthly costs: ${expense.amount} ₪",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isTrial) Color(0xFF3B8E8E) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Renewal date: ${convertMillisToDate(expense.renewalDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryIcon(category: String) {
    val (icon, color) = when (category.lowercase()) {
        "insurance", "car" -> Icons.Default.DirectionsCar to ErrorRed
        "internet", "wifi" -> Icons.Default.Wifi to TealAccent
        "home", "rent" -> Icons.Default.Home to Color(0xFFFFA500)
        else -> Icons.Default.ShoppingCart to Color(0xFF6C757D)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color(0xFFD1E4F6), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}