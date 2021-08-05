package com.rmsoft.moneza.util

import android.app.Activity
import android.util.Log
import com.rmsoft.moneza.home.transactions_list.Contact
import io.realm.Realm
import io.realm.RealmConfiguration

class DataPersistence(var context: Activity) {

    private lateinit var mRealm : Realm
    var initialized = false

    private fun create() {
        if (initialized)
            return

        Realm.init(context)
        val config = RealmConfiguration.Builder()
            .name("test.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()

        mRealm = Realm.getInstance(config)

        initialized = true
    }

    fun save (m: Message) {
        create()

        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(m)
        mRealm.commitTransaction()

        Log.i("MOMO_READ", m.toString())
    }

    fun find () : ArrayList<Contact>
    {
        create()

        Log.d("MOMO_READ", "Trying to read")

        val ret = ArrayList<Contact>()

        for (x in mRealm.where(Message::class.java).like("subject", "N*").findAll()) {
            Log.d("MOMO_READ", x.toString())
            ret.add( Contact(x.subject!!, false, 1) )

        }

        return ret
    }
}