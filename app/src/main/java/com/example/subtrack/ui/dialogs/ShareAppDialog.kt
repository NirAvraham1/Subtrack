package com.example.subtrack.ui.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subtrack.ui.theme.TealAccent

@Composable
fun ShareAppDialog(
    onDismiss: () -> Unit,
    onShareClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = TealAccent,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Share with Friends",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = "Help your friends save money too! Share SubTrack with them and help them track their subscriptions efficiently.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onShareClick,
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent)
            ) {
                Text("Share App", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        tonalElevation = 6.dp,
        shape = MaterialTheme.shapes.medium
    )
}