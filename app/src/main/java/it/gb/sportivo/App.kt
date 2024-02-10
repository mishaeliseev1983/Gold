package it.gb.sportivo

import android.app.Application
import it.gb.sportivo.di.DaggerApplicationComponent

class App : Application() {
    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}