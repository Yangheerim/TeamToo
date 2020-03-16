package com.example.teamtotest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.teamtotest.R
import com.example.teamtotest.adapter.ChatListAdapter
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.MessageDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var myAdapter: ChatListAdapter? = null

    //    private var drawerFrag = DrawerFragment()
//    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle : ActionBarDrawerToggle

    private var PID : String? = null
    private var projectName : String? = null
    private var howManyMembers : String? = null
    //private var userName: String? = null


    private var ChatMessageList: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
    private var ChatMessageData: HashMap<String, String> = HashMap<String, String>()


    private lateinit var dbMessageeventListener : ValueEventListener
    private lateinit var members_listener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(chat_toolbar)   // xml에서 만든 toolbar를 이 activity의 툴바로 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 만들기

        val getintent = intent /*데이터 수신*/
        if(getintent!=null) {
            PID = getintent.extras!!.getString("PID")
            //        Log.e("PID:Chat", PID)
            projectName = getintent.extras!!.getString("projectName")
            howManyMembers = getintent.extras!!.getString("howManyMembers")
            chat_toolbar.title=projectName
//            chat_how_many_members.text=howManyMembers
        }

        // adapter 연결
        myAdapter = ChatListAdapter(ChatMessageList)
        chatList_recycler_view.adapter = myAdapter
        chatList_recycler_view.setHasFixedSize(true)

        // 내 uid를 현재 있는 모든 message 객체 안에 배열에 넣는다 !!


        nav_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.drawer_members -> {
                    intent = Intent(this, AddMemberActivity::class.java)
                    intent.putExtra("PID", PID)
                    intent.putExtra("howManyMembers", howManyMembers)
                    startActivity(intent)
                    chat_drawer.closeDrawer(GravityCompat.END)
                }
//                R.id.drawer_file -> setFrag(1)
                R.id.drawer_schedule -> {
                    intent = Intent(this, ScheduleActivity::class.java)
                    intent.putExtra("PID", PID)
                    startActivity(intent)
                    chat_drawer.closeDrawer(GravityCompat.END)
                }
                R.id.drawer_todo -> {
                    intent=Intent(this,TodoActivity::class.java)
                    intent.putExtra("PID", PID)
                    startActivity(intent)
                    chat_drawer.closeDrawer(GravityCompat.END)
                }
//                R.id.drawer_finaltest -> setFrag(3)
//                R.id.drawer_exit -> setFrag(3)

                else -> println("NavigationBar ERROR!")
            }
            true
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()


        sendButton.setOnClickListener{
            if (message.length() > 0) {
                addMessageInfoToDB()
                message.setText("")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.toolbar_menu -> {
                chat_drawer.openDrawer(GravityCompat.END);
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        setListener_MessageData()
        setListener_theNumOfMembersFromMyProjects()
        readCheckToDB()
    }

    override fun onStop() {
        Log.d("here is onStop", databaseReference.toString())
        databaseReference!!.removeEventListener(dbMessageeventListener)
        Log.d("here is onStop", dbMessageeventListener.toString())
        super.onStop()
    }


    private fun addMessageInfoToDB() {
        var isReadList: ArrayList<String> = ArrayList<String>()
        isReadList.add(firebaseAuth!!.currentUser!!.uid)
        val messageDTO =
            MessageDTO(
                message.text.toString(),
                firebaseAuth!!.currentUser!!.displayName.toString(),
                firebaseAuth!!.currentUser!!.uid,
                isReadList
            )  // 유저 이름과 메세지로 message data 만들기

        val date_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = date_format.format(System.currentTimeMillis())

        databaseReference = firebaseDatabase!!.getReference()
        databaseReference =
            databaseReference!!.child("ProjectList").child(PID.toString()).child("messageList").child(date)
        databaseReference!!.setValue(messageDTO)
    }

    private fun readCheckToDB() {
        databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("messageList")
        databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val myUID: String = firebaseAuth!!.currentUser!!.uid
                // 읽은 데이터에 나의 uid 저장
                for (snapshot in dataSnapshot.children) {
                    val messageDTO = snapshot.getValue(MessageDTO::class.java)  // 데이터를 가져와서
                    if (!messageDTO!!.read!!.contains(myUID)) { // 내 uid가 없으면! 추가해준당
                        messageDTO!!.read!!.add(myUID)
                        Log.d("Add complete!! ----> ", myUID)
                        databaseReference =
                            firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                                .child("messageList").child(snapshot.key.toString())
                        databaseReference!!.setValue(messageDTO)  // 덮어쓰기
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled",
                    databaseError.toException()!!
                )
            }
        })
    }



    private fun setListener_MessageData() {

        dbMessageeventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ChatMessageList.clear()    // 갱신될 때 이미 있던 데이터는 날리기
                val myUID : String = firebaseAuth!!.currentUser!!.uid
                // 읽은 데이터에 나의 uid 저장
                for (snapshot in dataSnapshot.children) {
                    val messageDTO = snapshot.getValue(MessageDTO::class.java)  // 데이터를 가져와서
                    if(!messageDTO!!.read!!.contains(myUID)) { // 내 uid가 없으면! 추가해준당
                        messageDTO!!.read!!.add(myUID)
                        Log.d("Add complete!! ----> ", myUID)
                        databaseReference =
                            firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("messageList").child(snapshot.key.toString())
                        databaseReference!!.setValue(messageDTO)  // 덮어쓰기
                    }
                }

                // list를 보여주기 위해 db에서 데이터를 받아 adapter에 데이터 전달
                for (snapshot in dataSnapshot.children) {
                    ChatMessageData = HashMap()

                    ChatMessageData["date"] = snapshot.key!!.substring(11, 16)

                    val messageDTO = snapshot.getValue(MessageDTO::class.java)
                    ChatMessageData["who"] = messageDTO!!.who
                    ChatMessageData["message"] = messageDTO.message
                    ChatMessageData["userUID"] = messageDTO.userUID
                    ChatMessageData["isRead"] = (Integer.parseInt(howManyMembers!!) - messageDTO.read!!.size).toString()
//                    Log.d("messageReadLog", Integer.parseInt(howManyMembers!!).toString())
//                    Log.d("messageReadLog", messageDTO.read!!.size.toString())
//                    Log.d("messageReadLog", (Integer.parseInt(howManyMembers!!) - messageDTO.read!!.size).toString())
                    ChatMessageList.add(ChatMessageData)
                    myAdapter!!.notifyDataSetChanged()
                    chatList_recycler_view.scrollToPosition(ChatMessageList.size-1); // 메세지리스트의 가장 밑으로 스크롤바 위치조정! 꺄
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled",
                    databaseError.toException()!!
                )
            }
        }

        databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("messageList")
        databaseReference!!.addValueEventListener(dbMessageeventListener)       // Projectlist/PID/messageList 경로에 있는 데이터가 뭔가가 바뀌면 알려주는 listener 설정!
    }

    private fun setListener_theNumOfMembersFromMyProjects() {
        members_listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val membersDTO = dataSnapshot.getValue(MembersDTO::class.java)
                howManyMembers = membersDTO!!.UID_list!!.size.toString()
                chat_how_many_members.text = howManyMembers
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("ExtraUserInfoActivity", "loadPost:onCancelled")
            }

        }
        databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("members")
        databaseReference!!.addValueEventListener(members_listener)       // Projectlist 경로에 있는 데이터가 뭔가가 바뀌면 알려주는 listener 설정!
    }

}

