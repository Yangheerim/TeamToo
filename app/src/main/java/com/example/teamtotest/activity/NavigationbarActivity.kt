package com.example.teamtotest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.fragment.*
import kotlinx.android.synthetic.main.bottom_navigation_layout.*

class NavigationbarActivity : AppCompatActivity() {

    lateinit var fragment2 : Frag2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation_layout)

        fragment2= Frag2()

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
                ft.replace(R.id.Main_Frame, Frag1())
                ft.commit()
            }

            1 -> {
                ft.replace(R.id.Main_Frame, fragment2)
                ft.commit()
            }

            2 -> {
                ft.replace(R.id.Main_Frame, CalendarFragment())
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


}