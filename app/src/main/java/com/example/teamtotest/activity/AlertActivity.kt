package com.example.teamtotest.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.example.teamtotest.R
import kotlinx.android.synthetic.main.activity_alert.*

class AlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)

        // 뒤로가기 버튼 만들기
        setSupportActionBar(alert_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // "알림 표시" 누르면 Activity 호출
        alert_display.setOnClickListener {
            intent = Intent(this, AlertDisplayActivity::class.java)
            startActivity(intent)
        }

        // 저장된 스위치 상태 불러오기
        val sf = getSharedPreferences("alertFile", Context.MODE_PRIVATE)
        // 알람 스위치 상태 불러오기, 저장된 상태없으면 default값은 true
        alert_msg_switch.isChecked = sf.getBoolean("msg_switch", true)

        alert_sound_switch.isChecked = sf.getBoolean("sound_switch", true)
        alert_sound_switch.isEnabled = sf.getBoolean("sound_switch_enable", true)
        sound.setTextColor(sf.getInt("sound_text", Color.BLACK))

        alert_vibrate_switch.isChecked = sf.getBoolean("vibrate_switch", true)
        alert_vibrate_switch.isEnabled = sf.getBoolean("vibrate_switch_enable", true)
        vibrate.setTextColor(sf.getInt("vibrate_text", Color.BLACK))

        alert_display.isEnabled = sf.getBoolean("display_enable", true)
        display.setTextColor(sf.getInt("display_text", Color.BLACK))

        // "메시지 알림" 스위치 설정
        // 메시지 알림이 off면 소리, 진동 switch disable
        alert_msg_switch.setOnCheckedChangeListener{compoundButton, b ->
            alert_sound_switch.isEnabled = b == true
            alert_vibrate_switch.isEnabled = b == true
            alert_display.isEnabled = b == true

            if (!b) {
                sound.setTextColor(Color.LTGRAY)
                vibrate.setTextColor(Color.LTGRAY)
                display.setTextColor(Color.LTGRAY)
            }
            else {
                sound.setTextColor(Color.BLACK)
                vibrate.setTextColor(Color.BLACK)
                display.setTextColor(Color.BLACK)
            }



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

    override fun onStop(){
        super.onStop()

        // 저장할 AlertFile 생성
        val sharedPreferences = getSharedPreferences("alertFile", Context.MODE_PRIVATE)

        // editor에 알람 스위치 상태 저장
        val editor = sharedPreferences.edit()
        editor.putBoolean("msg_switch",alert_msg_switch.isChecked)

        editor.putBoolean("sound_switch",alert_sound_switch.isChecked)
        editor.putBoolean("sound_switch_enable",alert_sound_switch.isEnabled)
        editor.putInt("sound_text",sound.currentTextColor)

        editor.putBoolean("vibrate_switch",alert_vibrate_switch.isChecked)
        editor.putBoolean("vibrate_switch_enable",alert_vibrate_switch.isEnabled)
        editor.putInt("vibrate_text",vibrate.currentTextColor)

        editor.putBoolean("display_enable",alert_display.isEnabled)
        editor.putInt("display_text",display.currentTextColor)

        editor.commit()

    }

}
