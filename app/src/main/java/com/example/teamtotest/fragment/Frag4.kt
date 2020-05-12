package com.example.teamtotest.fragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.teamtotest.R
import com.example.teamtotest.activity.*
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
        //설정 버튼 클릭시 해당 Activity 호출

        view.setting_myInfo.setOnClickListener {
            startActivity(Intent(requireActivity(), MyInfoActivity::class.java))
        }

        view.setting_announce.setOnClickListener {
            startActivity(Intent(requireActivity(), AnnounceActivity::class.java))
        }

        view.setting_versionInfo.setOnClickListener {
            startActivity(Intent(requireActivity(), VersionInfoActivity::class.java))
        }

        view.setting_timeTable.setOnClickListener {
            startActivity(Intent(requireActivity(), TimetableActivity::class.java))
        }

        view.setting_alert.setOnClickListener {
            startActivity(Intent(requireActivity(), AlertActivity::class.java))
        }

        view.setting_help.setOnClickListener {
            startActivity(Intent(requireActivity(), HelpActivity::class.java))
        }

        return view
    }
}