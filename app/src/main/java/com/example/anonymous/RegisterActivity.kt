package com.example.anonymous

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)






        register_button_register.setOnClickListener {
            performRegister()
        }
        already_have_an_account_textView4.setOnClickListener {
            Log.d("RegisterActivity","login open")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        profile_image_button_register.setOnClickListener {
            Log.d("RegisterActivity","try to show to photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }
var selectedPhotoUri: Uri? =null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK && data !=null){
            //check what the selected image was...
            Log.d("RegisterActivity","photo was selected")

            selectedPhotoUri = data.data
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            val bitmapDrawable =BitmapDrawable(bitmap)
            profile_image_button_register.setBackgroundDrawable(bitmapDrawable)
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
        Log.d("RegisterActivity","Email is: " +email)
        Log.d("RegisterActivity","password: $password")
        //firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                //
                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result?.user?.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","failed to create user: ${it.message}")
                Toast.makeText(this, "failed to create user: ${it.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }
    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity","file Location:$it")



                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref=FirebaseDatabase.getInstance().getReference("/user/$uid")

        val user = User(uid,username_editText_registration.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","saved the user to Firebase Data")
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","failure To Upload User data$it")
            }
    }
}

class User(val uid: String,val username: String, val profileImageUrl: String)