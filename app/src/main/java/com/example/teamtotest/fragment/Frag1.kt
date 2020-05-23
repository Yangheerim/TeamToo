package com.example.teamtotest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.teamtotest.R
import com.example.teamtotest.adapter.FileRVAdapterMain
import com.example.teamtotest.adapter.ProgressbarAdapterMain
import com.example.teamtotest.adapter.TodoRVAdapterMain
import com.example.teamtotest.dto.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.bottombar_fragment1.*
import kotlinx.android.synthetic.main.bottombar_fragment1.view.*
import kotlinx.android.synthetic.main.item_todo_dashboard.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class Frag1 : Fragment (){

    private var todoList: ArrayList<TodoDTO> = ArrayList<TodoDTO>()
    private var fileList: ArrayList<FileDTO> = ArrayList<FileDTO>()

    private lateinit var todoRVAdapter: TodoRVAdapterMain
    private lateinit var fileRVAdapter: FileRVAdapterMain
    private lateinit var progressbarAdapterMain: ProgressbarAdapterMain
    var myProjectPIDlist: ArrayList<String> = ArrayList<String>()
    var myProjectDTOs : ArrayList<ProjectDTO?> = ArrayList<ProjectDTO?>()

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

        //
        view.dashboard_progress_recycler_view.setHasFixedSize(true)
        progressbarAdapterMain = ProgressbarAdapterMain(requireActivity(), myProjectDTOs, myProjectPIDlist)
        view.dashboard_progress_recycler_view.adapter = progressbarAdapterMain
        progressbarAdapterMain.notifyDataSetChanged()


        view.dashboard_todo_recycler_view.setHasFixedSize(true)
        todoRVAdapter = TodoRVAdapterMain(requireActivity(), todoList)
        view.dashboard_todo_recycler_view.adapter = todoRVAdapter
        todoRVAdapter.notifyDataSetChanged()

        view.dashboard_file_recycler_view.setHasFixedSize(true)
        fileRVAdapter = FileRVAdapterMain(requireActivity(), fileList)
        view.dashboard_file_recycler_view.adapter = fileRVAdapter
        fileRVAdapter.notifyDataSetChanged()

        setTodoListListener()
        setFileListListener()

        return view
    }

    private fun sortByDate(){
        //Bubble sort
        for(i in 0 until todoList.size-1){
            for(j  in 0 until todoList.size-1-i) {
                if (todoList[j].deadLine > todoList[j + 1].deadLine) {
                    val temp = todoList[j]
                    todoList[j] = todoList[j + 1]
                    todoList[j + 1] = temp
                }
            }
        }
    }

    private fun removeLastTodoList(){
        val today = Calendar.getInstance()
        var deadCal = Calendar.getInstance()

        var newTodoData : ArrayList<TodoDTO> = ArrayList<TodoDTO>()

        for (position in todoList.indices) {

            deadCal.time = Date(todoList[position].deadLine)

            val diff_day = deadCal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)
            when {
                diff_day >= 0 -> {
                    newTodoData.add(todoList[position])
                    Log.d("Not Remove Position-->", todoList[position].projectdata!!.projectName.toString())
                }
            }
        }
        todoList.clear()
        for(i in newTodoData.indices){
            todoList.add(newTodoData[i])
        }

    }


    private fun setTodoListListener(){
        val myUID = firebaseAuth.currentUser!!.uid
        val dbTodoEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                todoList.clear()
                for (projectData in dataSnapshot.children){
                    val projectDTO : ProjectDTO = projectData.getValue(ProjectDTO::class.java)!!
                    if(myProjectPIDlist.contains(projectData.key.toString())) {
//                        Log.d("TodoList1--->", projectData.key.toString())
                        for(data in projectData.children) {
                            if(data.key=="todoList") {
                                for(todoData in data.children) {
                                    val todoDTO = todoData.getValue(TodoDTO::class.java)
                                    if(todoDTO!!.performers.contains(myUID)) { // 내 할일일 경우에만 리스트에 표시
                                        todoDTO.projectdata = projectDTO
                                        todoList.add(todoDTO!!)
                                    }
                                }
                            }
                        }
                    }
                }
                removeLastTodoList()
                sortByDate()
                todoRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", databaseError.toString())
            }

        }
        databaseReference = firebaseDatabase.getReference("ProjectList")
        databaseReference.addValueEventListener(dbTodoEventListener)
    }

    private fun setFileListListener(){
        val dbFileEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fileList.clear()
                for (projectData in dataSnapshot.children){
                    val projectDTO : ProjectDTO = projectData.getValue(ProjectDTO::class.java)!!
                    if(myProjectPIDlist.contains(projectData.key.toString())) {
                        for(data in projectData.children) {
                            if(data.key=="file") {
                                for(fileData in data.children) {
                                    val fileDTO = fileData.getValue(FileDTO::class.java)
                                    fileDTO!!.projectdata = projectDTO
                                    fileList.add(fileDTO!!)
                                }
                            }
                        }
                    }
                }
                fileRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", databaseError.toString())
            }

        }
        databaseReference = firebaseDatabase.getReference("ProjectList")
        databaseReference.addValueEventListener(dbFileEventListener)
    }



    private fun findMyProjectFromFirebaseDB() {
        myProjectPIDlist.clear()
        myProjectDTOs.clear()
        val myUID = firebaseAuth.currentUser!!.uid

        databaseReference = firebaseDatabase.getReference("ProjectList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 각각 프로젝트별로, 멤버중에 나 자신이 있는지 확인.
                for (snapshot in dataSnapshot.children) {
                    val membersPerProject = snapshot.child("members").getValue(MembersDTO::class.java) // memberUID 정보를 가져옴.
                    val projectDTO : ProjectDTO? = snapshot.getValue(ProjectDTO::class.java)

                    for(projectSnapshot in snapshot.children) {

                        if (projectSnapshot.key == "progress") {
                            val progressDTO: ProgressDTO = snapshot.child("progress").getValue(ProgressDTO::class.java)!!
                            projectDTO!!.progressData = progressDTO
                            Log.d("Frag1 ProgressBar---->", progressDTO.toString())
                        }
                    }
                    for (i in 0 until membersPerProject!!.UID_list!!.size) {
                        if (membersPerProject.UID_list!![i].equals(myUID)) {
                            myProjectDTOs.add(projectDTO)
                            myProjectPIDlist.add(snapshot.key.toString())
                        }
                    }
                }
                progressbarAdapterMain.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }



}