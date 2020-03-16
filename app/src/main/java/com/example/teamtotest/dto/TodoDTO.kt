package com.example.teamtotest.dto

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class TodoDTO(
    var name: String? = "",
    var note: String? = "",
    var deadLine: Long = 0,
    //과제 수행자 추가해야함
    var alarm: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(note)
        parcel.writeLong(deadLine)
        parcel.writeInt(alarm)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TodoDTO> {
        override fun createFromParcel(parcel: Parcel): TodoDTO {
            return TodoDTO(parcel)
        }

        override fun newArray(size: Int): Array<TodoDTO?> {
            return arrayOfNulls(size)
        }
    }

}