package com.example.teamtotest.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.activity.FileActivity
import com.example.teamtotest.dto.FileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File
import java.security.KeyStore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FileAdapter(private var get_fileInfoList: ArrayList<HashMap<String, FileDTO>>, private val activity: FileActivity, private var PID: String? )//MyAdapter의 constructor
    : RecyclerView.Adapter<FileAdapter.MyViewHolder>() {

    private var fileInfoList: List<HashMap<String, FileDTO>> = get_fileInfoList
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? = null

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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var key = ""
        fileInfoList[position].forEach { k, v ->
            holder.itemView.file_title.text = fileInfoList[position][k]?.fileName
            holder.itemView.file_date.text = fileInfoList[position][k]?.date
            holder.itemView.file_userName.text = fileInfoList[position][k]?.userName
            key = k
        }

        holder.itemView.setOnLongClickListener {
            
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("삭제하시겠습니까?")
            builder.setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->  })
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    val myUID = firebaseAuth.currentUser!!.uid
                    var userID: String? = null
                    databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("file").child(key)
                    databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("FileAdapter", "loadPost:onCancelled")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            userID = snapshot.child("uid").value.toString()

                            if(myUID == userID){    //올린 사람이랑 삭제하려는 사람이랑 같은 경우
                                databaseReference!!.removeValue()
                                activity.setListener_FileInfoFromDB()
                                Log.e("delete","삭제완료!")
                                notifyDataSetChanged()
                            }
                            else{   //올린 사람이 아닌 경우
                                Toast.makeText(activity,"삭제 하실 수 없습니다!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    })
            builder.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return fileInfoList.size // Return the size of your dataset (invoked by the layout manager)
    }
}