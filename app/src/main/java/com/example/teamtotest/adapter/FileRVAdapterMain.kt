package com.example.teamtotest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.FileDTO
import com.example.teamtotest.dto.ProjectDTO
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_file_dashboard.view.*
import kotlinx.android.synthetic.main.item_todo_dashboard.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class FileRVAdapterMain(
    private val context: Context,
    private var fileDTO: ArrayList<FileDTO>
) :
    RecyclerView.Adapter<ViewHolderHelper>() {
    private val today = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_file_dashboard, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return fileDTO.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        holder.itemView.d_file_project_name.text = fileDTO[position].projectdata!!.projectName
        holder.itemView.d_file_name.text = fileDTO[position].fileName
    }


}
