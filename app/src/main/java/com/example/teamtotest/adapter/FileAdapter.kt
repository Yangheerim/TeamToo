package com.example.teamtotest.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.solver.widgets.Helper
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.activity.FileActivity
import com.example.teamtotest.dto.FileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File
import java.io.IOException


class FileAdapter(private var get_fileInfoList: ArrayList<HashMap<String, FileDTO>>, private val activity: FileActivity, private var PID: String? )//MyAdapter의 constructor
    : RecyclerView.Adapter<FileAdapter.MyViewHolder>() {

    private var fileInfoList: List<HashMap<String, FileDTO>> = get_fileInfoList
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? = null
    private var firebaseStorage: FirebaseStorage ? =null

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { // create a new view
        get_fileInfoList?.let{
            fileInfoList = get_fileInfoList.reversed()
        }

        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_file,
                parent,
                false
            ) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        var key = ""
        fileInfoList[position].forEach { k, v ->
            holder.itemView.file_title.text = fileInfoList[position][k]?.fileName
            holder.itemView.file_date.text = fileInfoList[position][k]?.date
            holder.itemView.file_userName.text = fileInfoList[position][k]?.userName
            key = k
        }
        //삭제하기
        holder.itemView.setOnLongClickListener {
            
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("삭제하시겠습니까?")
            builder.setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->  })
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    val myUID = firebaseAuth.currentUser!!.uid
                    var userID: String? = null
                    databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("file").child(key)
                    databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener
                    {
                        override fun onCancelled(p0: DatabaseError)
                        {
                            Log.d("FileAdapter", "loadPost:onCancelled")
                        }

                        override fun onDataChange(snapshot: DataSnapshot)
                        {
                            userID = snapshot.child("uid").value.toString()

                            if(myUID == userID)
                            {    //올린 사람이랑 삭제하려는 사람이랑 같은 경우
                                databaseReference!!.removeValue()
                                activity.setListener_FileInfoFromDB()
                                Log.e("delete","삭제완료!")
                                notifyDataSetChanged()
                            }
                            else
                            {   //올린 사람이 아닌 경우
                                Toast.makeText(activity,"삭제 하실 수 없습니다!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    }) //positive btn
            builder.show()
            return@setOnLongClickListener true // LongClick + Onclick
        } //LongClick->삭제하기

        holder.itemView.setOnClickListener {

            val builder = AlertDialog.Builder(activity)
            builder.setMessage("다운로드 하시겠습니까?")
            builder.setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->  }) //아무액션없다
            builder.setPositiveButton("네",
                DialogInterface.OnClickListener { dialogInterface, which ->

                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.getReferenceFromUrl("gs://teamtogether-bdfc9.appspot.com")
                    var state = Environment.getExternalStorageState()
//                    var file = File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS), "YourAppDirectory")
//                    file.mkdirs()

                    val savePath = Environment.getExternalStorageDirectory().absolutePath + "/download"
//                    val dir = File(savePath)
//                    if(!dir.exists()){
//                        dir.mkdir()
//                    }
                    /* Checks if external storage is available for read and write */
                    fun isExternalStorageWritable(): Boolean {
                        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                    }

                    /* Checks if external storage is available to at least read */
                    fun isExternalStorageReadable(): Boolean {
                        return Environment.getExternalStorageState() in
                                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
                    }

//                    val dir = File(Environment.DIRECTORY_DOWNLOADS)
                    val localFile:File = File.createTempFile("File",null)

                    if(isExternalStorageWritable()){
                        storageRef.getFile(localFile).addOnSuccessListener {
                            Log.e("File","success")
                            Log.e("LocalFile", localFile.path)
                        }.addOnFailureListener{
                            Log.e("File","fail")
                        }
                    }


//                    try {
//                        //로컬에 저장할 폴더의 위치
//                        val path = File("Folder path")
//
//                        //저장하는 파일의 이름
//                        val file = File(path, "File name")
//                        try {
//                            if (!path.exists()) {
//                                //저장할 폴더가 없으면 생성
//                                path.mkdirs()
//                            }
//                            file.createNewFile()
//
//                            //파일을 다운로드하는 Task 생성, 비동기식으로 진행
//                            val fileDownloadTask: FileDownloadTask = storageRef.getFile(file)
//                            fileDownloadTask.addOnSuccessListener {
//                                //다운로드 성공 후 할 일
//                            }.addOnFailureListener {
//                                //다운로드 실패 후 할 일
//                            }.addOnProgressListener {
////                                var progress : Int = ((100 * it.getBytesTransferred()) / it.getTotalByteCount()) as Int
////                                progress.setProgress(progress)
//
//                            }
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }




                }) //positive btn
            builder.show()
            return@setOnClickListener
        } //Onclick->다운받기




    }//ViewHolder


    override fun getItemCount(): Int {
        return fileInfoList.size // Return the size of your dataset (invoked by the layout manager)
    }
}