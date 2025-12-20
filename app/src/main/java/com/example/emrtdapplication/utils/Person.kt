package com.example.emrtdapplication.utils

/**
 * Represents a person to notify in emergencies according to DG16
 * @property name The name of the person
 * @property address The address of the person
 * @property telephone The telephone number of the person including country code
 * @property date Date when the information was recorded
 */
class Person(name: String, address: String, telephone: String, date: String) {
    var name = name
        private set
    var address = address
        private set
    var telephone = telephone
        private set
    var date = date
        private set
}