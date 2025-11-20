package com.example.bai04

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.NonNull
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var listView: ListView
    private lateinit var arrayList: ArrayList<NotesModel>
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView1)
        arrayList = ArrayList()
        adapter = NotesAdapter(this, R.layout.row_notes, arrayList)
        listView.adapter = adapter
        try {
            databaseHandler = DatabaseHandler(this, "GhiChu.sqlite", null, 1)

            databaseHandler.QueryData("CREATE TABLE IF NOT EXISTS Notes(Id INTEGER PRIMARY KEY AUTOINCREMENT, NameNotes VARCHAR(200))")
            try {
                val cursorCount = databaseHandler.GetData("SELECT COUNT(*) FROM Notes")
                var count = 0
                if (cursorCount.moveToFirst()) count = cursorCount.getInt(0)
                cursorCount.close()
                if (count == 0) {
                    val sampleNames = listOf(
                        "Ví dụ SQLite 1",
                        "Ví dụ SQLite 2",
                        "Ví dụ SQLite 3",
                        "Đi chợ",
                        "Học Android",
                        "Đọc sách",
                        "Tập thể dục"
                    )
                    for (name in sampleNames) {
                        databaseHandler.QueryData("INSERT INTO Notes VALUES(null, '$name')")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            getData()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi khởi tạo cơ sở dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }

        val fabAddNote = findViewById<FloatingActionButton>(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            dialogThem()
        }
    }

    private fun getData() {
        try {
            val cursor: Cursor? = try {
                databaseHandler.GetData("SELECT * FROM Notes")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            arrayList.clear()
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(0)
                    val ten = cursor.getString(1)
                    arrayList.add(NotesModel(id, ten))
                }
                try {
                    cursor.close()
                } catch (ignored: Exception) {
                }
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_notes, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuAddNotes) {
            dialogThem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogThem() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_them_notes)

        val edtName = dialog.findViewById<EditText>(R.id.editTextName)
        val btnThem = dialog.findViewById<Button>(R.id.buttonThem)
        val btnHuy = dialog.findViewById<Button>(R.id.buttonHuy)

        btnThem.setOnClickListener {
            val tenCV = edtName.text.toString()
            if (tenCV.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show()
            } else {
                databaseHandler.QueryData("INSERT INTO Notes VALUES(null, '$tenCV')")
                Toast.makeText(this, "Đã thêm", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                getData()
            }
        }

        btnHuy.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun dialogCapNhatNotes(ten: String, id: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_sua_notes)

        val edtName = dialog.findViewById<EditText>(R.id.editTextNameEdit)
        val btnXacNhan = dialog.findViewById<Button>(R.id.buttonXacNhan)
        val btnHuy = dialog.findViewById<Button>(R.id.buttonHuyEdit)

        edtName.setText(ten)

        btnXacNhan.setOnClickListener {
            val tenMoi = edtName.text.toString().trim()
            databaseHandler.QueryData("UPDATE Notes SET NameNotes = '$tenMoi' WHERE Id = '$id'")
            Toast.makeText(this, "Đã cập nhật", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            getData()
        }

        btnHuy.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun dialogDeleteNotes(ten: String, id: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Bạn có muốn xóa công việc $ten không?")
        dialog.setPositiveButton("Có") { _: DialogInterface, _: Int ->
            databaseHandler.QueryData("DELETE FROM Notes WHERE Id = '$id'")
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
            getData()
        }
        dialog.setNegativeButton("Không") { _: DialogInterface, _: Int ->
        }
        dialog.show()
    }
}