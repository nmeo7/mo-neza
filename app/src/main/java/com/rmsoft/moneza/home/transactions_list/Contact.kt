package com.rmsoft.moneza.home.transactions_list

import java.util.*

class Contact(val name: String, val isOnline: Boolean, val type: Int) {

    companion object {
        private var lastContactId = 0
        fun createContactsList(numContacts: Int): ArrayList<Contact> {
            val contacts = ArrayList<Contact>()
            for (i in 1..numContacts) {
                contacts.add(Contact("Person " + ++lastContactId, i <= numContacts / 2, lastContactId % 5 - 1))
            }
            return contacts
        }
    }
}