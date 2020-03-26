package com.example.teamtotest

import android.app.Dialog
import android.content.Context
import com.example.teamtotest.activity.FinalTestActivity
import kotlinx.android.synthetic.main.dialog_show_result.*

class FinalTestResultDialog(activity : FinalTestActivity, memberName : String){
    //    private var activity : FinalTestActivity = activity
    private var context : Context = activity
    private var memberName : String = memberName

    fun callDialog(total : String, max : String, r1 : String, r2 : String, r3 : String, r4 : String){
        var dialog : Dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_show_result)
        dialog.show_result_dialog_member_name.text = memberName

        dialog.show_result_dialog_totalScore.text = total
        dialog.show_result_dialog_maxScore.text = "/$max"
        dialog.show_result_dialog_text1.text = r1
        dialog.show_result_dialog_text2.text = r2
        dialog.show_result_dialog_text3.text = r3
        dialog.show_result_dialog_text4.text = r4

        dialog.show()

        dialog.show_result_dialog_ok_button.setOnClickListener{
            dialog.dismiss()
        }

    }

}