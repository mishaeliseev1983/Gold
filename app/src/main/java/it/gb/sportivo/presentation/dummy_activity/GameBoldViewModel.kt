package it.gb.sportivo.presentation.dummy_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.gb.sportivo.ONE_SECOND
import it.gb.sportivo.THREE_SECOND
import it.gb.sportivo.TOTAL_TIME
import it.gb.sportivo.ZIDANE
import it.gb.sportivo.data.BoldMisterInfo
import it.gb.sportivo.data.Info
import it.gb.sportivo.data.MisterListRepositoryImpl
import it.gb.sportivo.data.SelectedItem
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameBoldViewModel : ViewModel() {

    val repositoryImpl = MisterListRepositoryImpl()
    val misterList = repositoryImpl.getTigersList()

    private val _updateMisterQuestion: MutableLiveData<String> =
        MutableLiveData<String>()
    val updateMisterQuestion: LiveData<String> = _updateMisterQuestion

    private val _updateWrongAnswer: MutableLiveData<String> =
        MutableLiveData<String>("0")
    val updateWrongAnswer: LiveData<String> = _updateWrongAnswer

    private val _updateRightAnswer: MutableLiveData<String> =
        MutableLiveData<String>("0")
    val updateRightAnswer: LiveData<String> = _updateRightAnswer

    private val _updateLeftAnswers: MutableLiveData<String> =
        MutableLiveData<String>("10")
    val updateLeftAnswers: LiveData<String> = _updateLeftAnswers

    private val _finishedGameLD: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>(false)
    val finishedGameLD: LiveData<Boolean> = _finishedGameLD

    private val _secondsLeftLD: MutableLiveData<Int> =
        MutableLiveData<Int>()
    val secondsLeftLD: LiveData<Int> = _secondsLeftLD



    var countMisters = 10
    var secondsLeft = TOTAL_TIME
    var questionsName: MutableSet<String> = repositoryImpl.getQuestionsName()
    var questionName: String = ZIDANE

//    var jobUpdate1: Job? = null
//    var jobUpdate2: Job? = null

    fun updateMistersPeriodically() {
        repositoryImpl.fillBoldListFirstTime()
        questionName= repositoryImpl.getQuestionName()
        _updateMisterQuestion.value = questionName
        viewModelScope.launch(Default) {
            while (secondsLeft >= 0 && countMisters>0) {
                delay(THREE_SECOND)
                repositoryImpl.removeAndFillBoldList()
            }
        }
    }

    fun editTigerInfo(newItem: BoldMisterInfo) {
        viewModelScope.launch(Default) {

            delay(20)
            if(newItem.info.name == questionName) {
                newItem.isSelected = SelectedItem.RightSelected
                repositoryImpl.addRightMisters(questionName)
                _updateRightAnswer.postValue( repositoryImpl.rightMisters.size.toString())
            }
            else
            {
                newItem.isSelected = SelectedItem.WrongSelected
                repositoryImpl.addWrongMisters(questionName)
                _updateWrongAnswer.postValue(repositoryImpl.wrongMisters.size.toString())
            }
            repositoryImpl.editTigerInfo(newItem)
            delay(20)

            questionName= repositoryImpl.getQuestionName()

            countMisters--
            _updateLeftAnswers.postValue(countMisters.toString())
            if(questionsName.isEmpty()) countMisters=0
            _updateMisterQuestion.postValue(questionName)

        }
    }


    fun fetchClock() {
        viewModelScope.launch(Default) {
            while (secondsLeft >= 0 && countMisters>0) {
                //communications.showDataClock(secondsLeft)
                _secondsLeftLD.postValue(secondsLeft)
                delay(ONE_SECOND)
                secondsLeft -= 1
            }
            _finishedGameLD.postValue(true)
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//        jobUpdate1  ?.cancel()
//        jobUpdate2?.cancel()
//    }


}