package com.funtik.fts_homework1.contacts

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.funtik.fts_homework1.R
import com.funtik.fts_homework1.App


class ContactActivity : AppCompatActivity() {

    companion object {
        const val TAKE_CONTACTS_ACTION = "com.funtik.fintech.contacts"
        const val EXTRA_CONFIRM_DATA = "extraConfirmDataFromSecondAct"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        setTitle(R.string.second_activity_title)

        val intentFilter = IntentFilter(TAKE_CONTACTS_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(contactReceiver, intentFilter)

        // каждый раз при пересоздании Activity происходит запуск сервиса
        val contactIntent = Intent(this, ContactIntentService::class.java)
        startService(contactIntent)
    }


    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactReceiver)
    }

    // при получении данных Activity закрывается и данные от Сервиса передаются далее в первую Activity
    private val contactReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = intent.getSerializableExtra(ContactIntentService.RESULT_MSG)
            Log.e(App.TAG, "Received message \"$result\"")
            val resultIntent = Intent().putExtra(EXTRA_CONFIRM_DATA, result)
            setResult(Activity.RESULT_OK, resultIntent)
            // Если данные получены, то можно закрывать сервис
            // val closeIntent = Intent(this@ContactActivity,ContactIntentService::class.java)
            //  stopService(closeIntent)
            // но нужно останавливать поток в методе onDestroy
            finish()
        }
    }

}