package com.example.teamtotest

import java.util.*

@Suppress("DEPRECATION")
class BaseCalendar {

    companion object {
        const val DAYS_OF_WEEK = 7  //가로 7칸
        const val LOW_OF_CALENDAR = 6   //세로 6칸
    }

    private val calendar: Calendar = Calendar.getInstance()

    var prevMonthTailOffset = 0
    var nextMonthHeadOffset = 0
    var currentMonthMaxDate = 0

    var data = arrayListOf<Calendar>()  //데이터를 날짜 형태로 관리하자!!

    //가장 처음 실행되는 함수
    init {
        calendar.time = Date()  //현재 시각
    }

    fun initBaseCalendar(refreshCallback: (Calendar) -> Unit) {
        makeMonthDate(refreshCallback)
    }

    //이전 달로 바꾸기
    fun changeToPrevMonth(refreshCallback: (Calendar) -> Unit) {
        if (calendar.get(Calendar.MONTH) == Calendar.JANUARY) {  //이전 달이 12월인 경우
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
            calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        }
        makeMonthDate(refreshCallback)
    }

    //다음 달로 바꾸기
    fun changeToNextMonth(refreshCallback: (Calendar) -> Unit) {
        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {  //다음 달이 12월인 경우
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1)
            calendar.set(Calendar.MONTH, Calendar.JANUARY)
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
        }
        makeMonthDate(refreshCallback)
    }

    //날짜 만들기
    private fun makeMonthDate(refreshCallback: (Calendar) -> Unit) {
        data.clear()    //data 초기화

        calendar.set(Calendar.DATE, 1)  //이전달 마지막주와 다음달 첫째주를 채우기 위해

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)  //이번달의 마지막 날짜
        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1    //이전달의 마지막주 날짜 개수

        makePrevMonthTail(calendar.clone() as Calendar) //이전달 마지막주 만들기
        makeCurrentMonth(calendar.clone() as Calendar)  //이번달 만들기

        nextMonthHeadOffset =
            LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate)    //다음달의 첫째주 날짜 개수
        makeNextMonthHead(calendar.clone() as Calendar) //다음달 첫째주 만들기

        refreshCallback(calendar)   //함수 호출하기

    }

    //이전달 마지막 주 만들기
    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - prevMonthTailOffset

        for (i in 1..prevMonthTailOffset) data.add((calendar.clone() as Calendar).apply { set(
            Calendar.DATE, ++maxOffsetDate) })
    }

    //이번달 만들기
    private fun makeCurrentMonth(calendar: Calendar) {
        for (i in 1..calendar.getActualMaximum(Calendar.DATE)) data.add((calendar.clone() as Calendar).apply { set(
            Calendar.DATE, i) })
    }

    //다음달 첫째 주 만들기
    private fun makeNextMonthHead(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1)
        var date = 1

        for (i in 1..nextMonthHeadOffset) data.add((calendar.clone() as Calendar).apply { set(
            Calendar.DATE, date++) })
    }
}