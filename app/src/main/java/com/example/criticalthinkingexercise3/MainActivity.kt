package com.example.criticalthinkingexercise3
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import DatabaseHelper
import TaskAdapter
import android.content.ContentValues
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var taskList: MutableList<String>
    private lateinit var taskIds: MutableList<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)
        taskList = mutableListOf()
        taskIds = mutableListOf()

        adapter = TaskAdapter(this, taskList, taskIds)
        findViewById<ListView>(R.id.listView).adapter = adapter

        loadTasks()

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val task = findViewById<EditText>(R.id.etTask).text.toString()
            if (task.isNotEmpty()) {
                addTask(task)
            }
        }
    }

    // add to db and list
    private fun addTask(task: String) {
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.TASK_NAME, task)
            put(DatabaseHelper.STATUS, "Pending")
        }
        val taskId = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues)
        db.close()

        if (taskId != -1L) {
            taskList.add(task)
            taskIds.add(taskId)
            adapter.notifyDataSetChanged()
        }

        findViewById<EditText>(R.id.etTask).text.clear()
    }

    //retrieve from DB and update list
    private fun loadTasks() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)
        taskList.clear()
        taskIds.clear()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.ID))
                val taskName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TASK_NAME))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STATUS))
                taskList.add("$taskName ($status)")
                taskIds.add(id)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        adapter.notifyDataSetChanged()
    }
}



