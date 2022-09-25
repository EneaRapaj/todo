package com.example.todo

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_second.view.*
import java.util.*


class SecondActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // access the items of the list
        val languages = resources.getStringArray(R.array.Languages)
        // access the spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                						android.R.layout.simple_spinner_item,
                                        languages)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View,
                                            position: Int,
                                            id: Long) {
                    Toast.makeText(this@SecondActivity,
                                getString(R.string.selected_item) + " " + "" + languages[position],
                                Toast.LENGTH_SHORT)
                    .show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action

                }
            }
        }

    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.imageView).setOnClickListener {
            getSet(view);
        }

        view.findViewById<Button>(R.id.buttonmbrapa).setOnClickListener {
            goPreviusPage(view);
        }
    }


    fun getSet(view: View) {
        var helper = MyDBHelper(applicationContext)
        var db = helper.readableDatabase
        val editTxt = findViewById<Spinner>(R.id.spinner);

        val msg = editTxt.getSelectedItem().toString();
        val editTxt1 = findViewById<EditText>(R.id.hour);
        val msg1 = editTxt1.text.toString();
        val editTxt2 = findViewById<EditText>(R.id.minute);
        val msg2 = editTxt2.text.toString();
        if ( msg1.toInt() > 22 || msg1.toInt() < 6) {
            Toast.makeText(
                applicationContext,
                "Ora duhet te jete midis 6 dhe 22",
                Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (msg2.toInt() > 59 || msg2.toInt() < 0) {
            Toast.makeText(
                applicationContext,
                "Minutat duhen te jene midis 00 dhe 59",
                Toast.LENGTH_LONG
            ).show();
            return;
        }

        var query = "Select count( Rregulli_rradhes ) from AKTIVITETES where ora = " + msg1 + " AND Minutat = " + msg2 + " ;";
        var cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if ( cursor.getString(0 ).toInt()  >= 1 ) {
            Toast.makeText(applicationContext, " Ky orar ekziston", Toast.LENGTH_LONG).show();
            return;
        }
        cursor.close();
        var cv = ContentValues();
        var date = Calendar.getInstance();
        //date.add( Calendar.DATE, 1 );
        var strDate = "";
        strDate = strDate + date.get( Calendar.DAY_OF_MONTH );
        strDate = strDate + "/";
        strDate = strDate + date.get( Calendar.MONTH );
        strDate = strDate + "/";
        strDate = strDate + date.get( Calendar.YEAR );
        cv.put("Rregulli_rradhes",msg );
        cv.put("Data", strDate );
        cv.put("Ora", msg1.toInt() );
        cv.put("Minutat", msg2.toInt() );
        db.insert("AKTIVITETES", null, cv);
    }

    fun goPreviusPage( view : View){
        val intent = Intent(this@SecondActivity, MainActivity::class.java);
        startActivity(intent);
    }

}
