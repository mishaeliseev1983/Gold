package it.gb.sportivo.presentation.dummy_activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.gb.sportivo.R


class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        val startFragment = WelcomeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.container_dummy, startFragment).commit()
    }
}