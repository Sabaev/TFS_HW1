package com.funtik.fts_homework1.contacts

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.database.CrossProcessCursor
import android.database.Cursor
import android.os.SystemClock
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.funtik.fts_homework1.App
import android.provider.ContactsContract
import java.io.Serializable


class ContactIntentService: IntentService("ContactIntentService") {
    companion object {
        const val RESULT_MSG = "com.funtik.fintech.contacts"
    }


    override fun onHandleIntent(intent: Intent?) {

        Log.e(App.TAG, "Start IntentService")
        val contracts = getContacts()
        Log.e(App.TAG, "Finish IntentService")

        val broadcastIntent = Intent(ContactActivity.TAKE_CONTACTS_ACTION)
        // есть ограничение на 50 кб при передаче данных через интент, но в данный момент мы передаём только имя,
        // что уменьшает вероятность ошибки, но не исключает
        broadcastIntent.putExtra(RESULT_MSG, contracts )
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private fun getContacts(): ArrayList<String> {
        val contacts = ArrayList<String>()
        // Получем  ContentResolver
        val cr = contentResolver
        // Получаем курсор на все контакты
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        // Перемещаем курсор на первый элемент. И проверяем пустой он или нет.
        if (cursor!!.moveToFirst()) {
            // Итерируемся по курсору
            do {
                // Получаем имя (предусмотрен отдельный класс для контакта на будущее)) )
//                val phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                contacts.add(name)
            } while (cursor.moveToNext())
        }
        // Закрываем курсор
        cursor.close()

        return contacts
    }


    // этот метод вызывается при stopService, но onHandlerIntent запускается в своём потоке
    override fun onDestroy() {
        Log.e(App.TAG,"onDestroy method started")
        super.onDestroy()

    }
}