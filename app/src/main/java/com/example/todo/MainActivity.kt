package com.example.todo



import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.todo.NotificationHelper.showNotification
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar


class MainActivity : AppCompatActivity() {



    private lateinit var coinTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var coinImageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long = 1 * 60 * 1000 // 30 minutes in milliseconds


    private lateinit var dbHelper: MyDBHelper

    private lateinit var rabbitImageView: ImageView
    private lateinit var clothesLayout: LinearLayout
    private lateinit var toggleButton: Button
    private var coins: Int = 0 // Initial coins

    private lateinit var startGameButton: Button










    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTextView = findViewById(R.id.timeTextView)
        coinImageView = findViewById(R.id.coinImageView)
        sharedPreferences = getSharedPreferences("CoinPrefs", Context.MODE_PRIVATE)


        dbHelper = MyDBHelper(this)
        coinTextView = findViewById(R.id.coinTextView)
        rabbitImageView = findViewById(R.id.rabbitImageView)
        clothesLayout = findViewById(R.id.clothesLayout)
        toggleButton = findViewById(R.id.toggleButton)

         startGameButton = findViewById(R.id.startGameButton)
        val gameSquare: RelativeLayout = findViewById(R.id.gameSquare)
        val iconInsideSquare: ImageView = findViewById(R.id.iconInsideSquare)


        coins = dbHelper.getCoins()
        updateCoinDisplay()

        // Update coin count based on elapsed time
        checkAndUpdateCoins()
        updateTimeDisplay()

        // Start the coin increment process
        startCoinIncrementTask()


//        deleteClothesData()


        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        shfaqImazhinAktivitetit(this) // Call function

//        val startGameButton: Button = findViewById(R.id.startGameButton)
//        startGameButton.setOnClickListener {
//            val intent = Intent(this, GameActivity::class.java)
//            startActivity(intent)
//        }


