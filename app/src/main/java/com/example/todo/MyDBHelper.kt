package com.example.todo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.AccessControlContext


class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "AKTIVITETEDB",  null,  1) {

    override fun onCreate(db: SQLiteDatabase?) {
//        db?.execSQL( "CREATE TABLE USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT , UNAME TEXT , PWD TEXT)")
//        db?.execSQL( "INSERT INTO USERS(UNAME, PWD) VALUES('ene123@gmail.com','libri')")
//        db?.execSQL( "INSERT INTO USERS(UNAME, PWD) VALUES('eno21@gmail.com','leopardi')")
        db?.execSQL( "CREATE TABLE AKTIVITETES(ID INTEGER PRIMARY KEY AUTOINCREMENT , Rregulli_rradhes TEXT, Data TEXT , Ora INTEGER , Minutat INTEGER)");
//        db?.execSQL( "INSERT INTO AKTIVITETES(Rregulli_rradhes, Ora, Minutat) VALUES('Rregulli_rradhes',0, 0 )");
        ;
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}