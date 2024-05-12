package com.example.task_manager5

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.task_manager5.Adapters.ToDoAdapter

class RecyclerItemTouchHelper(private val adapter: ToDoAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.LEFT) {
            val builder = AlertDialog.Builder(adapter.getContext())
            builder.setTitle("Delete Task")
            builder.setMessage("Are you sure you want to delete this task?")
            builder.setPositiveButton(
                "Confirm"
            ) { dialog, which -> adapter.deleteItem(position) }
            builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                adapter.notifyItemChanged(
                    viewHolder.adapterPosition
                )
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            adapter.editItem(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val todo: Drawable?
        val background: ColorDrawable
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20
        if (dX > 0) {
            todo = ContextCompat.getDrawable(adapter.getContext(), R.drawable.baseline_edit_24)
            background = ColorDrawable(
                ContextCompat.getColor(
                    adapter.getContext(),
                    R.color.colorPrimaryDark
                )
            )
        } else {
            todo = ContextCompat.getDrawable(adapter.getContext(), R.drawable.baseline_delete_24)
            background = ColorDrawable(Color.RED)
        }
        val todoMargin = (itemView.height - todo!!.intrinsicHeight) / 2
        val todoTop = itemView.top + (itemView.height - todo.intrinsicHeight) / 2
        val todoBottom = todoTop + todo.intrinsicHeight
        if (dX > 0) { //swiping to the left
            val todoLeft = itemView.left + todoMargin
            val todoRight = itemView.left + todoMargin + todo.intrinsicHeight
            todo.setBounds(todoLeft, todoTop, todoRight, todoBottom)
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt() + backgroundCornerOffset,
                itemView.bottom
            )
        } else if (dX < 0) { //swiping to the right
            val todoLeft = itemView.right - todoMargin - todo.intrinsicHeight
            val todoRight = itemView.right - todoMargin
            todo.setBounds(todoLeft, todoTop, todoRight, todoBottom)
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset, itemView.top,
                itemView.right, itemView.bottom
            )
        } else {
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
        todo.draw(c)
    }
}