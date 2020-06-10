package com.keepnote

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import com.google.firebase.firestore.FirebaseFirestore
import com.keepnote.databinding.HomescreenBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.view.UpgradePayment
import com.keepnote.view.exportbackup.DriveUtil
import com.keepnote.view.exportbackup.ExportBackup
import com.keepnote.view.exportbackup.RemoteBackup
import com.keepnote.view.homescreen.Homefragment
import com.keepnote.view.settings.Privacy
import com.keepnote.view.settings.Settings
import com.keepnote.view.trash.TrashFragment
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import com.raks.roomdatabase.NoteDatabase
import kotlinx.android.synthetic.main.homescreen.view.*
import kotlinx.android.synthetic.main.nav_bottom.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.*
import java.io.File
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeScreen : AppCompatActivity(), NoteListAdapter.NotesListner {


    private var lastsyncTime: String?=null
    private lateinit var dialog: Dialog
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
    private var showtoolbarView = false
    private var isBackup = true
    private var fromPage:String? = ""
    private lateinit var mInterstitialAd: InterstitialAd


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

        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean){
            setTheme(R.style.DarkTheme)
            showtoolbarView = true
        }
        else
            setTheme(R.style.LightTheme)


        mbinding = DataBindingUtil.setContentView(this, R.layout.homescreen)
        toolbar = mbinding.mainContent.toolbarLl.toolbar
        fromPage = intent.getStringExtra("frompage")
        if (fromPage!=null)
        Log.d("@@@@@@2",fromPage)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("@@@@@@","onAdLoaded")
                if (fromPage!=null){

                    when(fromPage){
                        "editnotesavenote"->{
                            if (mInterstitialAd.isLoaded) {
                                Log.d("@@@@@","loaded")
                                mInterstitialAd.show()
                            } else {
                                Log.d("@@@@", "The interstitial wasn't loaded yet.")
                            }
                        }
                    }
                }
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.d("@@@@@@","onAdFailedToLoad")
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.d("@@@@@@","onAdOpened")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("@@@@@@","onAdClicked")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d("@@@@@@","onAdLeftApplication")
            }

            override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Log.d("@@@@@@","onAdClosed")
            }

        }

        initLayout()


        dialog= Dialog(this)





        remoteBackup = RemoteBackup(this,object :DriveUtil{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun showProgress(progress: Int) {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("@@@@progress",progress.toString())
                    if (progress==100)
                    showSyncDialog(progress,false)
                    else{
                        showSyncDialog(progress,true)

                    }
                    syncDateTime()

                }

                }

        })
        if(StoreSharedPrefData.INSTANCE.getPref("appstart",false,this) as Boolean){
            StoreSharedPrefData.INSTANCE.savePrefValue("appstart",false,this)
            if (StoreSharedPrefData.INSTANCE.getPref("synconlaunch",false,this) as Boolean){
                startSyncNote()
            }
        }
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


                R.id.myfav->{
                    startActivity(Intent(this,UpgradePayment::class.java))

                }

                R.id.menu_settings->{
                    startActivity(Intent(this,Settings::class.java))

                }
                R.id.logout->{
                    val menu = mbinding.navMain.menu
                    val loginMenu = menu.findItem(R.id.logout)
                    val account = GoogleSignIn.getLastSignedInAccount(this)
                    if (account==null){
                        remoteBackup?.connectToDrive(true)
                    }else{
                        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).revokeAccess()
                            .addOnCompleteListener {
                                loginMenu.title = "Login"
                                mbinding.navMain.getHeaderView(0).mem_name.textSize = 24f
                                mbinding.navMain.getHeaderView(0).mem_name.text = "KeepNote"
                                mbinding.navMain.getHeaderView(0).mem_email.visibility= View.VISIBLE
                                mbinding.navMain.getHeaderView(0).mem_email.text=""
                                mbinding.navMain.getHeaderView(0).mem_image.visibility= View.VISIBLE
                                StoreSharedPrefData.INSTANCE.savePrefValue("memPhoto","",this)
                                Constants.showToast("Logged out",this)
                            }
                    }

                }
                R.id.syncnote->{
                    startSyncNote()
                }

            }

            CoroutineScope(Dispatchers.Main).launch{
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

    private fun startSyncNote() {
        val showAlert = StoreSharedPrefData.INSTANCE.getPref("drivealert",false,this) as Boolean

        if (!showAlert) showSyncAlert() else{
            isBackup = true
            remoteBackup?.connectToDrive(isBackup)
        }
    }

    private fun syncDateTime() {
        mbinding.navMain.nav_bottomll.visibility = View.VISIBLE
        val currentDateTime= DateFormat.getDateTimeInstance().format(Date())
        val syncDate = "Last sync: $currentDateTime"
        StoreSharedPrefData.INSTANCE.savePrefValue("lastsynctime",syncDate,this@HomeScreen)
        mbinding.navMain.nav_bottom.last_sync.text = syncDate
    }


    private fun showSyncAlert() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
        builder.setTitle("Synchronization").setIcon(null)
            .setMessage("Online synchronization will allow you to access notes from multiple devices.\nAll your notes wiil be stored online on Google Drive.")
        builder.setPositiveButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Continue") { dialog, which ->
            try {
                isBackup = true
                remoteBackup?.connectToDrive(isBackup)
            }catch (e:java.lang.Exception){
                Log.d("@@@@",e.printStackTrace().toString())
            }

        }
        builder.show()
    }


    private fun showSyncDialog(progress:Int,show:Boolean){
        dialog.setContentView(R.layout.sync_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        val syncProgress = dialog.findViewById<ProgressBar>(R.id.syncprogress)
        val syncText = dialog.findViewById<TextView>(R.id.progress_txt)
        syncProgress.progress = progress
        syncText.text = "${progress} %"
        if (show) {
            dialog.show()
        } else {
            dialog.dismiss()
        }

    }





     fun initFragment(For:Int){
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
        toolbar.setNavigationIcon(R.drawable.ic_nav_menu)
        toolbar.overflowIcon = getDrawable(R.drawable.ic_menu_overflow)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this,mbinding.drawer,toolbar, R.string.opens, R.string.closes)
        mbinding.drawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setToolbarNavigationClickListener {
            mbinding.drawer.openDrawer(GravityCompat.START)
        }
        toggle.syncState()
        if (showtoolbarView)  mbinding.mainContent.toolbarLl.vw1.visibility = View.GONE

        notesCount = MenuItemCompat.getActionView(mbinding.navMain.menu.findItem(R.id.notes)) as TextView
        trasCount = MenuItemCompat.getActionView(mbinding.navMain.menu.findItem(R.id.trash)) as TextView
        privacyCount = MenuItemCompat.getActionView(mbinding.navMain.menu.findItem(R.id.menu_privacy)) as TextView
        lastsyncTime = StoreSharedPrefData.INSTANCE.getPref("lastsynctime","",this) as String

        if (lastsyncTime!=null && lastsyncTime!=""){
            mbinding.navMain.nav_bottomll.visibility = View.VISIBLE
            mbinding.navMain.nav_bottom.last_sync.text = StoreSharedPrefData.INSTANCE.getPref("lastsynctime","",this@HomeScreen) as String
        }

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
                val trashcount = notes.size - nonDeletedNotes.size
                if (trashcount==0) trasCount.text = "" else trasCount.text =trashcount.toString()
                if (lockedNotes.size==0) privacyCount.text="" else privacyCount.text = lockedNotes.size.toString()
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
                    StoreSharedPrefData.INSTANCE.savePrefValue("drivefirstsync",true, this)
                    val account = GoogleSignIn.getLastSignedInAccount(this)
                    val credential = GoogleAccountCredential.usingOAuth2(this, mutableListOf(DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_APPDATA))
                    credential.selectedAccount = account?.account
                    remoteBackup?.connectToDrive(isBackup)
                    if (data!=null){
                        handleSignInIntent(data)
                    }
                }
            }
            200->{
                if (resultCode == Activity.RESULT_OK){
                    val account = GoogleSignIn.getLastSignedInAccount(this)
                    val credential = GoogleAccountCredential.usingOAuth2(this, mutableListOf(DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_APPDATA))
                    credential.selectedAccount = account?.account
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
                val memName= googlesigninaccount.displayName
                val memEmail= googlesigninaccount.email
                val memPhoto = googlesigninaccount.photoUrl.toString()

                StoreSharedPrefData.INSTANCE.savePrefValue("memName",memName,this)
                StoreSharedPrefData.INSTANCE.savePrefValue("memEmail",memEmail,this)
                StoreSharedPrefData.INSTANCE.savePrefValue("memPhoto",memPhoto,this)

                mbinding.navMain.getHeaderView(0).mem_name.text = memName
                mbinding.navMain.getHeaderView(0).mem_email.text = memEmail
                Glide.with(this)
                    .load(memPhoto)
                    .placeholder(R.drawable.noteavatar)
                    .error(R.drawable.noteavatar)
                    .into(mbinding.navMain.getHeaderView(0).mem_image)
            }
            .addOnCanceledListener {
                Log.d("@@@@","cancelled")
            }
            .addOnFailureListener {e->
                Log.d("@@@@exception",e.message)
            }
    }


    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val menu = mbinding.navMain.menu
        val loginMenu = menu.findItem(R.id.logout)
        if (account!=null) {
            loginMenu.title="Logout"
            mbinding.navMain.getHeaderView(0).mem_email.visibility= View.VISIBLE
            mbinding.navMain.getHeaderView(0).mem_email.setPadding(0,0,0,10)
            mbinding.navMain.getHeaderView(0).mem_name.text = StoreSharedPrefData.INSTANCE.getPref("memName","",this) as String
            mbinding.navMain.getHeaderView(0).mem_email.text = StoreSharedPrefData.INSTANCE.getPref("memEmail","",this) as String
            Glide.with(this)
                .load(StoreSharedPrefData.INSTANCE.getPref("memPhoto","",this) as String)
                .placeholder(R.drawable.noteavatar)
                .error(R.drawable.noteavatar)
                .into(mbinding.navMain.getHeaderView(0).mem_image)
        }else{
            loginMenu.title="Login"
            mbinding.navMain.getHeaderView(0).mem_name.textSize = 24f
            mbinding.navMain.getHeaderView(0).mem_name.text = "KeepNote"
            mbinding.navMain.getHeaderView(0).mem_email.visibility= View.VISIBLE
            mbinding.navMain.getHeaderView(0).mem_email.text=""
            mbinding.navMain.getHeaderView(0).mem_image.visibility= View.VISIBLE

        }

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


