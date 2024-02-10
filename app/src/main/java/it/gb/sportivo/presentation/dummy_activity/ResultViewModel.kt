package it.gb.sportivo.presentation.dummy_activity

import androidx.lifecycle.ViewModel
import it.gb.sportivo.data.SharedPreferencesData
import javax.inject.Inject

class ResultViewModel@Inject constructor(
    private val sharedPreferences: SharedPreferencesData
) : ViewModel() {


    fun getRecord() = sharedPreferences.getRecord()
    fun saveRecord(newRecord: Int){
        sharedPreferences.setRecord(newRecord)
    }

}