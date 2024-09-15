package com.example.db_test.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class MyDbManager(val context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        this.db = myDbHelper.writableDatabase
    }

    // функция добавления данных в БД
    fun insertToDb(title: String, content: String, uri: String, time: String){
        val values = ContentValues().apply {
            put(MyDBNameClass.COLUMN_NAME_TITLE, title)
            put(MyDBNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDBNameClass.COLUMN_NAME_URL, uri)
            put(MyDBNameClass.COLUMN_NAME_TIME, time)
        }
        db?.insert(MyDBNameClass.TABLE_NAME, null, values)
    }

    // функция удаления данных в БД
    fun delFromDb(id: String){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDBNameClass.TABLE_NAME, selection, null)
    }

    // функция обновления/редактирования данных в БД
    fun updateItem(title: String, content: String, uri: String, time: String, id:Int){
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDBNameClass.COLUMN_NAME_TITLE, title)
            put(MyDBNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDBNameClass.COLUMN_NAME_URL, uri)
            put(MyDBNameClass.COLUMN_NAME_TIME, time)
        }
        db?.update(MyDBNameClass.TABLE_NAME,  values, selection,null)
    }

    /*
    Функция считывания с БД.
    Возвращаем массив ArrayList с кол-вом элементов ListItem
    */
    fun readDbData(searchText:String) : ArrayList<ListItem>{
        // создаем массив
        val dataList = ArrayList<ListItem>()
        // создаем переменную для поиска
        val selection = "${MyDBNameClass.COLUMN_NAME_TITLE} like ?"

        // организуем запрос на интересующие нас данные с БД
        val cursor = db?.query(
            MyDBNameClass.TABLE_NAME, null, selection, arrayOf("%$searchText%"),
            null, null, null)

        //идет перебор данных из запроса до конца курсора. Эти данные кладем в dataList
            while(cursor?.moveToNext()!!){
                val dataTitle = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_NAME_TITLE))
                val dataContent = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_NAME_CONTENT))
                val dataURI = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_NAME_URL))
                val dataTime = cursor.getString(cursor.getColumnIndexOrThrow(MyDBNameClass.COLUMN_NAME_TIME))
                val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))

                val item = ListItem()

                item.title = dataTitle
                item.desc = dataContent
                item.uri = dataURI
                item.time = dataTime
                item.id = dataId

                dataList.add(item)
            }

        cursor.close()
        return dataList
    }

    fun closeDb(){
        myDbHelper.close()
    }
}