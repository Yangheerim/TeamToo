package com.example.teamtotest.fragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.teamtotest.R
import com.example.teamtotest.activity.AnnounceActivity
import com.example.teamtotest.activity.MyInfoActivity
import com.example.teamtotest.activity.NavigationbarActivity
import kotlinx.android.synthetic.main.bottombar_fragment4.view.*

class Frag4 : Fragment (){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottombar_fragment4, null)

        view.setting_myInfo.setOnClickListener {
            startActivity(Intent(requireActivity(), MyInfoActivity::class.java))
        }

        view.setting_announce.setOnClickListener {
            startActivity(Intent(requireActivity(), AnnounceActivity::class.java))
        }

        return view
    }
}