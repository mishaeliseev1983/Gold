package it.gb.sportivo.di

import android.content.Context
import dagger.Module
import dagger.Provides
import it.gb.sportivo.App


@Module
object AppModule {


    @ApplicationScope
    @Provides
    fun provideApplication(app: Context): App {
        return app as App
    }



}