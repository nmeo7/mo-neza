package com.rmsoft.moneza

import android.content.ClipData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rmsoft.moneza.util.Message

class StateMachine : ViewModel() {
    private val mutableSelectedMessage = MutableLiveData<Message>()
    val selectedMessage: LiveData<Message> get() = mutableSelectedMessage

    fun selectMessage(message: Message) {
        mutableSelectedMessage.value = message
    }
}
