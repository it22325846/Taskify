package com.example.task_manager5.Utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.task_manager5.Model.ToDoModel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    companion object {
        private const val VERSION = 1
        private const val NAME = "toDoListDatabase"
        private const val TODO_TABLE = "todo"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
        private const val CREATE_TODO_TABLE =
            "CREATE TABLE $TODO_TABLE ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $TASK TEXT, $STATUS INTEGER)"
    }

    private lateinit var db: SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        onCreate(db)
    }

    fun openDatabase() {
        db = this.writableDatabase
    }

    fun insertTask(task: ToDoModel) {
        val cv = ContentValues().apply {
            put(TASK, task.task)
            put(STATUS, 0)
        }
        db.insert(TODO_TABLE, null, cv)
        // Increment task count by 1 after inserting a new task
        updateTaskCount(1)
    }

    fun updateTaskCount(change: Int) {
        // Get the current total task count
        var totalCount = getTasksCount()
        // Add the change (positive for increase, negative for decrease)
        totalCount += change
        // If the count becomes negative, set it to 0
        if (totalCount < 0) totalCount = 0
        Log.d("DatabaseHandler", "Total Task Count: $totalCount")
        // You can also use this count for any other purpose
    }

    fun getAllTasks(): List<ToDoModel> {
        val taskList = mutableListOf<ToDoModel>()
        val query = "SELECT * FROM $TODO_TABLE"
        val cur = db.rawQuery(query, null)
        cur.use {
            val idIndex = it.getColumnIndex(ID)
            val taskIndex = it.getColumnIndex(TASK)
            val statusIndex = it.getColumnIndex(STATUS)

            while (it.moveToNext()) {
                val id = it.getInt(idIndex)
                val task = it.getString(taskIndex)
                val status = it.getInt(statusIndex)

                val taskModel = ToDoModel().apply {
                    this.id = id
                    this.task = task
                    this.status = status
                }
                taskList.add(taskModel)
            }
        }
        return taskList
    }

    fun updateStatus(id: Int, status: Int) {
        val cv = ContentValues().apply {
            put(STATUS, status)
        }
        db.update(TODO_TABLE, cv, "$ID = ?", arrayOf(id.toString()))
    }

    fun updateTask(id: Int, task: String) {
        val cv = ContentValues().apply {
            put(TASK, task)
        }
        db.update(TODO_TABLE, cv, "$ID = ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db.delete(TODO_TABLE, "$ID = ?", arrayOf(id.toString()))
        // Decrease task count by 1 after deleting a task
        updateTaskCount(-1)
    }


    fun getTasksCount(): Int {
        val query = "SELECT COUNT(*) FROM $TODO_TABLE"
        val cur = db.rawQuery(query, null)
        var count = 0
        cur.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        return count
    }
}
