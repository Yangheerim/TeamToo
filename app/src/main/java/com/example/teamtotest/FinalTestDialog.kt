package com.example.teamtotest

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.example.teamtotest.activity.FinalTestActivity
import kotlinx.android.synthetic.main.dialog_final_test.*
import kotlinx.android.synthetic.main.item_final_test_member.view.*

class FinalTestDialog(activity : FinalTestActivity, memberName : String, view : View, position : Int){

    private var activity : FinalTestActivity = activity
    private var context : Context = activity
    private var memberName : String = memberName
    private var viewholder : View = view
    private var position : Int = position

    @SuppressLint("ResourceAsColor", "Range")
    fun callDialog(){
        var dialog : Dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_final_test)
        dialog.final_test_dialog_member_name.text = memberName

        dialog.dialog_result1.setOnRatingBarChangeListener{ratingbar, rating, fromUser
            ->dialog.result_text1.text = rating.toString()
        }
        dialog.dialog_result2.setOnRatingBarChangeListener{ratingbar, rating, fromUser
            ->dialog.result_text2.text = rating.toString()
        }
        dialog.dialog_result3.setOnRatingBarChangeListener{ratingbar, rating, fromUser
            ->dialog.result_text3.text = rating.toString()
        }
        dialog.dialog_result4.setOnRatingBarChangeListener{ratingbar, rating, fromUser
            ->dialog.result_text4.text = rating.toString()
        }

        dialog.show()
        // 이제 여기다가는 클릭리스너? 같은거 달아주면 될듯

        dialog.final_test_dialog_complete_button.setOnClickListener{
            // FinalTestActivity에 임시저장 해두는데, 성공하면 "완료"로 변경
            val result1 : String = dialog.result_text1.text.toString()
            val result2 : String = dialog.result_text2.text.toString()
            val result3 : String = dialog.result_text3.text.toString()
            val result4 : String = dialog.result_text4.text.toString()

            if(result1=="0.0" || result2=="0.0" ||result3=="0.0" ||result4=="0.0") {    //하나라도 입력 안한게 있으면
                Toast.makeText(activity.applicationContext, "모든 항목을 모두 평가해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                activity.storeFinalTestResult(position, result1, result2, result3, result4)
                viewholder.item_final_test_isComplete.text = " 완료"
                viewholder.item_final_test_isComplete.setTextColor(Color.parseColor("#00B700"))
                dialog.dismiss()
            }

        }

    }

}