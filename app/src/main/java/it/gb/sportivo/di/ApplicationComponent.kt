package it.gb.sportivo.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import it.gb.sportivo.MainActivity
import it.gb.sportivo.presentation.dummy_activity.ResultFragment


@ApplicationScope
@Component(
    modules = [
        ViewModelModule::class,
        AppModule::class,
        SharedPreferencesModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(frg: ResultFragment)

    @Component.Factory
    interface AppCompFactory {
        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}