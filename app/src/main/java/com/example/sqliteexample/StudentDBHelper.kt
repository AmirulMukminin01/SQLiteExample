package com.example.sqliteexample

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class StudentDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${DBContract.UserEntry.TABLE_NAME} (" +
                    "${DBContract.UserEntry.COLUMN_NIM} TEXT PRIMARY KEY," +
                    "${DBContract.UserEntry.COLUMN_NAME} TEXT," +
                    "${DBContract.UserEntry.COLUMN_AGE} TEXT)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DBContract.UserEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun createStudent(student: StudentModel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DBContract.UserEntry.COLUMN_NIM, student.nim)
            put(DBContract.UserEntry.COLUMN_NAME, student.name)
            put(DBContract.UserEntry.COLUMN_AGE, student.age)
        }
        return db.insert(DBContract.UserEntry.TABLE_NAME, null, values)
    }

    fun searchStudentByNIM(nim: String): ArrayList<StudentModel> {
        val students = ArrayList<StudentModel>()
        val db = writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_NIM}=?", arrayOf(nim))
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        cursor?.use {
            if (it.moveToFirst()) {
                val name = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME))
                val age = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AGE))
                students.add(StudentModel(nim, name, age))
            }
        }

        return students
    }

    fun searchStudentByName(name: String): ArrayList<StudentModel> {
        val students = ArrayList<StudentModel>()
        val db = writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM ${DBContract.UserEntry.TABLE_NAME} WHERE ${DBContract.UserEntry.COLUMN_NAME} LIKE ?", arrayOf("%$name%"))
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        cursor?.use {
            while (it.moveToNext()) {
                val nim = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NIM))
                val name = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME))
                val age = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AGE))
                students.add(StudentModel(nim, name, age))
            }
        }

        return students
    }

    fun readStudents(): ArrayList<StudentModel> {
        val users = ArrayList<StudentModel>()
        val db = writableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM ${DBContract.UserEntry.TABLE_NAME} ORDER BY ${DBContract.UserEntry.COLUMN_NIM}", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        cursor?.use {
            while (it.moveToNext()) {
                val nim = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NIM))
                val name = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME))
                val age = it.getString(it.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AGE))
                users.add(StudentModel(nim, name, age))
            }
        }

        return users
    }

    @Throws(SQLiteConstraintException::class)
    fun updateStudent(student: StudentModel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DBContract.UserEntry.COLUMN_NAME, student.name)
            put(DBContract.UserEntry.COLUMN_AGE, student.age)
        }
        val selection = "${DBContract.UserEntry.COLUMN_NIM} = ?"
        val selectionArgs = arrayOf(student.nim)
        return db.update(DBContract.UserEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteStudent(nim: String): Int {
        val db = writableDatabase
        val selection = "${DBContract.UserEntry.COLUMN_NIM} = ?"
        val selectionArgs = arrayOf(nim)
        return db.delete(DBContract.UserEntry.TABLE_NAME, selection, selectionArgs)
    }
}
