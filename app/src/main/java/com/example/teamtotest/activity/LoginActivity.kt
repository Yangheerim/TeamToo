package com.example.teamtotest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.teamtotest.R
import com.example.teamtotest.dto.UserDTO
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName
    private val RC_SIGN_IN = 9001

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mListener: FirebaseAuth.AuthStateListener
    private lateinit var mSignInClient: GoogleSignInClient

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_firstScreen.visibility = View.VISIBLE

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance()

        // 파이어베이스 DB 객체 선언
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference


        init()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(mListener)
    }

    override fun onStop() {
        firebaseAuth.removeAuthStateListener(mListener)
        //signOut() //-> 이렇게 할 경우, 앱을 종료할 때마다 자동 로그아웃---> 아니야 !!! 메인에서 해야해,,,,
        super.onStop()
    }



    private fun init() {
        // Google Sign In Client를 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mSignInClient = GoogleSignIn.getClient(this, gso)

        auth_btn_google.setOnClickListener(View.OnClickListener {
            val signInIntent = mSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)    //어떤 구글 아이디로 로그인할지 정하는 화면으로 넘어감
        })

        mListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // **중요** 이미 로그인 되어있는 상태라면, 바로 메인 화면으로 전환~
            if (firebaseAuth.currentUser != null) {
                isFirstLogin()
                Log.d("First?---->", firebaseAuth.currentUser.toString())
            }else{
                login_firstScreen.visibility = View.GONE
                Log.d("First?---->", firebaseAuth.currentUser.toString())
            }
        }
    }

    private fun signInWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {// 인증 성공.
                    Toast.makeText(this, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
                    isFirstLogin()
                    finish()
                } else {// 인증 실패.
                    Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        mSignInClient.signOut()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //startActivityForResult로 보냈을 때 구글로그인에서 계정 선택 후 종료-> 다시 돌아올 때 정보를 받아서 처리하는애 ~
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val acct = task.getResult(ApiException::class.java)
                signInWithGoogle(acct!!) // 요기로 또 넘어감
            } catch (e: ApiException) {
                Log.w(TAG, "Google Sign In Failed", e)
            }

        }
    }

    private fun isFirstLogin() {
        val UID = firebaseAuth.getUid()
        databaseReference.child("UserList").child(UID!!).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue<UserDTO>(UserDTO::class.java)
                    if (user != null) {//파이어베이스 DB에 구글 로그인 한 유저의 UID 정보가 등록되어있다면
                        startActivity(Intent(this@LoginActivity,NavigationbarActivity::class.java))    // 메인화면으로 이동
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "최초로그인입니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, ExtraUserInfoActivity::class.java)) //추가정보 입력 창으로 이동
                        finish()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
        )
    }

}
