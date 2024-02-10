package it.gb.sportivo.data


data class BoldMisterInfo(

    var id: Int,
    val info: Info,
    var isSelected: SelectedItem = SelectedItem.NoSelected,

    ) {
    companion object {

        const val UNDEFINED_ID = -1
    }
}