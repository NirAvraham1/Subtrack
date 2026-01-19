package com.example.subtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _activeDialog = MutableStateFlow<DialogType?>(null)
    val activeDialog = _activeDialog.asStateFlow()

    // enum כדי שיהיה מסודר
    enum class DialogType { RATE, SHARE, UPDATE }

    // אתחול: בדיקת תנאים
    init {
        checkDialogs()
    }

    private fun checkDialogs() {
        viewModelScope.launch {
            val currentVersion = 1
            val remoteVersion = 1
            if (remoteVersion > currentVersion) {
                _activeDialog.value = DialogType.UPDATE
                return@launch
            }

            val hasRated = userPreferences.hasUserRatedFlow.first()

            if (!hasRated) {
                val shouldShowRate = userPreferences.shouldShowDialog("rate").first()
                if (shouldShowRate) {
                    _activeDialog.value = DialogType.RATE
                    return@launch
                }
            }


            val shouldShowShare = userPreferences.shouldShowDialog("share").first()
            if (shouldShowShare) {
                _activeDialog.value = DialogType.SHARE
            }
        }
    }


    fun onUserRated() {
        viewModelScope.launch {
            userPreferences.markAppAsRated()
            _activeDialog.value = null
        }
    }

    fun onDialogDismissed(type: DialogType) {
        viewModelScope.launch {
            _activeDialog.value = null
            if (type == DialogType.RATE) userPreferences.saveDialogDismissTime("rate")
            if (type == DialogType.SHARE) userPreferences.saveDialogDismissTime("share")
        }
    }
}