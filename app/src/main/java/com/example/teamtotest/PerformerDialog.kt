package com.example.teamtotest

import android.app.Dialog
import android.util.Log
import com.example.teamtotest.activity.AddTodoActivity
import com.example.teamtotest.adapter.PerformerListAdapter
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_add_todolist_performer.*

class PerformerDialog(activity : AddTodoActivity){
    //    private var activity : AddTodoActivity = activity
    private var activity : AddTodoActivity = activity
    private lateinit var myAdapter : PerformerListAdapter

    private var memberNameList : ArrayList<String> =  ArrayList<String>()
    private var memberUIDList : ArrayList<String> =  ArrayList<String>()
    private var performerUIDList : ArrayList<String> =  ArrayList<String>()

    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference

    public var PID :String? =null

    fun callDialog(){
        var dialog : Dialog = Dialog(activity)
        dialog.setContentView(R.layout.dialog_add_todolist_performer)

        // 멤버 정보를 가져와서 리스트 만들어줌
        findMembersUIDFromDB()
        myAdapter= PerformerListAdapter(memberNameList)
        dialog.dialog_performer_recyclerview.adapter = myAdapter
        dialog.dialog_performer_recyclerview.setHasFixedSize(true)

        dialog.show()
        dialog.dialog_performer_complete_button.setOnClickListener{
            getPerformerUID()
            activity.setPerformer(performerUIDList)
            dialog.dismiss()
        }

    }

    private fun getPerformerUID(){
        var performerPositionList : ArrayList<Int> = myAdapter.getPerformerPositionList()
        for(position in performerPositionList){
            performerUIDList.add(memberUIDList[position])
        }
    }

    private fun findMembersUIDFromDB(){
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("members")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 이미 추가되어있던 팀원들의 UID를 가져와서 memberUIDList에 저장! -> 이제 이거를 DB에 UserList에 가서 해당 UID를 가진 user들의 이름을 겟 하면됨
                val membersDTO : MembersDTO = dataSnapshot.getValue(MembersDTO::class.java)!!
                memberUIDList = membersDTO.UID_list!!
                findUserInfoOfMembersFromDB()
            }
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
            }
        })
    }

    private fun findUserInfoOfMembersFromDB(){
        for (i in memberUIDList.indices){   // initialize
            memberNameList.add(" ")
        }

        databaseReference = firebaseDatabase.getReference("UserList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    for(i in memberUIDList.indices) {
                        if (snapshot.key == memberUIDList[i]) {
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO : UserDTO = snapshot.getValue(UserDTO::class.java)!!
                            memberNameList.set(i, userDTO.name)
                        }
                    }
                }
                myAdapter.notifyDataSetChanged()    // 리스트 바뀌었으니 adapter에 알려줌
            }
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
            }
        })

    }

}