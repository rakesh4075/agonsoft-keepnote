package com.keepnote

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.keepnote.utils.PassDataToFragListner
import com.keepnote.view.exportbackup.DriveUtil
import com.keepnote.view.exportbackup.ExportBackup
import com.keepnote.view.exportbackup.RemoteBackup
import com.keepnote.view.favourite.FavouriteFragment
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
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeScreen : AppCompatActivity(), NoteListAdapter.NotesListner,PassDataToFragListner {


    private var fragment: Fragment? = null
    private var clickedNavItem: Int?=null
    private var lastsyncTime: String?=null
    private lateinit var dialog: Dialog
    private  var noteDBAdapter: NoteListAdapter?=null
    private lateinit var toolbar: Toolbar
    private lateinit var mbinding:HomescreenBinding
    private lateinit var viewmodel: HomeViewmodel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var notesCount:TextView
    private lateinit var trasCount:TextView
    private lateinit var privacyCount:TextView
    private lateinit var favouritecount:TextView
    private var remoteBackup: RemoteBackup? = null
    private var showtoolbarView = false
    private var isBackup = true
    private var fromPage:String? = ""
    private var mInterstitialAd: InterstitialAd?=null
    private var passDataToFragListners:PassDataToFragListner?=null


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
        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean){
            mbinding.navMain.getHeaderView(0).drawer_header.background = getDrawable(R.drawable.navbgdark)
        }else{
            mbinding.navMain.getHeaderView(0).drawer_header.background = getDrawable(R.drawable.navbglight)
        }
        toolbar = mbinding.mainContent.toolbarLl.toolbar
        fromPage = intent.getStringExtra("frompage")
        if (fromPage!=null){
            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd?.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            mInterstitialAd?.loadAd(AdRequest.Builder().build())
            mInterstitialAd?.adListener = object: AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.d("@@@@@@","onAdLoaded")
                    if (fromPage!=null){

                        when(fromPage){
                            "editnotesavenote"->{
                                if (mInterstitialAd!!.isLoaded) {
                                    Log.d("@@@@@","loaded")
                                    mInterstitialAd!!  .show()
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
        }

        mbinding.drawer.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                getAllNoteDBCount()
                if (clickedNavItem!=null){
                    when(clickedNavItem){
                        R.id.notes->{  initFragment(1) }
                        R.id.trash ->{  initFragment(2) }
                        R.id.myfav ->{ initFragment(3) }
                        R.id.menu_privacy->{startActivity(Intent(this@HomeScreen,Privacy::class.java))}
                        R.id.menu_settings->{ startActivity(Intent(this@HomeScreen,Settings::class.java))}
                    }
                }
            }

            override fun onDrawerOpened(drawerView: View) {

            }
        })

        passDataToFragListners = this
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
                    clickedNavItem = R.id.menu_privacy

                }

                R.id.backup->{
                    startActivity(Intent(this,ExportBackup::class.java))
                    mbinding.drawer.closeDrawer(GravityCompat.START)
                }

                R.id.notes->{
                    clickedNavItem = R.id.notes
                }
                R.id.trash->{
                    clickedNavItem = R.id.trash

                }


                R.id.myfav->{
                    clickedNavItem = R.id.myfav

                }

                R.id.menu_settings->{
                    clickedNavItem = R.id.menu_settings


                }

                R.id.menu_feedback->{
                    val emailList = arrayOf("raksexplore@gmail.com")
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:") // only email apps should handle this
                        putExtra(Intent.EXTRA_EMAIL,emailList)
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent) }
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
                                mbinding.navMain.getHeaderView(0).mem_name.text = getString(R.string.app_name)
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


            mbinding.drawer.closeDrawer(GravityCompat.START)




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
        builder.setPositiveButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Continue") { _, _ ->
            try {
                isBackup = true
                remoteBackup?.connectToDrive(isBackup)
            }catch (e:java.lang.Exception){
                Log.d("@@@@",e.printStackTrace().toString())
            }

        }
        builder.show()
    }


    @SuppressLint("SetTextI18n")
    private fun showSyncDialog(progress:Int, show:Boolean){
        dialog.setContentView(R.layout.sync_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        val syncProgress = dialog.findViewById<ProgressBar>(R.id.syncprogress)
        val syncText = dialog.findViewById<TextView>(R.id.progress_txt)
        syncProgress.progress = progress
        syncText.text = "$progress %"
        if (show) {
            dialog.show()
        } else {
            dialog.dismiss()
        }

    }





    fun initFragment(For:Int){
        val fragmentClass: Class<*>?
        when(For){
            1-> {
                mbinding.mainContent.toolbarLl.toolbartitle.text = "Notes"
                fragmentClass = Homefragment::class.java
            }
            2-> {
                mbinding.mainContent.toolbarLl.toolbartitle.text = "Trash"
                fragmentClass = TrashFragment::class.java
            }
            3-> {
                mbinding.mainContent.toolbarLl.toolbartitle.text = "Favourite"
                fragmentClass = FavouriteFragment::class.java
            }
            else -> fragmentClass = Homefragment::class.java
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e:Exception) {
            e.printStackTrace()
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        fragment?.let { fragmentManager.beginTransaction().replace(R.id.fragcontainer, it)
            .addToBackStack(null)
            .commit() }
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
        getAllNoteDBCount()



        //noteAdapter = NotesAdapterFirestore(allNotes)


        mbinding.mainContent.fab.setOnClickListener {
            val addNoteIntent = Intent(this@HomeScreen, EditNote::class.java)
            addNoteIntent.putExtra("from",1)
            startActivity(addNoteIntent)
        }


        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        val show = StoreSharedPrefData.INSTANCE.getPref("viewas",0,this) as Int
        if (show==1){
            mbinding.mainContent.adView.visibility = View.VISIBLE
            mbinding.mainContent.adView.loadAd(adRequest)
        }

    }

    fun getAllNoteDBCount() {
        try {
            notesCount = mbinding.navMain.menu.findItem(R.id.notes).actionView as TextView
            trasCount = mbinding.navMain.menu.findItem(R.id.trash).actionView as TextView
            privacyCount = mbinding.navMain.menu.findItem(R.id.menu_privacy).actionView as TextView
            favouritecount = mbinding.navMain.menu.findItem(R.id.myfav).actionView as TextView

            //Gravity property aligns the text
            notesCount.gravity = Gravity.CENTER_VERTICAL
            notesCount.setTypeface(null, Typeface.BOLD)
            notesCount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent))

            trasCount.gravity = Gravity.CENTER_VERTICAL
            trasCount.setTypeface(null, Typeface.BOLD)
            trasCount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent))

            privacyCount.gravity = Gravity.CENTER_VERTICAL
            privacyCount.setTypeface(null, Typeface.BOLD)
            privacyCount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent))

            favouritecount.gravity = Gravity.CENTER_VERTICAL
            favouritecount.setTypeface(null, Typeface.BOLD)
            favouritecount.setTextColor(ContextCompat.getColor(this,R.color.lightcoloraccent))

            viewmodel.getallNotes()
            viewmodel.allNotes.observe(this, Observer {notes->
                if (notes.isEmpty()){
                    notesCount.text =""
                    trasCount.text =""
                    privacyCount.text =""
                   // favouritecount.text =""
                }else{
                    val nonDeletedNotes = ArrayList<Notes>()
                    val lockedNotes = ArrayList<Notes>()
                    val favouriteNotes = ArrayList<Notes>()
                    val trashNotes = ArrayList<Notes>()

                    for (i in notes.indices){
                        if (notes[i].isDeleted==0 && notes[i].islocked==0 && notes[i].isFavourite==0){
                            nonDeletedNotes.add(notes[i])
                        }

                        if (notes[i].isDeleted==1)
                            trashNotes.add(notes[i])
                        if (notes[i].islocked==1)
                            lockedNotes.add(notes[i])
                        if (notes[i].isFavourite==1)
                            favouriteNotes.add(notes[i])

                    }

                    val trashcounts = trashNotes.size
                    val notesCounts = nonDeletedNotes.size
                    val privacyCounts = lockedNotes.size
                    val favouriteCounts = favouriteNotes.size



                    if (notesCounts==0) notesCount.text = "" else notesCount.text =notesCounts.toString()
                    if (trashcounts==0) trasCount.text = "" else trasCount.text =trashcounts.toString()
                    if (privacyCounts==0) privacyCount.text="" else privacyCount.text = privacyCounts.toString()
                   if (favouriteCounts==0) favouritecount.text="" else favouritecount.text = favouriteCounts.toString()
                }

            })
        }catch (e:java.lang.Exception){
            Log.d("@@@@@",e.message.toString())
        }


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.view_list ->{
                StoreSharedPrefData.INSTANCE.savePrefValue("viewas",1,this)
                passDataToFragListners?.passData("view_list")


            }
            R.id.view_grid ->{
                StoreSharedPrefData.INSTANCE.savePrefValue("viewas",2,this)
                passDataToFragListners?.passData("view_grid")


            }


        }
        return super.onOptionsItemSelected(item)
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
                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show()
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

    override fun passData(value: String) {
        when(fragment?.javaClass){
            Homefragment::class.java->{
                val sf = supportFragmentManager.findFragmentById(R.id.fragcontainer) as Homefragment
                sf.getDate(value)
            }
            TrashFragment::class.java->{
                val sf = supportFragmentManager.findFragmentById(R.id.fragcontainer) as TrashFragment
                sf.getDate(value)
            }
        }

    }


}


