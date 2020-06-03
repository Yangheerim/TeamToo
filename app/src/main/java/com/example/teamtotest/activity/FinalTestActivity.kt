package com.example.teamtotest.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.teamtotest.FinalTestResultDialog
import com.example.teamtotest.R
import com.example.teamtotest.adapter.FinalTestMemberListAdapter
import com.example.teamtotest.dto.FinalTestDateDTO
import com.example.teamtotest.dto.FinalTestResultDTO
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_final_test.*
import kotlinx.android.synthetic.main.item_final_test_member.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FinalTestActivity : AppCompatActivity() {

    private var date_listener : DatePickerDialog.OnDateSetListener? = null
    private lateinit var memberNameList : ArrayList<String>
    private lateinit var memberUIDList : ArrayList<String>
    private lateinit var myAdapter : FinalTestMemberListAdapter

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")

    private var finalTestResultList : ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()
    private lateinit var finalTestResult : HashMap<String, String>

    private var PID : String? = null
    private var complete : Boolean = false
    private val myUID: String = FirebaseAuth.getInstance().currentUser!!.uid
//    private var final_test_day :Date = null

    private lateinit var listener: ValueEventListener

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.teamtotest.R.layout.activity_final_test)

        setSupportActionBar(final_test_toolbar)         // 툴바 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //결과 확인 버튼은 모든 팀원이 최종평가를 완료했을 시 보이도록 변경, default는 invisible
        final_test_show_result_button.visibility = View.INVISIBLE


        // intent 수신
        val intent = intent /*데이터 수신*/
        PID = intent.extras!!.getString("PID")

        recyclerviewInit()

        // DB init
        firebaseDatabase = FirebaseDatabase.getInstance()

        calendarInit()

        // 멤버 정보 찾기
        findMembersUIDFromDB()
        findUserInfoOfMembersFromDB()


        // 평가 완료 후 결과 전송 버튼
        final_test_send_result_button.setOnClickListener{
            addFinalTestResultToDB()
        }

        // 결과 확인 버튼
        final_test_show_result_button.setOnClickListener{
            if(complete){   // 모든 팀원이 완료되었을 경우
                getResultFromDB()
            }else{
                Toast.makeText(this, "모든 팀원이 평가를 완료할 때까지 기다려주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        setListener_finalTestCompleteState()
        super.onStart()
    }

    override fun onStop() {
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("finalTest").child("result")
        databaseReference.removeEventListener(listener)
        super.onStop()
    }

    private fun recyclerviewInit() {
        final_test_recycler_view.setHasFixedSize(true)
        memberNameList = ArrayList<String>()
        myAdapter = FinalTestMemberListAdapter(memberNameList, this, final_test_date.text.toString())
        final_test_recycler_view.adapter = myAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calendarInit(){
        val calendar : Calendar = Calendar.getInstance()
//        final_test_date.text = ""+calendar.get(Calendar.YEAR)+" / "+(calendar.get((Calendar.MONTH))+1)+" / "+calendar.get(Calendar.DAY_OF_MONTH) // default는 오늘날짜


        date_listener = DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, year: Int, month: Int, day: Int ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("평가 날짜 지정")
            builder.setMessage("평가 날짜를 $year/${month+1}/$day 로 지정하시겠습니까?")
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    val cal : Calendar = Calendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    val test_date : Date = cal.time
                    val date_formatted = dateFormat.format(test_date)
                    val ftdate : FinalTestDateDTO = FinalTestDateDTO(date_formatted)
                    databaseReference =
                        firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("finalTest").child("test_date")
                    databaseReference.setValue(ftdate)
                    Toast.makeText(this, "평가 날짜 지정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    final_test_date.text = ftdate.date.substring(0, 4) + "/" + ftdate.date.substring(4, 6) + "/" + ftdate.date.substring(6, 8)
                    myAdapter.final_test_date_ = final_test_date.text.toString()
                })
            builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which -> })
            builder.show()
        }
        final_test_date.setOnClickListener {

            val dateDialog: DatePickerDialog = DatePickerDialog(
                this,
                date_listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dateDialog.show()
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

    private fun getResultFromDB(){
        val tmp :FinalTestActivity = this
        var resultScoreList : HashMap<String, HashMap<String, Double>> = HashMap<String, HashMap<String, Double>>()

        val maxScore : Int = 20 * memberUIDList.size

        // init score storage
        for(i in memberUIDList.indices){
            Log.d("Now i ---> ", i.toString())
            val tmpUID : String = memberUIDList[i]
            val resultScoreStorage = HashMap<String, Double>()
            resultScoreStorage["result1"]=0.0
            resultScoreStorage["result2"]=0.0
            resultScoreStorage["result3"]=0.0
            resultScoreStorage["result4"]=0.0
            resultScoreList[tmpUID] = resultScoreStorage
            Log.d("Now UID ---> ", tmpUID)
        }
        // DB에서 데이터 불러와서 각각 저장
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("finalTest").child("result")
        Log.d("Now PID ---> ", PID.toString())
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children){ // 누가했는지
                    for(resultSnapshot  in snapshot.children){ // 누구를했는지
                        val resultDTO: FinalTestResultDTO = resultSnapshot.getValue(FinalTestResultDTO::class.java)!!
                        val scoreStorage : HashMap<String, Double> = resultScoreList[resultSnapshot.key]!!
                        scoreStorage["result1"] = scoreStorage["result1"]!! + (resultDTO.result1).toDouble()
                        scoreStorage["result2"] = scoreStorage["result2"]!! + (resultDTO.result2).toDouble()
                        scoreStorage["result3"] = scoreStorage["result3"]!! + (resultDTO.result3).toDouble()
                        scoreStorage["result4"] = scoreStorage["result4"]!! + (resultDTO.result4).toDouble()
                        resultScoreList[resultSnapshot.key!!] = scoreStorage
                    }
                }
                val myResultScore: HashMap<String, Double> = resultScoreList[myUID]!!
                Log.d("Now hashMap ---> ", myResultScore.toString())
                val finalRestResultDialog = FinalTestResultDialog(tmp, FirebaseAuth.getInstance().currentUser!!.displayName.toString())
                val totalScore : Double = myResultScore["result1"]!!+myResultScore["result2"]!!+myResultScore["result3"]!!+myResultScore["result4"]!!
                finalRestResultDialog.callDialog(
                    totalScore.toString(),
                    maxScore.toString(),
                    myResultScore["result1"].toString(),
                    myResultScore["result2"].toString(),
                    myResultScore["result3"].toString(),
                    myResultScore["result4"].toString())
            }
            override fun onCancelled(p0: DatabaseError) { Log.d("ExtraUserInfoActivity", "loadPost:onCancelled") }
        })


    }

    private fun amICompleteTest(){ // 내가 이미 완료 했는지 안했는지 확인 (singleListener)
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("finalTest")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for( snapshot in dataSnapshot.children){
                    if(snapshot.key=="test_date") {
                        val tmp :FinalTestDateDTO = snapshot.getValue(FinalTestDateDTO::class.java)!!
                        Log.e("FINALTEST", tmp.toString())
                        final_test_date.text = tmp.date.substring(0, 4)+"/"+ tmp.date.substring(4, 6)+"/"+ tmp.date.substring(6, 8)
//                        final_test_day = dateFormat.parse(tmp.date)
                    }
                    if(snapshot.key=="result") {
                        for(resultSnapshot in snapshot.children) {
                            Log.e("myUID-->", myUID)
                            if (resultSnapshot.key == myUID) {
                                for (j in 1..myAdapter.itemCount) {
                                    final_test_recycler_view[j-1].item_final_test_isComplete.text =" 완료"
                                    final_test_recycler_view[j-1].item_final_test_isComplete.setTextColor(Color.parseColor("#00B700"))
                                    final_test_recycler_view[j-1].isEnabled = false   // 리스트뷰 비활성화
                                    final_test_send_result_button.visibility = View.INVISIBLE
                                    final_test_show_result_button.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                myAdapter.final_test_date_ = final_test_date.text.toString()
            }
            override fun onCancelled(p0: DatabaseError) { Log.d("ExtraUserInfoActivity", "loadPost:onCancelled") }
        })
    }

    private fun setListener_finalTestCompleteState(){
        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if((dataSnapshot.childrenCount).toInt() == memberNameList.size){  // 모든 멤버가 평가를 완료했으면
                    // 결과확인 버튼 활성화 (회색 -> 노란색으로 변경)
                    final_test_show_result_button.setBackgroundResource(R.drawable.button1)
                    complete = true

                }else{  // 테스트용 - 실제로는 평가 삭제 불가능.
                    final_test_show_result_button.setBackgroundResource(R.drawable.button2)
                    complete = false
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("ExtraUserInfoActivity", "loadPost:onCancelled")
            }

        }
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("finalTest").child("result")
        databaseReference.addValueEventListener(listener)
    }


    public fun storeFinalTestResult(position : Int, result1: String, result2 : String, result3 : String, result4 : String){
        val memberName : String = memberNameList[position]
        val memberUID : String = memberUIDList[position]
        finalTestResult = HashMap<String, String>()
        finalTestResult["memberName"] = memberName
        finalTestResult["memberUID"] = memberUID
        finalTestResult["result1"] = result1
        finalTestResult["result2"] = result2
        finalTestResult["result3"] = result3
        finalTestResult["result4"] = result4
        finalTestResultList.add(finalTestResult)
        if(finalTestResultList.size==memberNameList.size){
            final_test_send_result_button.setBackgroundResource(R.drawable.button1)
//            final_test_send_result_button.isEnabled = true
        }
    }

    private fun addFinalTestResultToDB(){
        if(finalTestResultList.size == memberNameList.size) {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("평가 결과 전송")
            builder.setMessage("평가 결과를 전송하시겠습니까? \n전송 후에는 다시 평가할 수 없습니다.")
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->

                    for (i in finalTestResultList.indices) {
                        var tmpResult: HashMap<String, String> = finalTestResultList[i]
                        val ftDTO: FinalTestResultDTO = FinalTestResultDTO(
                            tmpResult["memberName"]!!,
                            tmpResult["result1"]!!,
                            tmpResult["result2"]!!,
                            tmpResult["result3"]!!,
                            tmpResult["result4"]!!
                        )
                        databaseReference =
                            firebaseDatabase.getReference("ProjectList").child(PID.toString())
                                .child("finalTest").child("result").child(myUID).child(tmpResult["memberUID"]!!)
                        databaseReference.setValue(ftDTO)
                    }
                    final_test_send_result_button.visibility = View.INVISIBLE
                    Toast.makeText(this, "전송이 완료되었습니다. \n모든 팀원이 완료하면 결과를 확인할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    final_test_show_result_button.setBackgroundResource(R.drawable.button2)
                    final_test_show_result_button.visibility = View.VISIBLE
                })
            builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which -> })
            builder.show()

        }else{
            Toast.makeText(this, "모든 팀원의 평가를 완료해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findMembersUIDFromDB(){
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("members")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
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
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    for(i in memberUIDList.indices) {
                        if (snapshot.key == memberUIDList[i]) {
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO : UserDTO = snapshot.getValue(UserDTO::class.java)!!
                            memberNameList.add(userDTO.name)
                        }
                    }
                }
                myAdapter.notifyDataSetChanged()    // 리스트 바뀌었으니 adapter에 알려줌
                amICompleteTest()
            }
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
            }
        })
    }
}
