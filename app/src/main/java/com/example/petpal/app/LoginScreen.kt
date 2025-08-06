package com.example.petpal.app

import android.widget.Toast
import androidx.compose.material3.Button
// SỬA THÀNH DÒNG NÀY:
import com.example.petpal.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.petpal.presentation.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest




@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleScope = rememberCoroutineScope()

    Button(onClick = {
        lifecycleScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false) // cho phép chọn tài khoản mới
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    viewModel.signInWithGoogleIdToken(
                        idToken = googleIdTokenCredential.idToken,
                        onSuccess = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Sign-in failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Text("Đăng nhập bằng Google")
    }
}
