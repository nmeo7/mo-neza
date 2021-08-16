package com.rmsoft.moneza.actions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rmsoft.moneza.R
import com.rmsoft.moneza.util.DataPersistence


class ActionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions)

        val people = DataPersistence(this). findPeople ()

        Log.i("PEOPLE", people.toString())
    }


    fun res ()
    {
        val returnIntent = Intent()
        returnIntent.putExtra("result", "Number")
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}