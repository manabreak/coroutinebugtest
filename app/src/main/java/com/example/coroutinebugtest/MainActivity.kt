package com.example.coroutinebugtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)

        val vm = ViewModelProviders.of(this)
                .get(MainViewModel::class.java)

        vm.liveData.observe(this, Observer {
            findViewById<TextView>(R.id.mainText).text = "Got result: $it"
        })

        vm.getFoo()
    }
}

@ExperimentalCoroutinesApi
class MainViewModel : ViewModel() {
    val liveData = MutableLiveData<String>()

    fun getFoo() {
        viewModelScope.launch {
            liveData.postValue(Operation().get())
        }
    }
}

open class Operation {
    private val handler = Handler(Looper.getMainLooper())
    fun execute(listener: (String) -> Unit) {
        handler.post { listener("Success") }
    }
}

@ExperimentalCoroutinesApi
suspend fun <T> Operation.get(): T = suspendCancellableCoroutine { c ->
    execute {
        c.resume(it as T, {})
    }
}