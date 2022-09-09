package com.example.todo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        var helper = MyDBHelper(applicationContext)
        var db = helper.readableDatabase
//         var rs = db.rawQuery("Select * FROM AKTIVITETES", null)
//          var qury = "delete from AKTIVITETES " ;
//         var cursor = db?.execSQL( qury );
    }

     fun goNextPage(view : View){
          view.findViewById<Button>(R.id.button2).setOnClickListener {
              val intent = Intent(this@MainActivity, SecondActivity::class.java);
              startActivity(intent);
          }
      }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        shfaqImazhinAktivitetit();
//        view.findViewById<Button>(R.id.button2).setOnClickListener {
//            getSet(view)
//        }
    }

    fun shfaqImazhinAktivitetit(){
        println("shfaqImazhinAktivitetit");
        var helper = MyDBHelper(applicationContext)
        var db = helper.readableDatabase
        var date = Calendar.getInstance();
        var strDate = date.get(Calendar.DAY_OF_MONTH).toString();
//        var strDate = ( date.get(Calendar.DAY_OF_MONTH) + 1  ).toString() ;
        strDate = strDate + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR);
        var kaSkaduarAktiviteti = false;
        var index = -10;
        var id = -10;
        var ora = -10;
        var minutat = -10;
        var rregulli = "";
        var qury = "Select  * FROM  AKTIVITETES WHERE Data LIKE '" + strDate + "' order by Ora asc , Minutat asc";
        var cursor = db.rawQuery(qury, null);
        while ( cursor.moveToNext() ){
            kaSkaduarAktiviteti = false;
            index = cursor.getColumnIndex("ID")
            id = cursor.getInt(index);
            index = cursor.getColumnIndex("Ora")
            ora = cursor.getInt(index)
            index = cursor.getColumnIndex("Minutat")
            minutat = cursor.getInt(index)
            index = cursor.getColumnIndex("Rregulli_rradhes")
            rregulli = cursor.getString(index);
            var momenti = Calendar.getInstance();
            var hourNow = momenti.get(Calendar.HOUR_OF_DAY);
            var minuteNow = momenti.get(Calendar.MINUTE);
            if (hourNow == ora && minuteNow > minutat) {
                Toast.makeText(
                    applicationContext,
                    "ka kaluar orari i aktivitetit",
                    Toast.LENGTH_LONG
                ).show();
                db?.execSQL("delete from aktivitetes where ID =" + id);
                kaSkaduarAktiviteti = true;
            }
            if (hourNow > ora) {
                Toast.makeText(
                    applicationContext,
                    "ka kaluar orari i aktivitetit",
                    Toast.LENGTH_LONG
                ).show();
                db?.execSQL("delete from aktivitetes where ID =" + id );
                kaSkaduarAktiviteti = true;
            }
            if( !kaSkaduarAktiviteti ){
                while ( hourNow != ora ){
                    Thread.sleep(1000);
                    momenti = Calendar.getInstance();
                    hourNow = momenti.get(Calendar.HOUR_OF_DAY);
                }
                while ( minuteNow != minutat ){
                    Thread.sleep(1000);
                    momenti = Calendar.getInstance();
                    minuteNow = momenti.get(Calendar.MINUTE);
                }
            }
            if (hourNow == ora && minuteNow == minutat) {
//                Afisho foton
                Toast.makeText(applicationContext, "aktiviteti : " + rregulli, Toast.LENGTH_LONG)
                    .show();
                db?.execSQL("delete from aktivitetes where ID =" + id);
            }
            Thread.sleep(1000);
        }
        cursor.close();
    }

     fun prove (view: View){
         shfaqImazhinAktivitetit();
     }


    // fun goNextPage(view: View) {
    //   val editTxt = findViewById<EditText>(R.id.editText2);
    //    val msg = editTxt.text.toString();

    // val txtview=findViewById<TextView>(R.id.textview).apply {
    //       text = msg
    //  }
    //   println(msg)
    //   }
}
