package it.gb.sportivo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gb.sportivo.data.SharedPreferencesData
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferencesData
) : ViewModel() {


    private var _liveData = MutableLiveData<String>()
    val liveData: LiveData<String> = _liveData


    private var _liveDataWithVPN = MutableLiveData<String>()
    val liveDataWithVPN: LiveData<String> = _liveDataWithVPN

    fun writeUrl(newUrl: String){
        sharedPreferences.changeUrl(newUrl)
    }

    fun readLocalUrl(){
        viewModelScope.launch {
            _liveData.postValue( sharedPreferences.getUrl() )
        }
    }

    fun readLocalUrlWithVPN(){
        viewModelScope.launch {
            _liveDataWithVPN.postValue( sharedPreferences.getUrl() )
        }
    }
}