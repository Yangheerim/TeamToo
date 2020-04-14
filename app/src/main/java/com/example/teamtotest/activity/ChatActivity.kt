package com.example.teamtotest.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.teamtotest.Push
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
    val tmp = 3

//    private var drawerFrag = DrawerFragment()
//    private lateinit var drawerLayout: DrawerLayout
//    private lateinit var drawerToggle : ActionBarDrawerToggle

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
            projectName = getintent.extras!!.getString("projectName")
            howManyMembers = getintent.extras!!.getString("howManyMembers")
            chat_toolbar.title=projectName
        }

        // adapter 연결
        myAdapter = ChatListAdapter(ChatMessageList)
        chatList_recycler_view.adapter = myAdapter
        chatList_recycler_view.setHasFixedSize(true)

        // 내 uid를 현재 있는 모든 message 객체 안에 배열에 넣는다 !!


        nav_view.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.drawer_members -> {
                    chat_drawer.closeDrawer(GravityCompat.END)
                    chat_drawer.clearFocus()
                    intent = Intent(this, AddMemberActivity::class.java)
                    intent.putExtra("PID", PID)
                    intent.putExtra("howManyMembers", howManyMembers)
                    startActivity(intent)
                }
//                R.id.drawer_file -> setFrag(1)
                R.id.drawer_schedule -> {
                    chat_drawer.closeDrawer(GravityCompat.END)
                    intent = Intent(this, ScheduleActivity::class.java)
                    intent.putExtra("PID", PID)
                    startActivity(intent)

                }
                R.id.drawer_file -> {
                    chat_drawer.closeDrawer(GravityCompat.END)
                    intent = Intent(this, FileActivity::class.java)
                    intent.putExtra("PID", PID)
                    startActivity(intent)
                }

                R.id.drawer_todo -> {
                    chat_drawer.closeDrawer(GravityCompat.END)
                    intent=Intent(this,TodoActivity::class.java)
                    intent.putExtra("PID", PID)
                    startActivity(intent)

                }
                R.id.drawer_finaltest -> {
                    // 코드 추가 해야함
                    chat_drawer.closeDrawer(GravityCompat.END)
                    intent=Intent(this,FinalTestActivity::class.java)
                    intent.putExtra("PID", PID)
                    startActivity(intent)
                }
                R.id.drawer_exit -> {
                    chat_drawer.closeDrawer(GravityCompat.END)
                    exitProject()
                }
                else -> println("NavigationBar ERROR!")
            }
            true
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()


        sendButton.setOnClickListener{
            if (message.length() > 0) {
                addMessageInfoToDB()
                Push(PID.toString(), message.text.toString())
                message.setText("")
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat_toolbar, menu)
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
        // 리스너 삭제
        databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("messageList")
        databaseReference!!.removeEventListener(dbMessageeventListener)
        databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("members")
        databaseReference!!.removeEventListener(members_listener)
        super.onStop()
    }

    private fun exitProject(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("던지고 나가기")
        builder.setMessage("정말로 나가시겠습니까? 학점은 보장할 수 없습니다...")
        builder.setPositiveButton("예",
            DialogInterface.OnClickListener { dialog, which ->
                val myUID = firebaseAuth!!.currentUser!!.uid

                databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            if (snapshot.key == "members") { // memberList에서 삭제
                                val membersDTO: MembersDTO = snapshot.getValue(MembersDTO::class.java)!!
                                if (membersDTO.UID_list!!.size <= 1) {// 나 혼자 남아있었다면 프로젝트 전체 삭제
                                    firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                                        .removeValue()
                                    // 리스너 삭제
                                    databaseReference =
                                        firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                                            .child("messageList")
                                    databaseReference!!.removeEventListener(dbMessageeventListener)
                                    databaseReference =
                                        firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                                            .child("members")
                                    databaseReference!!.removeEventListener(members_listener)
                                    finish()
                                    break
                                } else { // 아니라면 memberList에서 내 정보만 삭제
                                    membersDTO.UID_list!!.remove(myUID)
                                    firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                                        .child("members").setValue(membersDTO)
                                }
                            }
                            if (snapshot.key == "messageList") {
//                        for(messageSnapshot in snapshot.children){  // 내가 보낸 메세지들의 보낸사람 이름을 알수없음으로
//                            val messageDTOtoRemove: MessageDTO? = messageSnapshot.getValue(MessageDTO::class.java)
//                            if(messageDTOtoRemove!!.userUID == myUID){
//                                databaseReference!!.child("messageList").child(messageSnapshot.key.toString()).removeValue().addOnSuccessListener {
//                                    Toast.makeText(this@ChatActivity, "messageList에서 삭제완료!", Toast.LENGTH_SHORT).show()
//                                }.addOnFailureListener{
//                                    Toast.makeText(this@ChatActivity, "messageList에서 삭제실패..", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
                                for (messageSnapshot in snapshot.children) {// 읽은 사람 목록에서 나 삭제
                                    val messageDTOtoRemove: MessageDTO? =
                                        messageSnapshot.getValue(MessageDTO::class.java)
                                    if (messageDTOtoRemove!!.read!!.contains(myUID)) {
                                        messageDTOtoRemove!!.read!!.remove(myUID)
                                        firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                                            .child("messageList").child(messageSnapshot.key.toString())
                                            .setValue(messageDTOtoRemove)
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
                    }
                })
                onStop()
                finish()
            })
        builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which -> })
        builder.show()


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
//        val date_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val current = Date()
        val utc = Date(current.time - Calendar.getInstance().timeZone.getOffset(current.time))

        databaseReference = firebaseDatabase!!.getReference()
        databaseReference =
            databaseReference!!.child("ProjectList").child(PID.toString()).child("messageList").child(utc.toString())
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

                // list를 보여주기 위해 db에서 데이터를 받아 adapter에 데이터 전달
                for (snapshot in dataSnapshot.children) {
                    ChatMessageData = HashMap()

                    val utc = Date(snapshot.key)
                    val date = Date(utc.time + Calendar.getInstance().timeZone.getOffset(utc.time))
                    Log.e("dateTest", date.toString())
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val date_formatted = dateFormat.format(date)

                    ChatMessageData["date"] = date_formatted.substring(11, 16)

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

