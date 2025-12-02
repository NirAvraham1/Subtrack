package com.example.subtrack.ui.ai_advisor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.dao.ExpenseDao
import com.example.subtrack.data.local.entity.Expense
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// מבנה של הודעה בצ'אט
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)

@HiltViewModel
class GeminiAdvisorViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
) : ViewModel() {

    // --- המפתח שלך כאן (אל תשכח למחוק לפני העלאה!) ---
    private val apiKey = "API_KEY_HERE"

    // המודל של ג'מיני
    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = apiKey
    )

    // רשימת ההודעות ל-UI
    var messages by mutableStateOf(listOf(
        ChatMessage("Hello! I'm your AI Financial Advisor. I have access to your subscription data. Ask me anything!", false)
    ))
        private set

    fun sendMessage(userMessage: String) {
        // 1. הוסף את הודעת המשתמש למסך
        messages = messages + ChatMessage(userMessage, true)

        // 2. הוסף בועת "חושב..." (Loading)
        messages = messages + ChatMessage("", false, isLoading = true)

        viewModelScope.launch {
            try {
                // 3. שליפת כל הנתונים מהדאטה-בייס כדי לתת ל-AI הקשר
                val expenses = expenseDao.getAllExpensesRaw().first()
                val contextPrompt = buildContextPrompt(expenses)

                // 4. בניית ההודעה המלאה ל-AI (הקשר + השאלה)
                val fullPrompt = "$contextPrompt\n\nUser Question: $userMessage"

                // 5. שליחה לג'מיני
                val response = generativeModel.generateContent(fullPrompt)

                // 6. הסרת הבועה של "חושב" והוספת התשובה האמיתית
                messages = messages.dropLast(1) + ChatMessage(response.text ?: "Sorry, I couldn't understand that.", false)

            } catch (e: Exception) {
                messages = messages.dropLast(1) + ChatMessage("Error: ${e.localizedMessage}", false)
            }
        }
    }

    // פונקציית עזר: הופכת את רשימת ההוצאות לטקסט שג'מיני מבין
    private fun buildContextPrompt(expenses: List<Expense>): String {
        val expensesListString = expenses.joinToString("\n") {
            "- ${it.name}: ${it.amount} (${it.frequency}, Category: ${it.category})"
        }

        return """
            You are a helpful financial advisor inside an app called SubTrack.
            Here is the user's current subscription data:
            
            $expensesListString
            
            Please answer the user's question based on this data. 
            Keep your answers short, concise, and helpful.
            If the user asks about savings, suggest which subscriptions look expensive.
        """.trimIndent()
    }
}