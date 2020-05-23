package com.example.teamtotest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.fragment.*
import kotlinx.android.synthetic.main.bottom_navigation_layout.*

class NavigationbarActivity : AppCompatActivity() {
    lateinit var fragment1 : Frag1
    lateinit var fragment2 : Frag2
    lateinit var fragment3 : CalendarFragment
    var time:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation_layout)

        fragment1= Frag1()
        fragment2= Frag2()
        fragment3= CalendarFragment()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> setFrag(0)
                R.id.action_chat -> setFrag(1)
                R.id.action_calender -> setFrag(2)
                R.id.action_setting -> setFrag(3)
                else -> println("NavigationBar ERROR!")
            }
            true
        }
        setFrag(0) // default 화면은 첫번째!
    }

    private fun setFrag(n: Int) {
        val ft = supportFragmentManager.beginTransaction()
        when (n) {
            0 -> {
                ft.replace(R.id.Main_Frame, fragment1)
                ft.commit()
            }

            1 -> {
                ft.replace(R.id.Main_Frame, fragment2)
                ft.commit()
            }

            2 -> {
                ft.replace(R.id.Main_Frame, fragment3)
                ft.commit()
            }

            3 -> {
                ft.replace(R.id.Main_Frame, Frag4())
                ft.commit()
            }
        }
    }

    fun toProject() {
        Log.e("LOG", "toProject")
        startActivity(Intent(this, AddProjectActivity::class.java))
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis()-time>2000){
            time=System.currentTimeMillis()
            Toast.makeText(applicationContext, "뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show()
        }else if(System.currentTimeMillis()-time<2000){
            finish()
        }
    }
}