package com.keepnote

//import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepnote.colorpicker.ColorPicker
import com.keepnote.databinding.ActivityEditNoteBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.NoteDatabase
import com.keepnote.notesDB.NoteViewmodel
import com.keepnote.notesDB.NoteViewmodelFactory
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.utils.ExceptionTrack
import com.keepnote.view.homescreen.HomeScreen
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class EditNote : AppCompatActivity() {
    private var displayWidth: Int=0
    private var displayHeight: Int =0
    private val gALLERYREQUESTIMAGE: Int = 1
    private var notecolor: Int = 0
    private lateinit var viewmodel: NoteViewmodel
    private var title:String?=null
    private  var content:String?=null
    private var colorcode:Int?=null
    private var fromPage:Int?=0
    lateinit var binding:ActivityEditNoteBinding
   // private lateinit var fstore:FirebaseFirestore
    private var noteId:Long?=null
    private var isFavourite=0
    private var note:Notes?=null
    private var addImageRecyclerview: AddImageRecyclerview?=null
    private var imageUri:ArrayList<Bitmap?>?=null
    private var isDarktheme = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarktheme =
            if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean) {
                setTheme(R.style.DarkTheme)
                true
            } else{
                setTheme(R.style.LightTheme)
                false
            }


        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_edit_note
        )


        binding.toolbar.overflowIcon = AppCompatResources.getDrawable(this,R.drawable.ic_menu_overflow)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
      //  fstore = FirebaseFirestore.getInstance()

        val application = requireNotNull(this).application
        val dataSource = NoteDatabase.invoke(this).getNoteDao()
        val noteViewmodelFactory = NoteViewmodelFactory(dataSource,application)
        viewmodel = ViewModelProviders.of(this,noteViewmodelFactory).get(NoteViewmodel::class.java)
        binding.noteviewmodel = viewmodel
        binding.lifecycleOwner = this
        init()
        imageUri = ArrayList()
        if (imageUri!=null)
        addImageRecyclerview = AddImageRecyclerview(imageUri!!)
        binding.noteContenteditll.imagerecyclerview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.noteContenteditll.imagerecyclerview.adapter = addImageRecyclerview


        intent?.let {
                intent ->
            title = intent.getStringExtra("title")
            content = intent.getStringExtra("content")
            fromPage = intent.getIntExtra("from",0)
            noteId = intent.getLongExtra("noteid",5)
        }


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayHeight = displayMetrics.heightPixels
        displayWidth = displayMetrics.widthPixels





        binding.favToogle.isChecked = false
        binding.favToogle.background = AppCompatResources.getDrawable(this,R.drawable.ic_favorite)
        binding.favToogle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                isFavourite =1
                binding.favToogle.background = AppCompatResources.getDrawable(this,R.drawable.ic_favorite_checked)
            }else{
                isFavourite = 0
                binding.favToogle.background = AppCompatResources.getDrawable(this,R.drawable.ic_favorite)
            }

        }


        if (fromPage!=null && fromPage==0){
            if (noteId!=null) {
               viewmodel.getNotebyId(noteId)
                viewmodel.note.observe(this, Observer {
                   note = it
                    if (note!=null) {
                        binding.favToogle.isChecked = note?.isFavourite==1
                        binding.editNoteTitle.setText(note?.title)
                        binding.noteContenteditll.editNoteContent.setText(note?.content?.let { it1 ->
                            HtmlCompat.fromHtml(
                                it1,HtmlCompat.FROM_HTML_MODE_COMPACT)
                        })
                        colorcode = note?.notecolor
                        if (colorcode!=null){
                            if ((colorcode.toString().subSequence(0, 1) as String) == "-") {
                                if (isDarktheme || notecolor!=-16777216){
                                    binding.noteContenteditll.editNoteContent.setTextColor(ContextCompat.getColor(this@EditNote,R.color.black))
                                }else binding.noteContenteditll.editNoteContent.setTextColor(ContextCompat.getColor(this@EditNote,R.color.white))
                                binding.noteContenteditll.editNoteContent.setBackgroundColor(colorcode!!)
                                binding.noteContenteditll.txtContentll.setBackgroundColor(colorcode!!)
                            } else {
                                binding.noteContenteditll.editNoteContent.setBackgroundColor(  if (isDarktheme) ContextCompat.getColor(this,R.color.black) else ContextCompat.getColor(this,R.color.lightprimary))
                                binding.noteContenteditll.txtContentll.setBackgroundColor( if (isDarktheme) ContextCompat.getColor(this,R.color.black) else ContextCompat.getColor(this,R.color.lightprimary))
                            }
                        }

                    }
                })

            }
        }

        notecolor = if (isDarktheme) R.color.black else R.color.lightprimary
        binding.imageBox.setOnClickListener { colorImageBox() }

    }

    private fun init(){
        binding.noteContenteditll.raksToolbar.setEditText(binding.noteContenteditll.editNoteContent)
        if (!isDarktheme) binding.noteContenteditll.editNoteContent.setBackgroundColor(ContextCompat.getColor(this,R.color.lightprimary)) else  binding.noteContenteditll.editNoteContent.setBackgroundColor(ContextCompat.getColor(this,R.color.black))
        binding.noteContenteditll.editNoteContent.setToolbar(binding.noteContenteditll.raksToolbar)

    }

    private fun updateNote(title:String,content:String,isFavourite:Int) {
        viewmodel.updateNote(noteId,title,content,colorcode,isFavourite = isFavourite)
        Constants.showToast("Note updated",this)
    }


    private fun colorImageBox() {
        val coloPicker = ColorPicker(this)
        coloPicker.setOnFastChooseColorListener(object :ColorPicker.OnFastChooseColorListener{
            override fun setOnFastChooseColorListener(position: Int, color: Int) {
                binding.noteContenteditll.editNoteContent.setBackgroundColor(color)
                binding.noteContenteditll.txtContentll.setBackgroundColor(color)
                notecolor = color
                colorcode = color

                if (isDarktheme || notecolor!=-16777216){
                    binding.noteContenteditll.editNoteContent.setTextColor(ContextCompat.getColor(this@EditNote,R.color.black))
                }else binding.noteContenteditll.editNoteContent.setTextColor(ContextCompat.getColor(this@EditNote,R.color.white))


            }

            override fun onCancel() {

            }
        })

            .setColumns(5)
            .setColors(Constants.colorLists())
            .show()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        saveNote()
        startActivity(Intent(this, HomeScreen::class.java))
        finish()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> {
                onBackPressed()

            }
            R.id.savenote ->{
              onBackPressed()

            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        Constants.keyPadClose(this)
        binding.noteSaveProgress.visibility = View.VISIBLE
        val title = binding.editNoteTitle.text.toString()
        val content = binding.noteContenteditll.editNoteContent.getHtml()
        if (content!=null){
            if (title.isEmpty() && content == "<html><body></body></html>"){
                Constants.showToast("Required field is empty",this)
            }else{
                if (fromPage==0) {
                    updateNote(title,content,isFavourite)
                } else {
                    try {
                        viewmodel.insertNote(Notes(0,title, content,notecolor,isFavourite = isFavourite))
                        when {
                            StoreSharedPrefData.INSTANCE.getPref("showIntersialAd",0,this) as Int >=2 && fromPage!=null && fromPage!=0 -> {
                                Constants.showIntersialAd(this)
                            }
                            else -> {
                                val value = StoreSharedPrefData.INSTANCE.getPref("showIntersialAd",0,this) as Int + 1
                                StoreSharedPrefData.INSTANCE.savePrefValue("showIntersialAd",value,this)
                            }
                        }
                    }catch (e:Exception){
                        ExceptionTrack.getInstance().TrackLog(e)
                    }

                }

            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode== RESULT_OK && data!=null){
            when(requestCode){
                gALLERYREQUESTIMAGE ->{
                    val selectedImage = data.data
                    try {
                        val bitmap = decodeSampledBitmapFromUri(this,selectedImage,500,500)
                        val resizedbitmap = bitmap?.let { Bitmap.createScaledBitmap(it,
                            (displayWidth*.8).toInt(), (displayHeight*.35).toInt(),true) }
                        showImageInImageview(resizedbitmap)
                    //    addImageInEditText(bitmap)
                    }catch (e:Exception){
                        ExceptionTrack.getInstance().TrackLog(e)
                    }

                }
            }
        }
    }


    @Throws(FileNotFoundException::class)
    fun decodeSampledBitmapFromUri(context: Context, imageUri: Uri?, reqWidth: Int, reqHeight: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            // Get input stream of the image
            val options = BitmapFactory.Options()

            var iStream: InputStream? = imageUri?.let { context.contentResolver.openInputStream(it) }

            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(iStream, null, options)
            iStream?.close()
            iStream = imageUri?.let { context.contentResolver.openInputStream(it) }

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeStream(iStream, null, options)

            iStream?.close()
        } catch (e: FileNotFoundException) {
            ExceptionTrack.getInstance().TrackLog(e)
        } catch (e: IOException) {
            ExceptionTrack.getInstance().TrackLog(e)
        }
        return bitmap
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {

        // Raw height and width of image
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }





    private fun showImageInImageview(bitmap: Bitmap?) {
        imageUri?.add(bitmap)
        binding.noteContenteditll.imagerecyclerview.visibility = View.VISIBLE
        addImageRecyclerview?.notifyDataSetChanged()
    }




}
