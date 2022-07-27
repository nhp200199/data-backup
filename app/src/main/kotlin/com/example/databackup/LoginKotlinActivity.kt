package com.example.databackup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.databackup.auth.repository.AuthRepository.LoginStatus
import com.example.databackup.auth.view.LoginActivity
import com.example.databackup.auth.viewmodel.LoginViewModel
import com.example.databackup.backup.view.HomeActivity
import com.example.databackup.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseUser

class LoginKotlinActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mLoginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.login_activity_title)

        mLoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        mLoginViewModel.init(this)

        if (mLoginViewModel.isUserSignedIn) {
            navigateToHome(mLoginViewModel.firebaseUser)
        }

        setUpObservables()

        binding.btnGoogleSignIn.setOnClickListener {
            val googleSignInIntent = mLoginViewModel.googleSignInIntent
            startActivityForResult(googleSignInIntent, LoginActivity.SIGN_IN_REQUEST_CODE)
        }
    }

    private fun setUpObservables() {
        mLoginViewModel.authenticatedUser.observe(
            this
        ) { firebaseUser: FirebaseUser? ->
            firebaseUser?.let { navigateToHome(it) }
        }
        mLoginViewModel.loginStatusLiveData.observe(
            this
        ) { loginStatus: LoginStatus? ->
            when (loginStatus) {
                LoginStatus.IN_PROGRESS -> showLoadingDialog()
                LoginStatus.SUCCESS -> {
                    hidePopup()
                    Toast.makeText(
                        this,
                        getString(R.string.toast_operation_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                LoginStatus.FAIL -> showInformationPopup(
                    getString(R.string.operation_popup_title_fail),
                    getString(R.string.operation_popup_msg_fail)
                )
                else -> {}
            }
        }
    }

    fun navigateToHome(currentUser: FirebaseUser) {
        val homeActivityIntent: Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(HomeActivity.EXTRA_USER_EMAIL, currentUser.email)
        }
        startActivity(homeActivityIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginActivity.SIGN_IN_REQUEST_CODE) {
            mLoginViewModel.login(this, data)
        }
    }
}