package com.example.multitoolsdocumentscanner.utils

import android.annotation.SuppressLint
import android.text.format.DateUtils
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StringUtils {

    companion object{

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(): String {

            return try {

                SimpleDateFormat("yyyy-MM-dd").format(Date())

            } catch (e: Exception) {

                ""
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun timestampToDate(timestamp: Long): String {

            return try {

                val dateString = SimpleDateFormat("yyyy-MM-dd").format(
                    Date(timestamp)
                );

                dateString

            } catch (e: Exception) {

                ""
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun isDateToday(dateString: String): Boolean {

            return try {

                val format = SimpleDateFormat("yyyy-MM-dd")
                val date = format.parse(dateString)
                DateUtils.isToday(date!!.time)

            } catch (e: ParseException) {
                false
            }
        }
    }
}