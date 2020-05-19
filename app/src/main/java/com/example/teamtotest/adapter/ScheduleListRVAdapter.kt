package com.example.teamtotest.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.ScheduleDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_schedule_list.view.*

//해당 날짜를 눌렀을 때 스케줄을 리스트 형식으로 보여주는 어댑터

class ScheduleListRVAdapter(var scheduleList: ArrayList<ScheduleDTO>?,val context: Context, private val PID: String?) :
    RecyclerView.Adapter<ViewHolderHelper>() {
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_list, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return scheduleList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        holder.itemView.schedule_tv_list.text = scheduleList!![position].name
        holder.itemView.schedule_color.setColorFilter(scheduleList!![position].color)

        // 스케줄 삭제하기
        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("삭제하시겠습니까?")
            builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->  })
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("scheduleList")
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                if (snapshot.child("name").value == scheduleList!![position].name) {
                                    snapshot.ref.removeValue()
                                    scheduleList!!.removeAt(position)
                                    notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onCancelled(p0: DatabaseError) {
                            Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                        }
                    })
                })
            builder.show()
            return@setOnLongClickListener true
        }
    }

}