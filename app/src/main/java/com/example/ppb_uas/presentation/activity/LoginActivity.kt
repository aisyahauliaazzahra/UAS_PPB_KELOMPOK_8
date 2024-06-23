package com.example.ppb_uas.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ppb_uas.R
import com.example.ppb_uas.utils.Extensions.toast
import com.example.ppb_uas.utils.FirebaseUtils.firebaseAuth
import com.example.ppb_uas.presentation.LoadingDialog

class LoginActivity : AppCompatActivity() {

    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInBtn: Button
    lateinit var emailEt: EditText
    lateinit var passEt: EditText

    lateinit var loadingDialog: LoadingDialog

    lateinit var emailError: TextView
    lateinit var passwordError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpTv = findViewById<TextView>(R.id.signUpTv)
        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)

        textAutoCheck()

        loadingDialog = LoadingDialog(this)

        signUpTv.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        signInBtn.setOnClickListener {
            checkInput()
        }
    }

    private fun textAutoCheck() {
        emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (passEt.text.length > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                passwordError.visibility = View.GONE
                if (count > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }
        })
    }

    private fun checkInput() {
        if (emailEt.text.isEmpty()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Email Can't be Empty"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
            emailError.visibility = View.VISIBLE
            emailError.text = "Enter Valid Email"
            return
        }
        if (passEt.text.isEmpty()) {
            passwordError.visibility = View.VISIBLE
            passwordError.text = "Password Can't be Empty"
            return
        }

        if (passEt.text.isNotEmpty() && emailEt.text.isNotEmpty()) {
            emailError.visibility = View.GONE
            passwordError.visibility = View.GONE
            signInUser(emailEt.text.toString().trim(), passEt.text.toString().trim())
        }
    }

    private fun signInUser(email: String, password: String) {
        loadingDialog.startLoadingDialog()

        // Check hardcoded credentials
        if (email == "aisyah@gmail.com" && password == "uasppb") {
            loadingDialog.dismissDialog()
            startActivity(Intent(this, HomeActivity::class.java))
            toast("Signed in successfully")
            finish()
        } else {
            // Firebase authentication for other users
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { signIn ->
                    loadingDialog.dismissDialog()
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        toast("Signed in successfully")
                        finish()
                    } else {
                        val exceptionMessage = signIn.exception?.message ?: "Unknown error"
                        toast("Sign in failed: $exceptionMessage")
                    }
                }
        }
    }
}
