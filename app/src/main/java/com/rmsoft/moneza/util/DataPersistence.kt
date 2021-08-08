package com.rmsoft.moneza.util

import android.app.Activity
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort

class DataPersistence constructor (var context: Activity) {

    private var config : RealmConfiguration

    init {
        Realm.init(context)

        config = RealmConfiguration.Builder()
                .name("test.db")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
    }

    fun save (m: Message) {
        val mRealm = Realm.getInstance(config)

        mRealm.beginTransaction()
        mRealm.copyToRealmOrUpdate(m)
        mRealm.commitTransaction()
    }

    fun find () : ArrayList<Message>
    {
        val mRealm = Realm.getInstance(config)

        Log.d("MOMO_READ", "Trying to read")

        val ret = ArrayList<Message>()

        var day = ""

        for (x in mRealm.where(Message::class.java) .like("subject", "*") .findAllSorted("time", Sort.DESCENDING)) {
            Log.d("MOMO_READ_ALL", x.toString())
            val newDay = x.getDay()

            if (newDay != day)
            {
                val d = Message()
                d.subject = newDay
                d.type = "DAY"
                day = newDay
                ret.add(d)
            }
            ret.add( x )

        }

        return ret
    }

    fun aggregates ()
    {
        val mRealm = Realm.getInstance(config)
        mRealm.where(Message::class.java).sum("amount")
    }

    fun reset ()
    {
        val mRealm = Realm.getInstance(config)

        while (!mRealm.isClosed)
            mRealm.close()

        Realm.deleteRealm(config)

        MessageReadAll(context).resetSyncTime(context)
    }
}