package com.example.task_manager5

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task_manager5.Adapters.ToDoAdapter
import com.example.task_manager5.Utils.DatabaseHandler
import com.example.task_manager5.Model.ToDoModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), DialogCloseListener {

    private lateinit var db: DatabaseHandler

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var taskCountText: TextView
    private lateinit var taskList: MutableList<ToDoModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        db = DatabaseHandler(this)
        db.openDatabase()

        taskCountText = findViewById(R.id.taskCountText)
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        fab = findViewById(R.id.fab)

        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksAdapter = ToDoAdapter(db, this@MainActivity)
        tasksRecyclerView.adapter = tasksAdapter
        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)

        updateTaskCount()

        showToastAfterDelay("Add text", 2000)
        showToastAfterDelay("Swipe to Edit or Delete", 6000)

        fab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }
    }

    private fun updateTaskCount(): Int {
        val totalCount = db.getTasksCount()
        taskCountText.text = "Total Task Count: $totalCount"
        return totalCount
    }

    private fun addNewTask(task: ToDoModel) {
        db.insertTask(task)
        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)
        tasksAdapter.notifyDataSetChanged()
        updateTaskCount()
    }

    private fun deleteTask(id: Int) {
        db.deleteTask(id)
        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)
        tasksAdapter.notifyDataSetChanged()

        // Get the updated task count directly from the database
        val totalCount = db.getTasksCount()
        Log.d("MainActivity", "Total Task Count after delete: $totalCount") // Log the count to see if it's correct

        // Update the TextView with the new total task count
        taskCountText.text = "Total Task Count: $totalCount"
    }







    override fun handleDialogClose(dialog: DialogInterface) {
        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)
        tasksAdapter.notifyDataSetChanged()
        updateTaskCount()
    }

    private fun showToastAfterDelay(message: String, delayMillis: Long) {
        Handler().postDelayed({ showToast(message) }, delayMillis)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
