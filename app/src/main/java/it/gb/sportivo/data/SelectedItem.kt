package it.gb.sportivo.data

sealed class SelectedItem {
    data object NoSelected: SelectedItem()
    data object WasWrongSelected: SelectedItem()
    data object WrongSelected: SelectedItem()
    data object RightSelected: SelectedItem()
}