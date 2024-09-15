package com.example.db_test.db

import android.provider.BaseColumns

// данный класс используется для работы MyDbManager
object MyDBNameClass : BaseColumns {
    // задаем константы для работы с таблицей и столбцами
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_URL = "uri"
    const val COLUMN_NAME_TIME = "time"

    // задаем имя и версию БД
    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "test.db"

    // запрос на создание таблицы
    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_CONTENT TEXT, " +
                "$COLUMN_NAME_URL TEXT, " +
                "$COLUMN_NAME_TIME TEXT)"

    // запрос на удаление таблицы
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}

/*
Урок по БД -  SQLite База Данных на Андроид (KOTLIN)/ Урок 17 - YT канал Neco
После - плейлист по Блокноту (10 уроков)
*/

