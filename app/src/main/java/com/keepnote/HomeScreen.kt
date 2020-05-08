package com.keepnote

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import com.google.firebase.firestore.FirebaseFirestore
import com.keepnote.databinding.HomescreenBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.view.exportbackup.ExportBackup
import com.keepnote.view.exportbackup.RemoteBackup
import com.keepnote.view.homescreen.Homefragment
import com.keepnote.view.settings.Privacy
import com.keepnote.view.trash.TrashFragment
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import com.raks.roomdatabase.NoteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class HomeScreen : AppCompatActivity(), NoteListAdapter.NotesListner {


    private var mmenu: Menu?=null
    private  var noteDBAdapter: NoteListAdapter?=null
    private lateinit var toolbar: Toolbar
    private lateinit var mbinding:HomescreenBinding
    private lateinit var viewmodel: HomeViewmodel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var stagLayoutManager: StaggeredGridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var noteListAdapter: NoteListAdapter
    private lateinit var fstore:FirebaseFirestore
    private lateinit var notesCount:TextView
    private lateinit var trasCount:TextView
    private lateinit var privacyCount:TextView
    private var remoteBackup: RemoteBackup? = null
    private var isBackup = true


    companion object{
        @JvmStatic
        val GOOGLE_SIGN_IN: Int = 400
        @JvmStatic
        val REQUEST_CODE_CREATION = 401
        @JvmStatic
        val REQUEST_CODE_OPENING = 402
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.homescreen)
        toolbar = mbinding.mainContent.toolbarLl.toolbar

        initLayout()

        remoteBackup = RemoteBackup(this)
        if (StoreSharedPrefData.INSTANCE.getPref("firsttimepermmision",true,this) as Boolean){
            Constants.verifyPermission(this)
            StoreSharedPrefData.INSTANCE.savePrefValue("firsttimepermmision",false,this)
        }

//        credentials = GoogleAccountCredential.usingOAuth2(this,Collections.singleton(DriveScopes.DRIVE_FILE))
//        googleDriveservices = Drive.Builder(
//            AndroidHttp.newCompatibleTransport(),
//            GsonFactory(),credentials)
//            .setApplicationName("KeepNote")
//            .build()


        mbinding.navMain.setNavigationItemSelectedListener { item ->

            when(item.itemId){
                R.id.menu_privacy ->{
                   startActivity(Intent(this,Privacy::class.java))
                    mbinding.drawer.closeDrawer(GravityCompat.START)
                }

                R.id.backup->{
                    startActivity(Intent(this,ExportBackup::class.java))
                    mbinding.drawer.closeDrawer(GravityCompat.START)
                }

                R.id.notes->{
                    initFragment(1)
                }
                R.id.trash->{
                    initFragment(2)

                }

                R.id.logout->{
                    isBackup = false
                    remoteBackup?.connectToDrive(isBackup)

                }
                R.id.syncnote->{
                    isBackup = true
                    remoteBackup?.connectToDrive(isBackup)
                }

            }

            GlobalScope.launch {
                delay(600)
                mbinding.drawer.closeDrawer(GravityCompat.START)
            }



            true
        }

        mbinding.mainContent.swiperefresh.setOnRefreshListener {
            GlobalScope.launch {
                delay(1000L)
                runOnUiThread {
                    noteDBAdapter?.notifyDataSetChanged()
                    mbinding.mainContent.swiperefresh.isRefreshing =false
                }

            }

        }



    }


    private fun converthtmlTopdf() {
        val folderSD: String? =
            Environment.getExternalStorageDirectory().toString() + "/KeepNotePdf"
        val sd = File(folderSD)

        if (!sd.exists()) {
            sd.mkdir()
            Log.d("@@@@@","folder created")
        } else {
            val html = "<body>\n" +
                    "\n" +
                    "<h2>Using a Full URL File Path</h2>\n" +
                    "<img src=\"https://www.w3schools.com/images/picture.jpg\" alt=\"Mountain\" style=\"width:300px\">\n" +
                    "\n" +
                    "</body>"
            val converter = Html2Pdf.Companion.Builder()
                .context(this)
                .html(html)
                .file(File(sd,"raks.pdf"))
                .build()

            converter.convertToPdf(object:
                Html2Pdf.OnCompleteConversion {
                override fun onSuccess(msg: String) {
                    Constants.showToast(msg,this@HomeScreen)
                }

                override fun onFailed(msg: String) {
                    Constants.showToast(msg,this@HomeScreen)
                }
            })
        }





    }



    private fun initFragment(For:Int){
        var fragment: Fragment? = null
        var fragmentClass: Class<*>?=null
        when(For){
            1-> {
                mbinding.mainContent.toolbarLl.toolbartitle.text = "Notes"
                fragmentClass = Homefragment::class.java
            }
            2-> {
                mbinding.mainContent.toolbarLl.toolbartitle.text = "Trash"
                fragmentClass = TrashFragment::class.java
            }
            else -> fragmentClass = Homefragment::class.java
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e:Exception) {
            e.printStackTrace();
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        fragment?.let { fragmentManager.beginTransaction().replace(R.id.fragcontainer, it)
            .addToBackStack(null)
            .commit() }
    }

    override fun onResume() {
        super.onResume()

    }


    private fun viewPdf(s: String, s1: String) {

    }


    private fun initLayout() {
        mbinding.mainContent.swiperefresh.setColorSchemeColors(ContextCompat.getColor(this@HomeScreen, R.color.accestcolor))

        toolbar.title=""
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this,mbinding.drawer,toolbar, R.string.opens, R.string.closes)
        mbinding.drawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        notesCount = MenuItemCompat.getActionView(mbinding.navMain.menu.findItem(R.id.notes)) as TextView
        trasCount = MenuItemCompat.getActionView(mbinding.navMain.menu.findItem(R.id.trash)) as TextView
        privacyCount = MenuItemCompat.getActionView(mbinding.navMain.menu.findItem(R.id.menu_privacy)) as TextView


//        fstore = FirebaseFirestore.getInstance()
//        val allNotesquery = fstore.collection("notes").orderBy("title",Query.Direction.DESCENDING)
//        val allNotes = FirestoreRecyclerOptions.Builder<Note>()
//            .setQuery(allNotesquery, Note::class.java)
//            .build()

        initFragment(1)
        //init viewmodel
        val application = requireNotNull(this).application
        val dataSource = NoteDatabase.invoke(this).getNoteDao()
        val homeViewmodelFactory = HomeViewmodelFactory(dataSource,application)
        viewmodel = ViewModelProviders.of(this,homeViewmodelFactory).get(HomeViewmodel::class.java)
        mbinding.viewmodel = viewmodel
        mbinding.lifecycleOwner = this
        getAllNoteDB()
        //noteAdapter = NotesAdapterFirestore(allNotes)


        mbinding.mainContent.fab.setOnClickListener {
            val addNoteIntent = Intent(this@HomeScreen, EditNote::class.java)
            addNoteIntent.putExtra("from",1)
            startActivity(addNoteIntent)
        }


        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        mbinding.mainContent.adView.loadAd(adRequest)
    }

    private fun getAllNoteDB() {
        //Gravity property aligns the text
        notesCount.gravity = Gravity.CENTER_VERTICAL;
        notesCount.setTypeface(null, Typeface.BOLD);
        notesCount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent));

        trasCount.gravity = Gravity.CENTER_VERTICAL;
        trasCount.setTypeface(null, Typeface.BOLD);
        trasCount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent));

        privacyCount.gravity = Gravity.CENTER_VERTICAL;
        privacyCount.setTypeface(null, Typeface.BOLD);
        privacyCount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent));

        viewmodel.getallNotes()
        viewmodel.allNotes.observe(this, Observer {notes->
            if (notes.isEmpty()){
                notesCount.text =""
            }else{
                val nonDeletedNotes = ArrayList<Notes>()
                val lockedNotes = ArrayList<Notes>()
                for (i in notes.indices){
                    if (notes[i].isDeleted==0)
                        nonDeletedNotes.add(notes[i])
                    if (notes[i].islocked==1)
                        lockedNotes.add(notes[i])
                }
                notesCount.text = nonDeletedNotes.size.toString()
                trasCount.text = ((notes.size - nonDeletedNotes.size).toString())
                privacyCount.text = lockedNotes.size.toString()
            }

        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        mmenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.view_list ->{
//                mbinding.mainContent.notelistRecycler.layoutManager = getLayoutManager(1)
//                noteDBAdapter?.notifyDataSetChanged()
               viewmodel.passData("view_list")

            }
            R.id.view_grid ->{
//                mbinding.mainContent.notelistRecycler.layoutManager = getLayoutManager(2)
//                noteDBAdapter?.notifyDataSetChanged()
                viewmodel.passData("view_grid")

            }


        }
        return super.onOptionsItemSelected(item)
    }










    private fun getLayoutManager(i:Int):RecyclerView.LayoutManager{
        when(i){
            1->{
                linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                return linearLayoutManager
            }

            2->{
                gridLayoutManager = GridLayoutManager(this,3)
                return gridLayoutManager
            }


            3->{
                stagLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                return stagLayoutManager
            }

            else ->{
                stagLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                return stagLayoutManager
            }


        }
    }





    override fun onBackPressed() {
        if (mbinding.drawer.isDrawerOpen(GravityCompat.START)){
            mbinding.drawer.closeDrawer(GravityCompat.START)
        }else{
          finishAffinity()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            GOOGLE_SIGN_IN ->{
                if (resultCode == Activity.RESULT_OK){
                    remoteBackup?.connectToDrive(isBackup)
                    if (data!=null){
                        handleSignInIntent(data)
                    }
                }
            }
            REQUEST_CODE_CREATION->{
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show();
                }
            }
            REQUEST_CODE_OPENING->{

            }
        }
    }

    private fun handleSignInIntent(data: Intent?) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnSuccessListener { googlesigninaccount->
                val loginMenu = mbinding.navMain.menu.findItem(R.id.logout)
                loginMenu.title="Logout"

               // credentials?.selectedAccount = googlesigninaccount.account
                val name = googlesigninaccount.displayName
                Log.d("@@@@@@2",name)

                val credential = GoogleAccountCredential.usingOAuth2(this,Collections.singleton(DriveScopes.DRIVE_FILE))
                credential.selectedAccount = googlesigninaccount.account


            }
    }


    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val menu = mbinding.navMain.menu
        val loginMenu = menu.findItem(R.id.logout)
        if (account!=null) {
            loginMenu.title="Logout"
        }else
            loginMenu.title="Login"
    }


    override fun takeActionForNotes(actionFor: String, noteId: Long, position: Int) {
        when(actionFor){
            "deletenotebyid"->{
                try {
                    viewmodel.updateDeleteById(noteId,1)
                }catch (e:java.lang.Exception){

                }

            }
        }

    }


}


