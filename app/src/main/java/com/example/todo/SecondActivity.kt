package com.example.todo


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.databinding.ActivitySecondBinding
import java.util.*




class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Access the items of the list
        val languages = resources.getStringArray(R.array.Languages)

        // Access the spinner using View Binding
        val spinner = binding.spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(this@SecondActivity, getString(R.string.selected_item) + " " + languages[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Action if nothing selected
            }
        }

        // Button click listeners using View Binding
        binding.imageView.setOnClickListener { view -> getSet(view)  }
        binding.buttonmbrapa.setOnClickListener {  view -> goPreviousPage(view) }
    }

    private fun getSet(view: View) {
        val helper = MyDBHelper(applicationContext)
        val db = helper.readableDatabase

        // Access spinner and edit texts using View Binding
        val msg = binding.spinner.selectedItem.toString()
        val msg1 = binding.hour.text.toString()
        val msg2 = binding.minute.text.toString()

        if (msg1.toInt() > 22 || msg1.toInt() < 6) {
            Toast.makeText(applicationContext, "Ora duhet te jete midis 6 dhe 22", Toast.LENGTH_LONG).show()
            return
        }

        if (msg2.toInt() > 59 || msg2.toInt() < 0) {
            Toast.makeText(applicationContext, "Minutat duhen te jene midis 00 dhe 59", Toast.LENGTH_LONG).show()
            return
        }

        val query = "SELECT COUNT(Rregulli_rradhes) FROM AKTIVITETES WHERE Ora = $msg1 AND Minutat = $msg2;"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()
        if (cursor.getString(0).toInt() >= 1) {
            Toast.makeText(applicationContext, "Ky orar ekziston", Toast.LENGTH_LONG).show()
            return
        }
        cursor.close()

        val cv = ContentValues()
        val date = Calendar.getInstance()
        val strDate = "${date[Calendar.DAY_OF_MONTH]}/${date[Calendar.MONTH]}/${date[Calendar.YEAR]}"
        cv.put("Rregulli_rradhes", msg)
        cv.put("Ora", msg1)
        cv.put("Minutat", msg2)
        cv.put("Data", strDate)
        db.insert("AKTIVITETES", null, cv)
        Toast.makeText(applicationContext, "Aktiviteti i ruajtur", Toast.LENGTH_SHORT).show()
    }

    private fun goPreviousPage(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}
