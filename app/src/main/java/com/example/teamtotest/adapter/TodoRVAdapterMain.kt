package com.example.teamtotest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.ProjectDTO
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_todo_dashboard.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class TodoRVAdapterMain(
    private val context: Context,
    private var todoDTO: ArrayList<TodoDTO>
) :
    RecyclerView.Adapter<ViewHolderHelper>() {
    private val today = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_todo_dashboard, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return todoDTO.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        Log.d("Todo Position -->", position.toString())
        Log.d("Todo data -->", todoDTO[position].toString())

        val deadCal = Calendar.getInstance()
        deadCal.time = Date(todoDTO[position].deadLine)

        holder.itemView.d_todo_project_name.text = todoDTO[position].projectdata!!.projectName
        holder.itemView.d_todo_tv_name.text = todoDTO[position].name

        val diff = deadCal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)
        when {
            diff == 0 -> {
                holder.itemView.d_todo_day.text = "D-day"
            }
            diff > 0 -> {
                holder.itemView.d_todo_day.text = "D-${diff}"
            }
        }



    }


}
