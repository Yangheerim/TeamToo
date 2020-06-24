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
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File
import java.io.IOException


class FileAdapter(
    private var get_fileInfoList: ArrayList<HashMap<String, FileDTO>>,
    private val activity: FileActivity,
    private var PID: String?,
    private var file_loadingCircle: View
)//MyAdapter의 constructor
    : RecyclerView.Adapter<FileAdapter.MyViewHolder>() {

    private var fileInfoList: List<HashMap<String, FileDTO>> = get_fileInfoList
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? = null
    private var firebaseStorage: FirebaseStorage? = null

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // create a new view
        get_fileInfoList?.let {
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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
            builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which -> })
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    file_loadingCircle.visibility = View.VISIBLE
                    val myUID = firebaseAuth.currentUser!!.uid
                    var userID: String? = null
                    databaseReference =
                        firebaseDatabase!!.getReference("ProjectList").child(PID.toString())
                            .child("file").child(key)
                    databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("FileAdapter", "loadPost:onCancelled")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            userID = snapshot.child("uid").value.toString()


                            if (myUID == userID) {    //올린 사람이랑 삭제하려는 사람이랑 같은 경우
                                databaseReference!!.removeValue()
                                activity.setListener_FileInfoFromDB()
                                file_loadingCircle.visibility = View.INVISIBLE
                                Toast.makeText(activity, "삭제 완료!", Toast.LENGTH_SHORT).show()

                                notifyDataSetChanged()
                            } else {   //올린 사람이 아닌 경우
                                Toast.makeText(activity, "삭제 하실 수 없습니다!", Toast.LENGTH_SHORT).show()
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
            builder.setNegativeButton(
                "아니오",
                DialogInterface.OnClickListener { dialog, which -> }) //아무액션없다
            builder.setPositiveButton("네",
                DialogInterface.OnClickListener { dialogInterface, which ->
                    val fileName = holder.itemView.file_title.text.toString()
                    val storage = FirebaseStorage.getInstance()
                    val storageRef =
                        storage.getReferenceFromUrl("gs://teamtogether-bdfc9.appspot.com")
                    val islandRef = storageRef.child(fileName)

                    val rootPath = File(Environment.getExternalStorageDirectory(), "TeamTo")
                    if (!rootPath.exists()) {
                        rootPath.mkdirs()
                    }
                    val localFile = File(rootPath, fileName)

                    file_loadingCircle.visibility = View.VISIBLE


                    islandRef.getFile(localFile).addOnSuccessListener {
                        file_loadingCircle.visibility = View.INVISIBLE
                        Toast.makeText(activity, "다운로드 완료!", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener {
                        // 다운로드 실패 시
                        Log.e("FileDownloadTask", it.toString())
                        Toast.makeText(activity,"다운실패!",Toast.LENGTH_SHORT).show()

                    }.addOnProgressListener {
                        file_loadingCircle.visibility = View.VISIBLE

                    }
                }) //positive btn
            builder.show()
            return@setOnClickListener
        } //Onclick->다운받기
    }//ViewHolder

    override fun getItemCount(): Int {
        return fileInfoList.size // Return the size of your dataset (invoked by the layout manager)
    }

}