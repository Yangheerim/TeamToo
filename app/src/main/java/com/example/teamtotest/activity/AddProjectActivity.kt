package com.example.teamtotest.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.adapter.MemberListAdapter
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.ProjectDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_project.*
import java.util.*

class AddProjectActivity : AppCompatActivity() {
    private var index: Int = -1
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var memberUIDList = ArrayList<String>()    // 초대한 user의 UID 저장
    private var memberNameList = ArrayList<String>()   // 초대한 user의 name 저장
    private var UserUIDList = ArrayList<String>()      // user UID 임시저장
    private var UserIdList = ArrayList<String>()       // user id 임시저장
    private var UserNameList = ArrayList<String>()     // user name 임시저장

    private lateinit var myAdapter: MemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)

        setSupportActionBar(add_project_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        userInfo.text = "아이디를 입력해주세요"
        add_members_text.visibility = View.INVISIBLE

        memberUIDList.add(firebaseAuth.currentUser!!.uid)
        memberNameList.add(firebaseAuth.currentUser!!.displayName.toString())

        recyclerInit()

        add_members_text.setOnClickListener(View.OnClickListener { addTeamMember() })

        search.setOnClickListener {
            if (search_id_input.text.toString().isEmpty()) {
                Toast.makeText(this@AddProjectActivity, "추가할 팀원의 아이디를 입력해주세요", Toast.LENGTH_SHORT)
                    .show()
            } else {
                findIdFromDB(search_id_input.text.toString())
            }
        }

        createButton.setOnClickListener {
            addUserInfoToDB()
            //Log.d("Add Activity End!", "testtest");

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun recyclerInit() {
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        member_recycler_view.setHasFixedSize(true)
        myAdapter = MemberListAdapter(memberNameList, this)
        member_recycler_view.adapter = myAdapter
    }

    private fun isTheUserAlreadyAdded(): Boolean {
        Log.d("지금 확인중인 user UID->", UserUIDList[index])
        Log.d("index ->", index.toString() + "")
        for (i in memberUIDList.indices) {
            Log.d("이미 등록된 팀원->", memberUIDList[i])
            if (memberUIDList[i] == UserUIDList[index]) {
                return true
            }
        }
        return false
    }

    private fun addTeamMember() {
        if (!isTheUserAlreadyAdded()) {
            memberUIDList.add(UserUIDList[index])      // Member 리스트에 추가
            memberNameList.add(UserNameList[index])    // Member 리스트에 추가
            myAdapter.notifyDataSetChanged()   // 데이터 바뀐거 어뎁터한테 알려주면 -> 리사이클러뷰 refresh
            how_many_members.text = memberNameList.size.toString() + ""
            userInfo.text = "아이디를 입력해주세요"          // 추가 했으니까 다시 숨겨놓음
            add_members_text.visibility = View.INVISIBLE
            search_id_input.text = null
            index = -1
        } else {
            Toast.makeText(this@AddProjectActivity, "이미 추가된 팀원입니다", Toast.LENGTH_SHORT).show()
        }
    }

    public fun deleteMemberList(position : Int){

        if(position!=0) {
            memberNameList.removeAt(position)
            memberUIDList.removeAt(position)
            myAdapter.notifyDataSetChanged()
            how_many_members.text = memberNameList.size.toString() + ""
        }else{
//            Toast.makeText(this@AddProjectActivity, "너 자신을 지우려하지 말라", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearLists() {
        UserNameList.clear()
        UserIdList.clear()
        UserUIDList.clear()
    }

    private fun findIdFromDB(inputID: String) {
        clearLists()
        //***코드 고쳐야함
        databaseReference = firebaseDatabase.getReference("UserList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    UserUIDList.add(snapshot.key.toString())
                    Log.e("IS THIS UID? ---> ", snapshot.key!!)
                    // 모든 유저의 id 리스트를 가져와 UserIdList 배열에 넣는다.
                    val user = snapshot.getValue(UserDTO::class.java)
                    //memberUIDList.add(user.getUID);
                    UserIdList.add(user!!.id)
                    UserNameList.add(user.name)
                    //Log.d("USERID ---> ", user.getId());

                }

                for (i in UserIdList.indices) {
                    //Log.d("저장된userlist ---> ", UserIdList.get(i));
                    if (UserIdList[i] == inputID) { // 입력한 id를 가진 user를 찾으면
                        Log.e("FIND THIS ID!! ---> ", inputID)
                        Log.e("index ---> ", index.toString() + "")
                        userInfo.text = "" + UserNameList[i] + " / " + UserIdList[i]
                        //userInfo.setVisibility(View.VISIBLE); // 숨겨놨던 뷰에 데이터를 담아서 보여주고
                        add_members_text.visibility = View.VISIBLE // 추가기능도 활성화
                        index =
                            i // user의 UID와 name이 담긴 index 저장해두기. --> 나중에 DB에 데이터 넣을때랑, 동그라미이름으로 보여줄 때 사용
                        break
                    } else {
                        userInfo.text = "검색 결과가 없습니다"
                        //userInfo.setTextColor(Color.GRAY);
                        //userInfo.setVisibility(View.VISIBLE); // 숨겨놨던 뷰에 데이터를 담아서 보여줌
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun addUserInfoToDB() {
        // 프로젝트 생성 시 프로젝트 명과 멤버 정보 DB에 저장
        when {
            project_name.text.toString().isEmpty() -> Toast.makeText(this@AddProjectActivity, "프로젝트 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            memberUIDList.size <= 1 -> Toast.makeText(this@AddProjectActivity, "팀원을 추가해주세요", Toast.LENGTH_SHORT).show()
            else -> {
//                val user = FirebaseAuth.getInstance().currentUser
                val projectDTO = ProjectDTO(project_name.text.toString())
                val membersDTO = MembersDTO(memberUIDList)
                databaseReference = firebaseDatabase.reference
                databaseReference = databaseReference.child("ProjectList").push()
                databaseReference.setValue(projectDTO)
                databaseReference.child("members").setValue(membersDTO)
                finish()   // 현재 액티비티 종료
            }
        }
    }
}