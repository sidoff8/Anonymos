package com.example.anonymous

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
        }
        already_have_an_account_textView4.setOnClickListener {
            Log.d("MainActivity","login open")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
    private fun performRegister(){
        val email = email_editText_registration.text.toString()
        val password = password_editText_registration.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT)
                .show()
            return
        }
        Log.d("MainActivity","Email is: " +email)
        Log.d("MainActivity","password: $password")
        //firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                //
                Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Log.d("Main","failed to create user: ${it.message}")
                Toast.makeText(this, "failed to create user: ${it.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
}
