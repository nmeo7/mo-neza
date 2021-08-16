package com.rmsoft.moneza.util

import android.app.Activity
import android.util.Log
import androidx.collection.ArrayMap
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    fun findPeople () : Map<String, Int>
    {
        val mRealm = Realm.getInstance(config)
        val ret2 = ArrayMap<String, Int>()

        for (x in mRealm.where(Message::class.java).findAll()) {
            if ( !ret2.contains(x.subject) )
                ret2[x.subject] = 0
            ret2[x.subject] = ret2[x.subject]!! + 1
        }

        val ret  = ArrayMap<String, Int>()

        for (x in ret2.keys)
        {
            if (ret2[x] != 1)
                ret[x] = ret2[x]
        }

        return ret.toList().sortedByDescending { (k, v) -> v }.toMap()
    }

    fun aggregates (type: String = "*", field: String = "amount", lastDays: Long = 0) : String
    {
        var t : Long = 0
        if (lastDays != 0L)
        {
            val date = Date(System.currentTimeMillis() - lastDays * 3600 * 1000 * 24)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val day = format.format(date)

            t = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(day)?.time!!
        }

        val mRealm = Realm.getInstance(config)
        val ret = mRealm.where(Message::class.java).greaterThan("time", t).like("type", type).sum(field)
        return Message().parseAmount(ret)
    }

    fun balance (lastDays: Long = 0) : String
    {
        var t : Long = System.currentTimeMillis()
        if (lastDays != 0L)
        {
            val date = Date(System.currentTimeMillis() - lastDays * 3600 * 1000 * 24)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val day = format.format(date)
            t = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(day)?.time!!
            Log.i("TIME", t.toString())
        }

        val mRealm = Realm.getInstance(config)

        return try {
            val ret = mRealm.where(Message::class.java).lessThan("time", t).findAllSorted("time", Sort.DESCENDING).first()
            Message().parseAmount(ret.balance)
        }
        catch (e: Exception)
        {
            "0"
        }
    }

    fun asDay (day: Long) : String
    {
        val date = Date(day)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return format.format(date)
    }

    fun days() : HashMap<String, MutableList<Pair<String, Float>>>
    {
        val mRealm = Realm.getInstance(config)
        var firstDay = try {
            mRealm.where(Message::class.java).greaterThan("amount", 0).findAllSorted("time", Sort.ASCENDING).first().time
        }
        catch (e:Exception)
        {
            System.currentTimeMillis()
        }

        Log.i("DATA_PERSISTENCE1", firstDay.toString())

        val sp = HashMap<String, Long>()
        val sv = HashMap<String, Long>()

        val date = Date(firstDay)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val day = format.format(date)
        firstDay = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(day)?.time!!


        Log.i("DATA_PERSISTENCE1", firstDay.toString())
        Log.i("DATA_PERSISTENCE1", day)
        Log.i("DATA_PERSISTENCE1", System.currentTimeMillis().toString())

        for (x in firstDay..System.currentTimeMillis() step 3600 * 1000 * 24)
        {
            val d = asDay(x)
            sp[ d ] = 0
            sv[ d ] = 0
        }

        val ret = mRealm.where(Message::class.java).findAll()

        for (x in ret)
        {
            val d = asDay(x.time)

            if (!sp.containsKey(d)) {
                sp[d] = 0
                sv[d] = 0
            }

            if (x.type == "DEPOSIT" || x.type == "RECEIVING")
                sv[d] = sp[d]!! + x.amount
            if (x.type == "PAYMENT" || x.type == "TRANSFER" || x.type == "WITHDRAW")
                sp[d] = sp[d]!! + x.amount
        }

        val r = HashMap<String, MutableList<Pair<String, Float>>>()

        r["sv"] = mutableListOf() // sv.values.toTypedArray().sortedArray()
        r["sp"] = mutableListOf() // sp.values.toTypedArray().sortedArray()

        for (x in sv)
        {
            val m = Pair(x.key, x.value.toFloat())
            r["sv"]?.add(m)
        }
        for (x in sp)
        {
            val m = Pair(x.key, x.value.toFloat())
            r["sp"]?.add(m)
        }

        return r
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