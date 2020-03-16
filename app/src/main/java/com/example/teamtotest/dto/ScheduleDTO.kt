package com.example.teamtotest.dto

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlin.random.Random

data class ScheduleDTO(
    var name: String? = "",
    var startTime: Long = 0,
    var endTime: Long = 0,
    var place: String? = "",
    var alarm: Int = 0,
    var note: String? = "",
    @ColorInt val color: Int = Color.rgb(
        Random.nextInt(255),
        Random.nextInt(255),
        Random.nextInt(255)
    )
) : Parcelable, Comparable<ScheduleDTO> {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(startTime)
        parcel.writeLong(endTime)
        parcel.writeString(place)
        parcel.writeInt(alarm)
        parcel.writeString(note)
        parcel.writeInt(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleDTO> {
        override fun createFromParcel(parcel: Parcel): ScheduleDTO {
            return ScheduleDTO(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleDTO?> {
            return arrayOfNulls(size)
        }
    }

    override fun compareTo(other: ScheduleDTO): Int {
        return if (this.startTime > other.startTime) 1
        else -1
    }
}