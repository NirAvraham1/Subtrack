package com.example.subtrack.ui.dialogs

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subtrack.ui.theme.TealAccent

@Composable
fun RateAppDialog(
    onDismiss: () -> Unit,
    // השינוי 1: שינינו את השם והוספנו Boolean.
    // true = תפתח חנות, false = אל תפתח חנות.
    onRateFinished: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var currentRating by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = TealAccent,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = if (currentRating == 0) "Rate SubTrack" else if (currentRating >= 4) "We love you too! 😍" else "Thanks for feedback 🙏",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tap the stars to rate your experience:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= currentRating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { currentRating = i }
                                .padding(4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (currentRating > 0) {
                Button(
                    onClick = {
                        // השינוי 2: שימוש בפונקציה החדשה
                        if (currentRating >= 4) {
                            // דירוג גבוה -> שולחים true (פתח חנות)
                            onRateFinished(true)
                        } else {
                            // דירוג נמוך -> שולחים false (אל תפתח חנות, רק סמן שבוצע)
                            Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                            onRateFinished(false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentRating >= 4) "Rate on Play Store" else "Submit Feedback",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        tonalElevation = 6.dp,
        shape = MaterialTheme.shapes.medium
    )
}