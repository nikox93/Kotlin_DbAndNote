package com.example.db_test

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.db_test.databinding.EditActivityBinding
import com.example.db_test.db.MyDbManager
import com.example.db_test.db.MyIntentConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EditActivity : AppCompatActivity() {

    var id = 0
    var editState = false
    private lateinit var bindingClassEA : EditActivityBinding
    private val myDbManager = MyDbManager(this)

    val imageRequestCode = 10
    var tempImageURI = "empty" //тут храним ссылку на картинку

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClassEA = EditActivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingClassEA.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getMyIntents()
        Log.d("MyLog","myTime:" + getCurrentTime())
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == imageRequestCode){
            var imageView = bindingClassEA.imageView
            imageView.setImageURI(data?.data)
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        tempImageURI = data?.data.toString()
    }

    fun onClickAddImage(view: View) {
        val main_IL = bindingClassEA.mainImageLayout
        val fbAddImage = bindingClassEA.fbPic

        main_IL.visibility = View.VISIBLE
        fbAddImage.visibility = View.GONE
    }

    fun onClickImageDel(view: View) {
        val main_IL = bindingClassEA.mainImageLayout
        val fbAddImage = bindingClassEA.fbPic

        main_IL.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
        tempImageURI = "empty"
    }

    fun onClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, this.imageRequestCode)
    }

    fun onClickSave(view: View) {
        val eTitle = bindingClassEA.eTitle
        val eDesc = bindingClassEA.eDesc

        val myTitle = eTitle.text.toString()
        val myDesc = eDesc.text.toString()

        // запускаем функцию Coroutine для оптимизации потоков - урок 10 Блокнот
        CoroutineScope(Dispatchers.Main).launch {
            if(myTitle != "" && myDesc != ""){
                if(editState) {
                    myDbManager.updateItem(myTitle, myDesc, tempImageURI, getCurrentTime(), id)
                } else {
                    myDbManager.insertToDb(myTitle, myDesc, tempImageURI, getCurrentTime())
                }
                finish()
            }
        }

    }

    fun onEditEnable(view: View){
        val eTitle = bindingClassEA.eTitle
        val eDesc = bindingClassEA.eDesc
        val fbEdit = bindingClassEA.fbEdit
        val fbAddImage = bindingClassEA.fbPic
        val imButtonDel = bindingClassEA.imButtonDel
        val imButtonEdit = bindingClassEA.imButtonEdit

        eTitle.isEnabled = true
        eDesc.isEnabled = true
        fbEdit.visibility = View.GONE
        fbAddImage.visibility = View.VISIBLE
        if(tempImageURI != "empty") {
            imButtonEdit.visibility = View.VISIBLE
            imButtonDel.visibility = View.VISIBLE
        }
    }

    fun getMyIntents() {
        val eTitle = bindingClassEA.eTitle
        val eDesc = bindingClassEA.eDesc
        val m_il = bindingClassEA.mainImageLayout
        val fbAddImage = bindingClassEA.fbPic
        val imButtonDel = bindingClassEA.imButtonDel
        val imButtonEdit = bindingClassEA.imButtonEdit
        val fbEdit = bindingClassEA.fbEdit
        var imageView = bindingClassEA.imageView

        fbEdit.visibility = View.GONE
        val i = intent

        if (i != null) {
            if (i.getStringExtra(MyIntentConstant.I_TITLE_KEY) != null) {
                fbAddImage.visibility = View.GONE
                eTitle.setText(i.getStringExtra(MyIntentConstant.I_TITLE_KEY))
                editState = true
                eTitle.isEnabled = false
                eDesc.isEnabled = false
                fbEdit.visibility = View.VISIBLE
                eDesc.setText(i.getStringExtra(MyIntentConstant.I_DESC_KEY))
                id = i.getIntExtra(MyIntentConstant.I_ID_KEY, 0)
                if (i.getStringExtra(MyIntentConstant.I_URI_KEY) != "empty") {
                    m_il.visibility = View.VISIBLE
                    tempImageURI = i.getStringExtra(MyIntentConstant.I_URI_KEY)!! //получаем ссылку на картинку
                    imageView.setImageURI(Uri.parse(tempImageURI)) //вставляем в наш ImageView
                    imButtonEdit.visibility = View.GONE
                    imButtonDel.visibility = View.GONE
                }
            }
        }
    }

    // функция получения и форматирования времени. Возвращает тип String
    private fun getCurrentTime():String{
        //получили время
        val time = Calendar.getInstance().time
        //Отформатировали. Кстати, kk - для 24 часового формата
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        val fTime = formatter.format(time)
        // вернули
        return fTime.toString()
    }
}