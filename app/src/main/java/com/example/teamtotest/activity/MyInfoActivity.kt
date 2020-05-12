package com.example.teamtotest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.teamtotest.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_my_info.*

class MyInfoActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        // Toolbar
        setSupportActionBar(my_info_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 만들기


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        // 현재 로그인 이메일 표시하기
        val user_email = firebaseAuth.currentUser?.email

        email_info.setText(user_email)

        // 현재 로그인 아이디 표시하기
        val databaseReference= firebaseDatabase.getReference("UserList").child(firebaseAuth.currentUser!!.uid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if (snapshot.key == "id") {
                        val user_id = snapshot.value as String
                        id_info.setText(user_id)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
            }
        })


        // Log out
        log_out_button.setOnClickListener { view ->
            firebaseAuth.signOut()

            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // 뒤로가기 버튼 활성화
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
