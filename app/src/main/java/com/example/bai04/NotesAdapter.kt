package com.example.bai04

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class NotesAdapter(
    private val context: Context,
    private val layout: Int,
    private val notesList: MutableList<NotesModel>
) : BaseAdapter() {
    override fun getCount(): Int = notesList.size
    override fun getItem(position: Int): Any = notesList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    private class ViewHolder {
        var txtName: TextView? = null
        var imgEdit: ImageView? = null
        var imgDelete: ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            holder = ViewHolder()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(layout, null)
            holder.txtName = view.findViewById(R.id.textViewNameNote)
            holder.imgEdit = view.findViewById(R.id.imageViewEdit)
            holder.imgDelete = view.findViewById(R.id.imageViewDelete)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        val notes = notesList[position]
        holder.txtName?.text = notes.nameNote
        holder.imgEdit?.setOnClickListener {
            (context as MainActivity).dialogCapNhatNotes(notes.nameNote, notes.idNote)
        }
        holder.imgDelete?.setOnClickListener {
            (context as MainActivity).dialogDeleteNotes(notes.nameNote, notes.idNote)
        }
        return view!!
    }
}