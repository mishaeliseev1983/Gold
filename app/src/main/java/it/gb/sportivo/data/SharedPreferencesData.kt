package it.gb.sportivo.data

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

const val  url = "URL"
const val  record = "RECORD"
class SharedPreferencesData @Inject constructor(private val data: SharedPreferences) {

    fun getUrl(): String {
        return data.getString(url, "") ?:""
    }

    fun changeUrl(newUrl: String) {
        data.edit {
            putString(url, newUrl)
        }
    }

    fun getRecord(): Int{
        return data.getInt(record, 0) ?: 0
    }

    fun setRecord(newRecord: Int){
        if(newRecord > getRecord()) {
            data.edit {
                putInt(record, newRecord)
            }
        }
    }
}