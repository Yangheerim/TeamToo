package com.example.teamtotest

import android.util.Log
import com.example.teamtotest.dto.MembersDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class Push (val PID: String, private var message: String, private var type: String) {
    val TAG : String = "Push Class"
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private var uidList: MembersDTO? = null
    private var tokenList = arrayListOf<String>()
    private var projectName:String? = null

    init {
        findUidList()
        findFcmTokenList()
    }

    //프로젝트에 해당하는 멤버 uid 찾아주는 메소드
    private fun findUidList(){
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                projectName = snapshot.child("projectName").value.toString()
                uidList = snapshot.child("members").getValue(MembersDTO::class.java)!!
                delMyself()
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.w(TAG, p0.message)
            }
        })
    }

    //자기 자신의 uid를 제외한 나머지 uidList
    private fun delMyself(){
        val iterator = uidList?.UID_list?.iterator()

        while(iterator!!.hasNext()){
            if(firebaseAuth.currentUser!!.uid == iterator.next()){
                iterator.remove()
            }
        }
    }

    //uid에 해당하는 FcmToken 찾아주는 메소드
    private fun findFcmTokenList() {
        tokenList.clear()
        databaseReference = firebaseDatabase.getReference("UserList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    if (data.key.toString() in uidList?.UID_list!!) {
                        tokenList.add(data.child("fcmToken").value.toString())
                    }
                }
                sendPostToFCM()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.w(TAG, p0.message)
            }
        })
    }

    private fun sendPostToFCM(){
        Thread(Runnable{
            try{
                //FMC 메시지 생성 start
                val root = JSONObject()
                val notification = JSONObject()
                val data = JSONObject()
                val tokenArray = arrayListOf<String>()

                // notification object에 메시지 내용 넣어주기
                notification.put("title","[$projectName]")
                message = when(type){
                    "Todo"-> "새로운 할일: '$message'"
                    "Schedule"->"새로운 스케줄: '$message'"
                    "Todo2" -> "수정된 할일: '$message'"
                    "Schedule2"->"수정된 스케줄: '$message'"
                    "Alarm_todo"->"예정된 할일: '$message'"
                    "Alarm_schedule"->"예정된 스케줄: '$message'"
                    else -> message
                }
                notification.put("body", message)

                // 메시지 받을 사람 token array
                for (token in tokenList){
                    tokenArray.add(token)
                }
//                tokenArray.add("fEF-AKW6T_aqKDYukJd400:APA91bECLLZjp4rjqpy4cDL9G24QByAjuebUmqUB1_hutI5flwn4xhZu1Gv0iBBMLbogjycqngVFcxxfRbmS9skdC1LRkd6r2-Q28lTqv_ZFK86hupwG6sFC2AM3iJBij443SG9lOZsM")

                // data object에 PID 넣어주기
                data.put("pid", PID)

                // root object에 다 넣어주기
                root.put("notification", notification)
                root.put("registration_ids", JSONArray(tokenArray))
                root.put("data",data)
                //FMC 메시지 생성 end

                val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
                val SERVER_KEY = "AAAAbfeLNTg:APA91bHAzqgowv5v7K3NcqpFEgkkJeeVeQioqlybs48-wzKUp9Itu8sJ4LbrerBaosijqbLtSjX4iIBUCtAV1n4SwCvc_LYULy-GrsZ5yF5LHHUANaEQ4LPYyRe7dFWLf-_A1Gnf8prS"
                val url = URL(FCM_MESSAGE_URL)
                val conn : HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.doInput = true
                conn.addRequestProperty("Authorization", "key=$SERVER_KEY")
                conn.setRequestProperty("Accept","application/json")
                conn.setRequestProperty("Content-type","application/json")
                val os = conn.outputStream
                os.write(root.toString().toByteArray(Charset.forName("utf-8")))

                Log.e("os",root.toString())

                os.flush()
                val ret = conn.responseCode
//            runOnUiThread {
//                Toast.makeText(this,ret.toString(),Toast.LENGTH_SHORT).show()
//            }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }).start()
    }
}