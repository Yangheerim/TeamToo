package com.example.teamtotest.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.adapter.FileAdapter
import com.example.teamtotest.dto.FileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_file.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class FileActivity : AppCompatActivity(){


    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null

    private val fileInfoList: ArrayList<HashMap<String, String>> = ArrayList<HashMap<String, String>>()

    private lateinit var listener: ValueEventListener

    private var PID: String?=null

    lateinit var myAdapter: FileAdapter

//    private val TAG : String = "FileActivity"

    private lateinit var filePath : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)

        setSupportActionBar(file_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent /*데이터 수신*/
        PID = intent.extras!!.getString("PID")

        recyclerviewInit()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference= firebaseDatabase!!.reference

        setListener_FileInfoFromDB()
    }

    private fun recyclerviewInit() {
        file_recycler_view.setHasFixedSize(true)
        myAdapter = FileAdapter(fileInfoList)
        file_recycler_view.adapter = myAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_file_toolbar, menu)
        return true
    }

    private fun getFileName(uri: Uri) : String? {
        var result : String? = null
        if (uri.scheme!!.equals("content"))
        {
            var cursor : Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.upload -> {
                val intent = Intent()
                intent.type = "*/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "파일을 선택하세요."), 0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Log.e("data", data.toString())
            filePath = data!!.data!!
            val fileName: String? = getFileName(filePath)
            uploadFile(fileName)
        }
    }

    private fun uploadFile(filename: String?)
    { //업로드할 파일이 있으면 수행
        if (filePath != null)
        { //storage
            val storage = FirebaseStorage.getInstance()

            //storage 주소와 폴더 파일명을 지정해 준다.
            val storageRef = storage.getReferenceFromUrl("gs://teamtogether-bdfc9.appspot.com")

            storageRef.child(filename!!).putFile(filePath) //성공시
                .addOnSuccessListener { Toast.makeText(applicationContext, "업로드 완료!", Toast.LENGTH_SHORT).show() } //실패시
                .addOnFailureListener { Toast.makeText(applicationContext, "업로드 실패!", Toast.LENGTH_SHORT).show() } //진행중

        } else {
            Toast.makeText(applicationContext, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
        }
        uploadFileInfoToDB(filename!!)
    }

    private fun uploadFileInfoToDB(fileName : String){
        var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val uid : String = firebaseAuth.currentUser!!.uid
        val userName : String = firebaseAuth.currentUser!!.displayName!!

        val date_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = date_format.format(System.currentTimeMillis())

        val fileDTO : FileDTO = FileDTO(fileName, date, uid, userName)

        databaseReference = firebaseDatabase!!.reference.child("ProjectList").child(PID.toString()).child("file").push()
        databaseReference!!.setValue(fileDTO)

    }

    private fun setListener_FileInfoFromDB(){
        listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fileInfoList.clear()

                for (snapshot in dataSnapshot.children) {
                    val fileInfo : HashMap<String, String> = HashMap<String, String>()
                    val fileDTO: FileDTO = snapshot.getValue(FileDTO::class.java)!!
                    fileInfo["fileName"] = fileDTO.fileName
                    fileInfo["date"] = fileDTO.date
                    fileInfo["uid"] = fileDTO.uid
                    fileInfo["userName"] = fileDTO.userName
                    fileInfoList.add(fileInfo)
                    myAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FileActivity", "loadPost:onCancelled",
                    databaseError.toException()
                )
            }
        }

        databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("file")
        databaseReference!!.addValueEventListener(listener)

    }

    override fun onStop() {
        databaseReference?.removeEventListener(listener)
        super.onStop()
    }


}