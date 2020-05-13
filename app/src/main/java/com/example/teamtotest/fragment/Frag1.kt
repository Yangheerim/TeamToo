package com.example.teamtotest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.teamtotest.R
import com.example.teamtotest.adapter.TodoRVAdapterMain
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.bottombar_fragment1.view.*

class Frag1 : Fragment (){

    private var todoList: ArrayList<TodoDTO> = ArrayList<TodoDTO>()
    private lateinit var todoRVAdapter: TodoRVAdapterMain
    var myProjectPIDlist: ArrayList<String> = ArrayList<String>()

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var PID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottombar_fragment1, null)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        findMyProjectFromFirebaseDB() // 내 프로젝트 찾음


        view.dashboard_recycler_view.setHasFixedSize(true)
        todoRVAdapter = TodoRVAdapterMain(requireActivity(), todoList)
        view.dashboard_recycler_view.adapter = todoRVAdapter
        todoRVAdapter.notifyDataSetChanged()

        setTodoListListener()

        return view
    }


    private fun setTodoListListener(){
        val myUID = firebaseAuth.currentUser!!.uid
        val dbTodoEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                todoList.clear()
                for (projectData in dataSnapshot.children){
                    if(myProjectPIDlist.contains(projectData.key.toString())) {
//                        Log.d("TodoList1--->", projectData.key.toString())
                        for(data in projectData.children) {
                            if(data.key=="todoList") {
                                for(todoData in data.children) {
                                    val todoDTO = todoData.getValue(TodoDTO::class.java)
                                    if(todoDTO!!.performers.contains(myUID)) { // 내 할일일 경우에만 리스트에 표시
                                        todoList.add(todoDTO!!)
                                    }
                                }
                            }
                        }
                    }
                }
                todoRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", databaseError.toString())
            }

        }
        databaseReference = firebaseDatabase.getReference("ProjectList")
        databaseReference.addValueEventListener(dbTodoEventListener)
    }

    private fun findMyProjectFromFirebaseDB() {
        myProjectPIDlist.clear()
        val myUID = firebaseAuth.currentUser!!.uid

        databaseReference = firebaseDatabase.getReference("ProjectList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 각각 프로젝트별로, 멤버중에 나 자신이 있는지 확인.
                for (snapshot in dataSnapshot.children) {
                    //ProjectInfo.put("PID", snapshot.getKey());
                    val membersPerProject = snapshot.child("members")
                        .getValue(MembersDTO::class.java) // memberUID 정보를 가져옴.

                    for (i in 0 until membersPerProject!!.UID_list!!.size) {
                        if (membersPerProject!!.UID_list!![i].equals(myUID)) {
                            myProjectPIDlist.add(snapshot.key.toString())
                        }
                    }
                }
                //                setListener_DataFromMyProjects();
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}