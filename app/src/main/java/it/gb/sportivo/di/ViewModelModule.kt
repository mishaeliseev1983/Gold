package it.gb.sportivo.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.gb.sportivo.MainViewModel
import it.gb.sportivo.presentation.dummy_activity.ResultViewModel


@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ResultViewModel::class)
    @Binds
    fun bindResultViewModel(impl: ResultViewModel): ViewModel

}