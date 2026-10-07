package com.jozeluindev.firelogin.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.jozeluindev.firelogin.data.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    lateinit var verificationCode: String


    fun login(user: String, password: String, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = withContext(Dispatchers.IO) {
                authService.login(user, password)
            }
            if (result != null) {
                navigateToDetail()
            } else {

                //Error
            }
            _isLoading.value = false
        }

    }

    fun loginWithPhone(
        phoneNumber: String,
        activity: Activity,
        onVerificationCompleted: () -> Unit,
        onVerificationFailed: (String) -> Unit,
        onCodeSent: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {

                    //navigateToDetail() todo ha ido bien

                    viewModelScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            authService.completeRegisterWithPhoneVerification(credentials)
                        }

                        if (result != null) {
                            onVerificationCompleted()

                        }

                    }


                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    _isLoading.value = false
                    onVerificationFailed(p0.message.orEmpty())

                }

                override fun onCodeSent(
                    verificationCode: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    //cuando haya enviado el sms al movil
                    this@LoginViewModel.verificationCode = verificationCode
                    _isLoading.value = false
                    onCodeSent()
                }

            }

            withContext(Dispatchers.IO) {
                authService.loginWithPhone(phoneNumber, activity, callback)
            }

            _isLoading.value = false
        }

    }

    fun verifyCode(phoneCode: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authService.verifyCode(verificationCode, phoneCode)
            }

            if (result != null) {
                onSuccessVerification()
            }
        }
    }

    fun onGoogleLoginSelected(googleLauncherLogin: (GoogleSignInClient) -> Unit) {
        val gsc = authService.getGoogleClient()
        googleLauncherLogin(gsc)
    }

    fun loginWithGoogle(idToken: String, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authService.loginWithGoogle(idToken)
            }
            if (result != null) {
                navigateToDetail()
            }


        }
    }

    fun loginWithFacebook(accessToken: AccessToken, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authService.loginWithFacebook(accessToken)
            }
            if (result != null) {
                navigateToDetail()
            }
        }

    }
}
