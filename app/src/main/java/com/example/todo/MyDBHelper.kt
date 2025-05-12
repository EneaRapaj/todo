package com.example.todo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MyDBHelper(context: Context) : SQLiteOpenHelper(context, "AKTIVITETEDB",  null,  1) {

    override fun onCreate(db: SQLiteDatabase?) {
//        db?.execSQL( "CREATE TABLE USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT , UNAME TEXT , PWD TEXT)")
//        db?.execSQL( "INSERT INTO USERS(UNAME, PWD) VALUES('ene123@gmail.com','libri')")
//        db?.execSQL( "INSERT INTO USERS(UNAME, PWD) VALUES('eno21@gmail.com','leopardi')")
        db?.execSQL( "CREATE TABLE AKTIVITETES(ID INTEGER PRIMARY KEY AUTOINCREMENT , Rregulli_rradhes TEXT, Data TEXT , Ora INTEGER , Minutat INTEGER)");
//        db?.execSQL( "INSERT INTO AKTIVITETES(Rregulli_rradhes, Ora, Minutat) VALUES('Rregulli_rradhes',0, 0 )");
        ;
        db?.execSQL("CREATE TABLE IF NOT EXISTS Clothes (name TEXT PRIMARY KEY)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS Coins (amount INTEGER)")
        db?.execSQL("INSERT INTO Coins (amount) VALUES (0)")
        db?.execSQL("delete from Clothes")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun getCoins(): Int {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT amount FROM Coins", null)
        return if (cursor.moveToFirst()) {
            val coins = cursor.getInt(0)
            cursor.close()
            coins
        } else {
            cursor.close()
         0
        }
    }

    fun updateCoins(newAmount: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("amount", newAmount)
        }
        db.update("Coins", values, null, null)
    }

    fun unlockCloth(clothName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", clothName)
        }
        db.insert("Clothes", null, values)
    }


    fun isClothUnlocked(clothName: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM Clothes WHERE name=?", arrayOf(clothName))
        val unlocked = cursor.moveToFirst()
        cursor.close()
        return unlocked
    }

    fun deleteAllClothes() {
        val db = writableDatabase
        db.execSQL("DELETE FROM Clothes")
    }


}