package com.example.teamtotest.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_extra_user_info.*

class ExtraUserInfoActivity :AppCompatActivity(){
//    public lateinit var userID: EditText
//    public lateinit var duplicate_confirm_button: Button
//    public lateinit var idOkBtn: Button
//    public lateinit var stateText: TextView
    public var duplicateComplete : Boolean = false

    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    public var UserIdList:ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_user_info)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference


        userID.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                duplicateComplete= false
            }
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                duplicateComplete= false
            }
        })


        duplicate_confirm_button.setOnClickListener{
            isUsableID(userID.toString())

        }
        idOkBtn.setOnClickListener{

            if (duplicateComplete) {
                //DB에 user정보 저장 (UID는 가져와서, 입력받은id, getCurrentUser-> email, name)
                var id : String = userID.text.toString()
                Log.e("TAG","add")
                addUserInfoToDB(id)
                Log.e("TAG","finish")

                finish()
            } else {
                Toast.makeText(this@ExtraUserInfoActivity, "중복 확인을 해주세요", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun isUsableID(inputID: String) {
        // 파이어베이스 에서 데이터를 가져 옴
        //getUserIdList();
        var tmp = "testtest"
        Log.d("isUsableID ---> ", tmp)
        databaseReference = FirebaseDatabase.getInstance().getReference("UserList")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 부모가 User 인데 부모 그대로 가져오면 User 각각의 데이터 이니까 자식으로 가져와서 담아줌
                if(dataSnapshot.hasChildren()) {    //DB에 User가 있는 경우
                    //DB에 있는 정보 가져오기
                    for (snapshot in dataSnapshot.children) {
                        val user: UserDTO? = snapshot.getValue(
                            UserDTO::class.java)

                        UserIdList.add(user!!.id)
                        Log.d("USERID ---> ", user!!.id)
                    }
                    //중복 체크
                    for (i in UserIdList.indices) {

                        Log.d("저장된userlist ---> ", UserIdList[i]+i.toString())
                        if (UserIdList[i] == inputID) { // DB에 있는 id중에 입력한 id가 있으면
                            UserIdList.clear()
                            stateInfo.text = "이미 사용중인 ID입니다. 다른 ID를 입력해주세요."
                            break
                        } else {
                            UserIdList.clear()
                            stateInfo.text = "사용 가능한 ID입니다."
                            duplicateComplete = true
                            break
                        }
                    }
                }else{  //DB에 User가 없는 경우
                    UserIdList.clear()
                    stateInfo.text = "사용 가능한 ID입니다."
                    duplicateComplete = true
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
    private fun addUserInfoToDB(extraID: String) {
        // 최초로그인일 때 DB에 user 정보 저장
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        user?.let {
            val userDTO = UserDTO(
                extraID,
                it?.email.toString(),
                it?.displayName.toString()
            )
            //databaseReference = firebaseDatabase.reference
            databaseReference.child(it!!.uid).setValue(userDTO)
            startActivity(
                Intent(applicationContext, NavigationbarActivity::class.java )
            ) // 메인화면으로 이동
            Toast.makeText(this@ExtraUserInfoActivity, "로그인 성공 ♡", Toast.LENGTH_SHORT).show()
        }
    }

}
