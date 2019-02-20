package com.funtik.fts_homework1

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.funtik.fts_homework1.contacts.ContactActivity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity(), View.OnClickListener {


    // Request code for READ_CONTACTS. любой номер > 0.
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private val CONTACT_REQUEST_CODE = 1


    // view
    private lateinit var btnToSecondActivity: Button
    private lateinit var lstNames: ListView

    // loaded contacts, on start it's empty
    private var contacts: ArrayList<String> = ArrayList()
    private val CONTACT_ON_BUNDLE = "contact"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_activity_title)

        btnToSecondActivity = findViewById(R.id.btnToSecondActivity)
        btnToSecondActivity.setOnClickListener(this)

        createList()

    }


    override fun onClick(p0: View?) {
        loadContactsFromSecondActivity(this)
    }


    /**
     * load the contacts from the second Activity and check permissions.
     */
    private fun loadContactsFromSecondActivity(activity: Activity) {
        // Проверяем версию SDK  и есть ли разрешение.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
            //После данной точки, ждём обратный вызов в onRequestPermissionsResult(int, String[], int[]) переопрёделённый метод
        } else {
            val intent = Intent(activity, ContactActivity::class.java)
            startActivityForResult(intent, CONTACT_REQUEST_CODE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                loadContactsFromSecondActivity(this)
            } else {
                Toast.makeText(this, getString(R.string.permissionNotGranted), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CONTACT_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        contacts = data?.getSerializableExtra(ContactActivity.EXTRA_CONFIRM_DATA) as ArrayList<String>
                        updList(contacts)
                        Toast.makeText(this, getString(R.string.contactsAreLoaded), Toast.LENGTH_SHORT).show()

                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this, R.string.data_IsNotReceived, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun createList(){
        viewManager = LinearLayoutManager(this)

        viewAdapter = MyAdapter(contacts)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    private fun updList(items: ArrayList<String>){
        recyclerView.adapter = MyAdapter(contacts)
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(CONTACT_ON_BUNDLE,contacts)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        contacts = savedInstanceState?.getSerializable(CONTACT_ON_BUNDLE) as ArrayList<String>
        updList(contacts)
    }




}