        // When Start Game button is clicked, show the square
        startGameButton.setOnClickListener {
            gameSquare.visibility = if (gameSquare.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // When the icon inside the square is clicked, start GameActivity
        iconInsideSquare.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        val quizTrigger: ImageView = findViewById(R.id.quizTrigger)
        quizTrigger.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        // Add to onCreate or after clothesLayout assignment
        toggleButton.setOnClickListener {
            clothesLayout.visibility = if (clothesLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // At the end of onCreate()
        applySavedClothing() // Apply saved clothing on app launch



    }




//
//    fun toggleClothesWindow(view: View) {
//        clothesLayout.visibility = if (clothesLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//    }

    fun selectClothes(view: View) {
        val clothName = when (view.id) {
            R.id.cloth1 -> "cloth1"
            R.id.cloth2 -> "cloth2"
            R.id.cloth3 -> "cloth3"
            else -> return
        }

        val price = when (clothName) {
            "cloth1" -> 200
            "cloth2" -> 400
            "cloth3" -> 600
            else -> return
        }

        if (dbHelper.isClothUnlocked(clothName)) {
            applyCloth(clothName)
        } else if (coins >= price) {
            coins -= price
            dbHelper.unlockCloth(clothName)
            dbHelper.updateCoins(coins)
            updateCoinDisplay()
            applyCloth(clothName)
        } else {
            Toast.makeText(this, "Not enough coins", Toast.LENGTH_SHORT).show()
        }
    }

    // Apply clothing and store in shared preferences
    private fun applyCloth(clothName: String) {
        val clothDrawable = getDrawableForCloth(clothName)
        clothDrawable?.let {
            rabbitImageView.setImageResource(it)
            saveSelectedClothing(clothName)
        }
    }

    // Persist selected clothing to SharedPreferences
    private fun saveSelectedClothing(clothName: String) {
        val prefs = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selectedCloth", clothName).apply()
    }

    // Apply saved clothing at startup
    private fun applySavedClothing() {
        val prefs = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        val selectedCloth = prefs.getString("selectedCloth", null)
        selectedCloth?.let {
            if (dbHelper.isClothUnlocked(it)) {
                val clothDrawable = getDrawableForCloth(it)
                clothDrawable?.let { drawable -> rabbitImageView.setImageResource(drawable) }
            }
        }
    }


    // Kthe ikonën për secilën veshje
    private fun getDrawableForCloth(clothName: String): Int? {
        return when (clothName) {
            "cloth1" -> R.drawable.clothes1
            "cloth2" -> R.drawable.cloth2
            "cloth3" -> R.drawable.cloth3
            else -> null
        }
    }

    private fun startCoinIncrementTask() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                incrementCoin()
                updateTimeDisplay()
                handler.postDelayed(this, interval)
            }
        }, interval)
    }

    private fun incrementCoin() {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT amount FROM Coins", null)

        var currentCoins = 0
        if (cursor.moveToFirst()) {
            currentCoins = cursor.getInt(0)
        }
        cursor.close()

        val newCoins = currentCoins + 1
        db.execSQL("UPDATE Coins SET amount = $newCoins")

        // ✅ Update SharedPreferences timestamp- e shtuar
        val sharedPreferences = getSharedPreferences("CoinPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putLong("lastUpdateTime", System.currentTimeMillis()).apply()


        updateCoinDisplay()
    }

    private fun updateCoinDisplay() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT amount FROM Coins", null)

        var coinCount = 0
        if (cursor.moveToFirst()) {
            coinCount = cursor.getInt(0)
        }
        cursor.close()

        coinTextView.text = "Coins: $coinCount"
    }

    private fun updateTimeDisplay() {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = dateFormat.format(Date(System.currentTimeMillis()))
        timeTextView.text = "Time: $currentTime"
    }

    private fun checkAndUpdateCoins() {
        val sharedPreferences = getSharedPreferences("CoinPrefs", Context.MODE_PRIVATE)
        val lastUpdateTime = sharedPreferences.getLong("lastUpdateTime", 0)
        val currentTime = System.currentTimeMillis()

        if (lastUpdateTime > 0) {
            val elapsedTime = currentTime - lastUpdateTime
            val intervalsPassed = (elapsedTime / interval).toInt()

            if (intervalsPassed > 0) {
                val db = dbHelper.writableDatabase
                val cursor = db.rawQuery("SELECT amount FROM Coins", null)

                var currentCoins = 0
                if (cursor.moveToFirst()) {
                    currentCoins = cursor.getInt(0)
                }
                cursor.close()

                val newCoins = currentCoins + intervalsPassed

                db.execSQL("UPDATE Coins SET amount = $newCoins")


            }
        }

        // ✅ Always update the last checked time- e shtuar
        sharedPreferences.edit().putLong("lastUpdateTime", currentTime).apply()

        updateCoinDisplay()
    }




    fun goNextPage(view: View) {
        view.findViewById<Button>(R.id.button2).setOnClickListener {
            val intent = Intent(this@MainActivity, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    fun goNextPage1(view: View) {
        view.findViewById<Button>(R.id.button).setOnClickListener {
            shfaqImazhinAktivitetit(this) // Call function
        }
    }


    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            println("Notification permission granted.")
        } else {
            println("Notification permission denied.")
        }
    }





    fun shfaqImazhinAktivitetit(context: Context) {
        val rregullat = LinkedList<String>()
        val helper = MyDBHelper(context)
        val db = helper.readableDatabase

        val date = Calendar.getInstance()
        val strDate = "${date.get(Calendar.DAY_OF_MONTH)}/${date.get(Calendar.MONTH)}/${date.get(Calendar.YEAR)}"

        var kaSkaduarAktiviteti = false;


        val query = "SELECT * FROM AKTIVITETES WHERE Data LIKE '$strDate' ORDER BY Ora ASC, Minutat ASC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {

            kaSkaduarAktiviteti = false;

            val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))


            val ora = cursor.getInt(cursor.getColumnIndexOrThrow("Ora"))
            val minutat = cursor.getInt(cursor.getColumnIndexOrThrow("Minutat"))
            val rregulli = cursor.getString(cursor.getColumnIndexOrThrow("Rregulli_rradhes"))

            rregullat.add(rregulli)

            val momenti = Calendar.getInstance()
            val oraTani = momenti.get(Calendar.HOUR_OF_DAY)
            val minutatTani = momenti.get(Calendar.MINUTE)

            if (oraTani > ora || (oraTani == ora && minutatTani > minutat)) {
                db.execSQL("DELETE FROM AKTIVITETES WHERE ID = $id")
                Toast.makeText(context, "Ka kaluar orari i aktivitetit", Toast.LENGTH_LONG).show()
                continue
            }



            if (!kaSkaduarAktiviteti) {
                // Llogarit intervalin kohor për ekzekutimin e aktivitetit
                val intervaliKohorNdodhiNgjarja =
                    ((ora - oraTani) * 60 * 60 * 1000) + ((minutat - minutatTani) * 60 * 1000) - 10000

                // Pret dhe ekzekuton logjikën pas intervalit të përcaktuar
                Handler().postDelayed({
                    val aktiviteti = rregullat.poll()
                    db.execSQL("DELETE FROM AKTIVITETES WHERE ID = $id")

                    // Shfaq njoftimin për aktivitetin
                    showNotification(context, aktiviteti)
                    //Toast.makeText(context, "Aktiviteti: $aktiviteti", Toast.LENGTH_LONG).show()
                    prove(aktiviteti)

                }, intervaliKohorNdodhiNgjarja.toLong())
            } else {
                // Njofton që aktiviteti ka skaduar
                Toast.makeText(context, "Ka kaluar orari i aktivitetit", Toast.LENGTH_LONG).show()
            }

        }
        cursor.close()
    }







    fun prove(activity: String) {
        val mapingActivityToImages = mapOf(
            "Zgjohu nga krevati" to "zgjohu",
            "Lahu" to "lahu",
            "Ha mengjes" to "mengjesi",
            "Shko shkolle" to "shkolla",
            "Meso" to "meso",
            "Shko ne sport" to "stervitja"
        )

        val imgName: String? = mapingActivityToImages[activity]

        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(2000, 2000)
        imageView.x = 20F
        imageView.y = 20F

        var imgResId: Int? = null

        when (imgName) {
            "zgjohu" -> imgResId = R.drawable.zgjohu
            "lahu" -> imgResId = R.drawable.lahu
            "mengjesi" -> imgResId = R.drawable.mengjesi
            "shkolla" -> imgResId = R.drawable.shkolla
            "stervitja" -> imgResId = R.drawable.stervitja
            "meso" -> imgResId = R.drawable.meso
        }

        imgResId?.let {
            imageView.setImageResource(it)

            val layout = findViewById<RelativeLayout>(R.id.layout)
            layout?.addView(imageView)
        }
    }
 //e shtuar
    override fun onResume() {
        super.onResume()
        coins = dbHelper.getCoins()
        updateCoinDisplay()
    }




//     Funksioni për të fshirë të dhënat nga tabela Clothes
//    fun deleteClothesData() {
//        dbHelper.deleteAllClothes() // Thirrja e funksionit për të fshirë të dhënat
//        Toast.makeText(this, "Të dhënat u fshinë!", Toast.LENGTH_SHORT).show() // Mesazh për konfirmim
//    }
}