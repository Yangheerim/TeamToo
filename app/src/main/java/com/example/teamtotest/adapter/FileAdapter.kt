package com.example.teamtotest.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.FileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FileAdapter(private var get_fileInfoList: ArrayList<HashMap<String, Objects>>, private var context: Context, private var PID: String? )//MyAdapter의 constructor
    : RecyclerView.Adapter<FileAdapter.MyViewHolder>() {

    private var fileInfoList: List<HashMap<String, Objects>> = get_fileInfoList
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.file_title.text = fileInfoList[position]["DTO"] as FileDTO
        holder.itemView.file_date.text = fileInfoList[position]["date"]
        holder.itemView.file_userName.text = fileInfoList[position]["userName"]
        holder.itemView.setOnLongClickListener {
            Toast.makeText(context, "롱클릭", Toast.LENGTH_SHORT).show()

            val builder = AlertDialog.Builder(context)
            builder.setMessage("삭제하시겠습니까?")
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    val myUID = firebaseAuth!!.currentUser!!.uid
                    databaseReference = firebaseDatabase!!.getReference("ProjectList").child(PID.toString()).child("file")







            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return fileInfoList.size // Return the size of your dataset (invoked by the layout manager)
    }
}