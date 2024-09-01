import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.criticalthinkingexercise3.R

class TaskAdapter(
    context: Context,
    private val tasks: MutableList<String>,
    private val taskIds: MutableList<Long>
) : ArrayAdapter<String>(context, R.layout.list_view, tasks) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.list_view, parent, false)
        val tvTask: TextView = view.findViewById(R.id.tvTask)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)

        val task = tasks[position]
        val taskId = taskIds[position]

        tvTask.text = task

        btnComplete.setOnClickListener {
            completeTask(taskId, position)
        }
        btnDelete.setOnClickListener {
            deleteTask(taskId, position)
        }
        return view
    }

    private fun completeTask(taskId: Long, position: Int) {
        val db = DatabaseHelper(context).writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.STATUS, "Completed")
        }
        db.update(DatabaseHelper.TABLE_NAME, contentValues, "${DatabaseHelper.ID} = ?", arrayOf(taskId.toString()))
        db.close()

        tasks[position] = tasks[position].split(" (")[0] + " (Completed)"
        notifyDataSetChanged()
    }

    private fun deleteTask(taskId: Long, position: Int) {
        val db = DatabaseHelper(context).writableDatabase
        db.delete(DatabaseHelper.TABLE_NAME, "${DatabaseHelper.ID} = ?", arrayOf(taskId.toString()))
        db.close()
        tasks.removeAt(position)
        taskIds.removeAt(position)
        notifyDataSetChanged()
    }
}
