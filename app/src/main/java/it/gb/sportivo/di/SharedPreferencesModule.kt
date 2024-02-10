package it.gb.sportivo.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import it.gb.sportivo.data.SharedPreferencesData


@Module
class SharedPreferencesModule {

    @Provides
    fun getSharedData(app: Context): SharedPreferences {
        return app.getSharedPreferences("PREFERENCE_NAM111111", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideSharedData(sharedPreferences: SharedPreferences): SharedPreferencesData {
        return SharedPreferencesData(sharedPreferences)
    }

}