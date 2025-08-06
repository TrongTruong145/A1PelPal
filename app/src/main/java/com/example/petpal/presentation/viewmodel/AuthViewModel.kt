package com.example.petpal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth

    // Thay đổi 1: Chuyển từ mutableStateOf sang StateFlow để phù hợp với kiến trúc reactive
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    fun signInWithGoogleIdToken(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Thay đổi 2: Cập nhật giá trị cho StateFlow
                    _user.value = auth.currentUser
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Authentication failed")
                }
            }
    }

    // Hàm này vẫn hữu ích cho các logic không phải ở UI
    fun isSignedIn(): Boolean = auth.currentUser != null
}