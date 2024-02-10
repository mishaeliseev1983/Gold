package it.gb.sportivo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.gb.sportivo.ARNE_SLOT
import it.gb.sportivo.BOB_BREDLI
import it.gb.sportivo.CHERCHESOV
import it.gb.sportivo.COUNT
import it.gb.sportivo.NIKOLIC
import it.gb.sportivo.NUNU
import it.gb.sportivo.PEP
import it.gb.sportivo.R
import it.gb.sportivo.ROBERTO_DI_MATTEO
import it.gb.sportivo.ROBERTO_MARTINEZ
import it.gb.sportivo.SHEAN
import it.gb.sportivo.SPALETTI
import it.gb.sportivo.STEFAN_PIOLI
import it.gb.sportivo.TEN_HAAG
import it.gb.sportivo.VINCENZO_ITALIANO
import it.gb.sportivo.ZIDANE


class MisterListRepositoryImpl {

    private val boldRepoListLD = MutableLiveData<List<BoldMisterInfo>>()
    private val boldRepoSet = sortedSetOf<BoldMisterInfo>({ o1, o2 -> o1.id.compareTo(o2.id) })
    private var autoIncrementId = 0
    var rightMisters = mutableSetOf<String>()
    var wrongMisters = mutableSetOf<String>()

    var infos = mutableListOf<Info>()
    val boldAll = mutableListOf<String>()
    fun getTigersList(): LiveData<List<BoldMisterInfo>> {
        return boldRepoListLD
    }

    fun editTigerInfo(bold: BoldMisterInfo) {
        val oldElement = getBoldMisterInfo(bold.id)
        boldRepoSet.remove(oldElement)
        addBoldInfo(bold)
    }

    private fun addBoldInfo(bold: BoldMisterInfo) {

        if (bold.id == BoldMisterInfo.UNDEFINED_ID) {
            bold.id = autoIncrementId++
        }
        boldRepoSet.add(bold)
        updateList()
    }

    private fun addBoldInfoNoUpdate(bold: BoldMisterInfo) {

        if (bold.id == BoldMisterInfo.UNDEFINED_ID) {
            bold.id = autoIncrementId++
        }
        if(bold.isSelected!=SelectedItem.NoSelected){
            println("12312312312312")
        }
        boldRepoSet.add(bold)
    }

    private fun updateList() {
        boldRepoListLD.postValue( boldRepoSet.toList() )
    }


    fun getBoldMisterInfo(id: Int): BoldMisterInfo {
        return boldRepoSet.find {
            it.id == id
        } ?: throw RuntimeException("Element with id $id not found")
    }

    fun fillBoldListFirstTime(){
        fillInfos()
            for (i in 0 until COUNT) {
                val bold = BoldMisterInfo(i, infos[i])
                println("qwe123 $i = ${infos[i]}   boldNew = ${bold.isSelected}")
                addBoldInfo(bold)
        }
    }


    fun removeAndFillBoldList(){
        fillInfos()
        boldRepoSet.clear()
        for (i in 0 until COUNT) {
            var state: SelectedItem = SelectedItem.NoSelected
            if(rightMisters.contains(infos[i].name)) state= SelectedItem.RightSelected
            else
                if(wrongMisters.contains(infos[i].name)) state= SelectedItem.WasWrongSelected

            val bold = BoldMisterInfo(i, infos[i], isSelected = state)
            addBoldInfoNoUpdate(bold)
        }
        updateList()
    }


    private fun  fillInfos(){
        infos.clear()
        infos = mutableListOf(
            Info(PEP, R.drawable.b1_pep),
            Info(SPALETTI, R.drawable.b10_spaletti),
            Info(BOB_BREDLI, R.drawable.b14_bob_bredli),
            Info(ZIDANE, R.drawable.b5_zidane),
            Info(ARNE_SLOT, R.drawable.b15_arne_slot),
            Info(ROBERTO_MARTINEZ, R.drawable.b12_roberto_martinez),
            Info(ROBERTO_DI_MATTEO, R.drawable.b13_robero_di_matteo),
            Info(NUNU, R.drawable.b2_nunu_eshpiritu),
            Info(NIKOLIC, R.drawable.b3_nikolic),
            Info(TEN_HAAG, R.drawable.b4_ten_haag),
            Info(CHERCHESOV, R.drawable.b6_cherchesov),
            Info(SHEAN, R.drawable.b7_sean_mark_dyche),
            Info(VINCENZO_ITALIANO, R.drawable.b8_vinchenzo_italiano),
            Info(STEFAN_PIOLI, R.drawable.b9_stefan_piolli),

            )
        infos.shuffle()
    }

    fun getQuestionName(): String {
//        val random = (0 until infos.size).random()
//        return infos[random].name

        if(questionsName.isEmpty())
            return ""
        questionsName.shuffled()
        val removeIt= questionsName.first()
        questionsName.remove(removeIt)
        return removeIt
    }

    private val questionsName =  mutableSetOf(

        PEP,
        ZIDANE,
        SPALETTI,
        TEN_HAAG,
        ROBERTO_MARTINEZ,
        NUNU,
        BOB_BREDLI,
        ARNE_SLOT,
        ROBERTO_DI_MATTEO,
        SHEAN,
        STEFAN_PIOLI,
        CHERCHESOV,
        NIKOLIC,
        VINCENZO_ITALIANO,

    )
    fun getQuestionsName(): MutableSet<String> {
       return questionsName
    }

    fun addRightMisters(questionName: String) {
        rightMisters.add(questionName)
    }

    fun addWrongMisters(questionName: String) {
        wrongMisters.add(questionName)
    }


}