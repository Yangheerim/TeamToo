package com.example.teamtotest

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.teamtotest.dto.MembersDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class Push (val PID: String, private val message: String) {
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

                notification.put("title",projectName)
                notification.put("body", message)
                data.put("pid", PID)
                root.put("notification", notification)
                for (token in tokenList){
                    tokenArray.add(token)
                }
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