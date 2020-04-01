package com.example.teamtotest.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.adapter.MemberListAdapter2
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_member.*
import java.util.*

class AddMemberActivity : AppCompatActivity() {
 //희림아 안되니?
    private var memberNameList = ArrayList<String>()    // 팀원들의 이름을 저장
    private var memberUIDList = ArrayList<String>()    // 팀원들의 UID를 저장
    private var UserUIDList = ArrayList<String>()      // user UID 임시저장
    private var UserIdList = ArrayList<String>()       // user id 임시저장
    private var UserNameList = ArrayList<String>()     // user name 임시저장

    private lateinit var myAdapter: MemberListAdapter2

    private var a=3
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var PID : String? = null
    private var theNumberOfOriginMembers : Int = 0
    private var index: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        setSupportActionBar(add_member_toolbar)         // 툴바 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseDatabase = FirebaseDatabase.getInstance()

        val intent = intent /*데이터 수신*/
        PID = intent.extras!!.getString("PID")
        add_member_how_many_members.text = intent.extras!!.getString("howManyMembers")
        theNumberOfOriginMembers =Integer.parseInt(intent.extras!!.getString("howManyMembers")!!)
        add_member_add_members_text.visibility = View.INVISIBLE


        // 1. 이미 추가되어있는 멤버들의 리스트를 보여주기
        recyclerInit()
        findMembersUIDFromDB()
        findUserInfoOfMembersFromDB()

        // 2. 입력한 id를 가진 user가 있는지 검색 -> 추가하면 UserList에 이름동그라미로 보이게
        //검색기능
        add_member_search.setOnClickListener {
            if (add_member_search_id_input.text.toString().isEmpty()) {
                Toast.makeText(this@AddMemberActivity, "추가할 팀원의 아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                findIdFromDB(add_member_search_id_input.text.toString())
            }
        }
        //검색된 id를 이름 리스트에 추가
        add_member_add_members_text.setOnClickListener(View.OnClickListener { addTeamMemberToList() })

        // 3. 그다음에 추가하기 버튼누르면 이미 있던애들 말고 새로 추가된 애들만 DB에 저장 ( ProjectList/PID/member/~ ) -> 그냥 덮어쓰기..?
        add_member_createButton.setOnClickListener{
            addNewMembersToDB()
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

    private fun recyclerInit(){
        add_member_recycler_view.setHasFixedSize(true)
        myAdapter = MemberListAdapter2(memberNameList,this)
        add_member_recycler_view.adapter = myAdapter
    }


    private fun addNewMembersToDB() {
        // 새로운 멤버 정보 DB에 저장
        when {
            memberUIDList.size == theNumberOfOriginMembers -> Toast.makeText(this@AddMemberActivity, "추가된 팀원이 없습니다.", Toast.LENGTH_SHORT).show()
            else -> {
                val membersDTO = MembersDTO(memberUIDList)
                databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("members")
                databaseReference.setValue(membersDTO)  // 덮어쓰기 가능,,,?

                finish()   // 현재 액티비티(팀원추가) 종료
            }
        }
    }

    public fun deleteMemberList(position : Int){

        if(position > theNumberOfOriginMembers-1) {
            memberNameList.removeAt(position)
            memberUIDList.removeAt(position)
            myAdapter.notifyDataSetChanged()
            add_member_how_many_members.text = memberNameList.size.toString() + ""
        }else{
//            Toast.makeText(this@AddMemberActivity, "원래 있던애들은 못지운닷", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTeamMemberToList() {
        if (!isTheUserAlreadyAdded()) {
            memberUIDList.add(UserUIDList[index])      // Member 리스트에 추가
            memberNameList.add(UserNameList[index])    // Member 리스트에 추가
            myAdapter.notifyDataSetChanged()   // 데이터 바뀐거 어뎁터한테 알려주면 -> 리사이클러뷰 refresh
            add_member_how_many_members.text = memberNameList.size.toString() + ""
            add_member_userInfo.text = "아이디를 입력해주세요"          // 추가 했으니까 다시 숨겨놓음
            add_member_add_members_text.visibility = View.INVISIBLE
            add_member_search_id_input.text = null
            index = -1
        } else {
            Toast.makeText(this@AddMemberActivity, "이미 추가된 팀원입니다", Toast.LENGTH_SHORT).show()
        }
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


    //Recyclerview에 이미 추가되어있는 팀원들 리스트 보이게 - DB에서 UID값 가져와서 UserList에서 찾아서 이름동그라미로 나타내기
    private fun findMembersUIDFromDB(){
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("members")
        databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 이미 추가되어있던 팀원들의 UID를 가져와서 memberUIDList에 저장! -> 이제 이거를 DB에 UserList에 가서 해당 UID를 가진 user들의 이름을 겟 하면됨
                val membersDTO : MembersDTO = dataSnapshot.getValue(MembersDTO::class.java)!!
                memberUIDList = membersDTO.UID_list!!
            }
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
            }
        })
    }

    private fun findUserInfoOfMembersFromDB(){
        databaseReference = firebaseDatabase.getReference("UserList")
        databaseReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    for(i in memberUIDList.indices) {
                        if (snapshot.key == memberUIDList[i]) {
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO : UserDTO = snapshot.getValue(UserDTO::class.java)!!
                            memberNameList.add(userDTO.name)
                            Log.d("LOG: 찾음!! ---->", userDTO.name);
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

    private fun findIdFromDB(inputID: String) {

        databaseReference = firebaseDatabase.getReference("UserList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    UserUIDList.add(snapshot.key.toString())
                    // 모든 유저의 id 리스트를 가져와 UserIdList 배열에 넣는다.
                    val user = snapshot.getValue(UserDTO::class.java)
                    UserIdList.add(user!!.id)
                    UserNameList.add(user.name)
                }

                for (i in UserIdList.indices) {
                    //Log.d("저장된userlist ---> ", UserIdList.get(i));
                    if (UserIdList[i] == inputID) { // 입력한 id를 가진 user를 찾으면
                        Log.e("FIND THIS ID!! ---> ", inputID)
                        Log.e("index ---> ", index.toString() + "")
                        add_member_userInfo.text = "" + UserNameList[i] + " / " + UserIdList[i]
                        add_member_add_members_text.visibility = View.VISIBLE  // 추가기능 활성화
                        index =i // user의 UID와 name이 담긴 index 저장해두기. --> 나중에 DB에 데이터 넣을때랑, 동그라미이름으로 보여줄 때 사용
                        break
                    } else {
                        add_member_userInfo.text = "검색 결과가 없습니다"
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


}