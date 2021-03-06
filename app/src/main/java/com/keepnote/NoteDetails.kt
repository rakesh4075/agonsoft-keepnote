package com.keepnote

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import com.keepnote.databinding.ActivityNoteDetailsBinding

class NoteDetails : AppCompatActivity() {
    lateinit var binding:ActivityNoteDetailsBinding
    private var title:String?=null
    private  var content:String?=null
    private var colorcode:Int?=null
    private var noteId:Long?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_note_details
        )
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent?.let {
            intent -> title = intent.getStringExtra("title")
            content = intent.getStringExtra("content")
            colorcode = intent.getIntExtra("colorcode",0)
            noteId = intent.getLongExtra("noteid",5)
        }

        if (title!=null && content!=null && colorcode!=null){
            binding.noteDetailsTitle.text = title
            binding.noteContentll.noteDetailsContent.text = HtmlCompat.fromHtml(content!!,HtmlCompat.FROM_HTML_MODE_COMPACT)
            if ((colorcode.toString().subSequence(0,1) as String) == "-"){
                binding.noteContentll.noteDetailsContent.setBackgroundColor(colorcode!!)
                binding.noteContentll.txtContentll.setBackgroundColor(colorcode!!)
            }else{
                binding.noteContentll.noteDetailsContent.setBackgroundColor(ContextCompat.getColor(this,colorcode!!))
                binding.noteContentll.txtContentll.setBackgroundColor(ContextCompat.getColor(this,colorcode!!))
            }

        }



        binding.fab.setOnClickListener {
            val editNoteIntent = Intent(this, EditNote::class.java)
            if (title!=null && content!=null && colorcode!=null){
                editNoteIntent.putExtra("noteid",noteId)
                editNoteIntent.putExtra("from",0)
                startActivity(editNoteIntent)
                finish()
            }

        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
