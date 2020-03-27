package com.example.teamtotest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_file.view.*


class FileAdapter(private var get_fileInfoList : ArrayList<HashMap<String,String>>)//MyAdapter의 constructor
    : RecyclerView.Adapter<FileAdapter.MyViewHolder>() {


    private var fileInfoList: ArrayList<HashMap<String, String>> = get_fileInfoList
    private var firebaseAuth = FirebaseAuth.getInstance()

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { // create a new view


        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_file,
                parent,
                false
            ) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.file_title.text = fileInfoList[position]["fileName"]
        holder.itemView.file_date.text = fileInfoList[position]["date"]
        holder.itemView.file_userName.text = fileInfoList[position]["userName"]
    }


    override fun getItemCount(): Int {
        return fileInfoList.size // Return the size of your dataset (invoked by the layout manager)
    }
}