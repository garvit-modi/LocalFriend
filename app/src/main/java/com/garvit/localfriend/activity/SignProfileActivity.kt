package com.garvit.localfriend.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.garvit.localfriend.R
import com.garvit.localfriend.databinding.ActivitySignProfileBinding
import com.garvit.localfriend.model.User
import com.garvit.localfriend.utils.BaseActivity
import com.garvit.localfriend.utils.startAActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class SignProfileActivity : BaseActivity<ActivitySignProfileBinding>() {

    lateinit var auth: FirebaseAuth

    lateinit var downloadUrl: String
    override fun getLayoutResId(): Int = R.layout.activity_sign_profile
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    override fun setupViews() {
       dataBinding.apply {
           auth = Firebase.auth
           userImgView.setOnClickListener {
               openGallery()
           }
           nextBtn.setOnClickListener {
               if (!::downloadUrl.isInitialized) {
                   Toast.makeText(this@SignProfileActivity, "image cannot be empty", Toast.LENGTH_SHORT).show()
               } else if (nameEt.text.isEmpty()) {
                   Toast.makeText(this@SignProfileActivity, "name cannot be empty", Toast.LENGTH_SHORT).show()
               } else {
                   val user = User(nameEt.text.toString(), downloadUrl, auth.uid!!)
                   database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                      startAActivity(MainActivity::class.java)
                   }.addOnFailureListener {

                   }
               }
           }
       }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {
            intent?.data?.let { image ->
              dataBinding.  userImgView.setImageURI(image)
                uploadImage(image)
            }
        }
    }

    private fun uploadImage(image: Uri) {
       dataBinding. nextBtn.isEnabled = false
        val ref = FirebaseStorage.getInstance().reference.child("uploads/${auth.uid.toString()}")
        val uploadTask = ref.putFile(image)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                //Handle error
                Log.e("Error uploading", task.exception.toString())
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
          dataBinding.  nextBtn.isEnabled = true
            if (task.isSuccessful) {
                Log.e("Done uploading", task.result.toString())
                downloadUrl = task.result.toString()
            }

        }.addOnFailureListener {
            dataBinding.nextBtn.isEnabled = true

        }

    }
    override fun setupViewsOnResume() {

    }

    override fun onBackPressed() {


    }
}