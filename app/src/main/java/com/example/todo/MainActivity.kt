package com.example.todo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        var helper = MyDBHelper(applicationContext)
        var db = helper.readableDatabase
//         var rs = db.rawQuery("Select * FROM AKTIVITETES", null)
//            var qury = "delete from AKTIVITETES " ;
//         var cursor = db?.execSQL( qury );

        setContentView(R.layout.activity_main);
    }

     fun goNextPage(view : View){
          view.findViewById<Button>(R.id.button2).setOnClickListener {
              val intent = Intent(this@MainActivity, SecondActivity::class.java);
              startActivity(intent);
          }
      }

    fun goNextPage1(view : View){
        view.findViewById<Button>(R.id.button).setOnClickListener {
            shfaqImazhinAktivitetit()
        }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }


    fun shfaqImazhinAktivitetit(){
        var rregullat = LinkedList<String>();

        var helper = MyDBHelper(applicationContext)
        var db = helper.readableDatabase

        // Formojme stringun e dates se sotme
        var date = Calendar.getInstance();
        var strDate = date.get(Calendar.DAY_OF_MONTH).toString();
        strDate = strDate + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR);

        var kaSkaduarAktiviteti = false;
        var index = -10;
        var id = -10;
        var ora = -10;
        var minutat = -10;
        var rregulli = "";

        // Formojme querin per te marre aktivitetet e dites se sotme te renditura nga me e vogla te me e madhja sipas ores dhe minutave
        var qury = "Select  * FROM  AKTIVITETES WHERE Data LIKE '" + strDate + "' order by Ora asc , minutat asc";
        var cursor = db.rawQuery(qury, null);

        // I iterojme rezultatet qe vijne nga egzekutimi i querit.
        while ( cursor.moveToNext() ){
            kaSkaduarAktiviteti = false;

            // Marrim id e rekordit
            index = cursor.getColumnIndex("ID")
            id = cursor.getInt(index);

            // Marrim oren nga rekordi
            index = cursor.getColumnIndex("Ora")
            ora = cursor.getInt(index)

            // Marrim minutat nga rekordi
            index = cursor.getColumnIndex("Minutat")
            minutat = cursor.getInt(index)

            // Marrim aktivitetin e planifikuar nga rekordi
            index = cursor.getColumnIndex("Rregulli_rradhes")
            rregulli = cursor.getString(index);

            // Mbajme rregullat ne liste
            rregullat.add(rregulli);

            // Marrim diten dhe oren ne kete moment
            var momenti = Calendar.getInstance();
            var oraTani = momenti.get(Calendar.HOUR_OF_DAY);
            var minutatTani = momenti.get(Calendar.MINUTE);

            // Kontrollojme per aktivitete qe kane qene te programuara te ndodhin me heret por ne te njejten ore.
            if (oraTani == ora && minutatTani > minutat) {
                // fshijme aktivitetin qe i ka skaduar afati nga DB
                db?.execSQL("delete from aktivitetes where ID =" + id);
                kaSkaduarAktiviteti = true;
            }

            // Kontrollojme per aktivitete qe kane qene te programuara te ndodhin me heret se oren ku jemi.
            if (oraTani > ora) {
                // fshijme aktivitetin qe i ka skaduar afati nga DB
                db?.execSQL("delete from aktivitetes where ID =" + id );
                kaSkaduarAktiviteti = true;
            }

            if( !kaSkaduarAktiviteti ){
                // Intervali Kohor qe te Ndodhi Ngjarja:
                // ((ora eventit - ora ne kete moment) * 60 min/ore * 60 s/min * 1000 mili s / s )
                var intervaliKohorNdodhiNgjarja = (ora - oraTani) * 60 * 60 * 1000;
                // Logarit pjesen e kohes per min dhe ia shtoj ores
                intervaliKohorNdodhiNgjarja = intervaliKohorNdodhiNgjarja + ((minutat - minutatTani) * 60 * 1000);
                intervaliKohorNdodhiNgjarja = intervaliKohorNdodhiNgjarja - 10000;

                // Pret aq sa eshte koha e llogaritur me siper perpara se te egzekutoje llogjiken brenda {} ne postDelayed
                Handler().postDelayed({
                    rregulli = rregullat.poll();
                    Toast.makeText(
                        applicationContext,
                        "aktiviteti: " + rregulli,
                        Toast.LENGTH_LONG
                    ).show();

//                    System.out.println("aktivitetes : " + rregulli);
                    db?.execSQL("delete from aktivitetes where ID =" + id);
                    prove(rregulli);

                }, intervaliKohorNdodhiNgjarja.toLong());

            }else{
                // Nje mesazh per te treguar qe aktiviteti ka skaduar
                Toast.makeText(
                    applicationContext,
                    "ka kaluar orari i aktivitetit",
                    Toast.LENGTH_LONG
                ).show();
            }
        }
        cursor.close();
    }

     fun prove (activity: String){
         val mapingActivityToImages = mapOf("Zgjohu nga krevati" to "zgjohu",
             "Lahu" to "lahu",
             "Ha mengjes" to "mengjesi",
             "Shko shkolle" to "shkolla",
             "Meso" to "muri",
             "Shko ne sport" to "stervitja")
//
         val imgName: String? = mapingActivityToImages.get(activity);

         val imageView = ImageView(this)
         // setting height and width of imageview
         imageView.layoutParams = LinearLayout.LayoutParams(2000, 2000)  // 2000, 2000
         imageView.x = 20F //setting margin from left
         imageView.y = 20F //setting margin from top

         var imgResId: Int? = 0;

         if (imgName.equals("zgjohu")){
             imgResId = R.drawable.zgjohu
         }

         if (imgName.equals("lahu")){
             imgResId = R.drawable.lahu
         }
         if (imgName.equals("mengjesi")){
             imgResId = R.drawable.mengjesi
         }
         if (imgName.equals("shkolla")){
             imgResId = R.drawable.shkolla
         }
         if (imgName.equals("stervitja")){
             imgResId = R.drawable.stervitja
         }
         if (imgName.equals("muri")){
             imgResId = R.drawable.muri
         }

         var resId = imgResId
         if (resId != null) {
             imageView.setImageResource(resId)

             //accessing our relative layout from activity_main.xml
             val layout = findViewById<RelativeLayout>(R.id.layout)
             // Add ImageView to LinearLayout
             layout?.addView(imageView)
         }
     }
}
