package com.example.teamtotest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.teamtotest.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_info.*

class MyInfoActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        // Toolbar
        setSupportActionBar(my_info_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 만들기


        // Log out
        firebaseAuth = FirebaseAuth.getInstance()

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
