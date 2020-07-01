package com.keepnote.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.keepnote.view.homescreen.HomeScreen
import com.keepnote.NoteListAdapter
import com.keepnote.R
import com.keepnote.databinding.ActivityPrivacyBinding
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.notesDB.NoteDatabase
import com.keepnote.notesDB.Notes
import com.keepnote.utils.Constants
import com.keepnote.view.trash.TrashAdapter
import com.keepnote.viewmodel.HomeViewmodel
import com.keepnote.viewmodel.HomeViewmodelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Privacy : AppCompatActivity(),NoteListAdapter.NotesListner {

    private  var lockpattern: String?=null
    private  var  lokedNotes: ArrayList<Notes>?=null
    private var notesize: Int=0
    private var selectedQuestionPosition: Int=0
    private lateinit var animshake: Animation
    lateinit var binding:ActivityPrivacyBinding
    private var patternFor=""
    private var showtoolbarView = false
    private var temppasword=""
    private var fromPage:String?=null
    private var noteId:Long?=null
    private  var noteDBAdapter: TrashAdapter?=null
    private var passwordReset = false
    private lateinit var viewmodel: HomeViewmodel
    private var securityQuestion = arrayOf("What is your father's name?","What is your mother's name?","" +
            "What is your first girl friend name?","What is your ID card numbers?","What is your first company name?",
    "What is your favorite actor?","What is your first pet name?")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if ((StoreSharedPrefData.INSTANCE.getPref("isDarktheme",false,this))as Boolean){
            setTheme(R.style.DarkTheme)
          showtoolbarView = true
        }
        else
            setTheme(R.style.LightTheme)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_privacy)
        binding.toolbarPrivacy.toolbarSearch.visibility = View.VISIBLE
        binding.toolbarPrivacy.toolbar.title=""
        binding.toolbarPrivacy.toolbartitle.text = resources.getString(R.string.privacy_txt)
        binding.toolbarPrivacy.toolbarSearch.visibility = View.GONE

        if (showtoolbarView)  binding.toolbarPrivacy.view.visibility = View.GONE
        setSupportActionBar(binding.toolbarPrivacy.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        animshake = AnimationUtils.loadAnimation(this,R.anim.shake)

        lockpattern = StoreSharedPrefData.INSTANCE.getPref("lockpattern",0,this).toString()
        if (lockpattern!=null){
            //init viewmodel
            val application = requireNotNull(this).application
            val dataSource = NoteDatabase.invoke(this).getNoteDao()
            val homeViewmodelFactory = HomeViewmodelFactory(dataSource,application)
            viewmodel = ViewModelProviders.of(this,homeViewmodelFactory).get(HomeViewmodel::class.java)
            binding.viewmodel = viewmodel
            binding.lifecycleOwner = this
            fromPage = intent.getStringExtra("from")
            noteId = intent.getLongExtra("noteid",0L)
            getLockedNotes()
        }



        binding.patternLockView.addPatternLockListener(object: PatternLockViewListener {
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                if (patternFor.isEmpty()){
                    temppasword = PatternLockUtils.patternToString(binding.patternLockView,pattern)
                    binding.privacyActionText.text = getString(R.string.privacy_action_txt_confirm)
                    binding.patternLockView.clearPattern()
                    patternFor="confirmpattern"
                }else if (patternFor=="confirmpattern"){
                    val confirmpattern = PatternLockUtils.patternToString(binding.patternLockView,pattern)
                    if (temppasword==confirmpattern){
                        binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT)
                        StoreSharedPrefData.INSTANCE.savePrefValue("securitypattern",confirmpattern,this@Privacy)
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            binding.toolbarPrivacy.toolbartitle.text=getString(R.string.privacy_title_setquestion)
                            binding.secquestionLl.visibility = View.VISIBLE
                            binding.patternLl.visibility = View.GONE
                        }

                    }else{
                        Constants.showToast("password not match",this@Privacy)
                        binding.privacyActionText.text =getString(R.string.privacy_action_txt_tryagain)
                        binding.privacyActionText.setTextColor(ContextCompat.getColor(this@Privacy,R.color.failurecolor))
                        binding.privacyActionText.startAnimation(animshake)
                        binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)

                    }
                }else getLockedNotes()

            }

            override fun onCleared() {

            }

            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

            }
        })

        val questionAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,securityQuestion)
        questionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.securitySpinner.adapter = questionAdapter
        binding.securitySpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedQuestionPosition = position

            }
        }

        binding.submitBtn.setOnClickListener {
            if (selectedQuestionPosition.toString().isNotEmpty()){
                    val selectedAnswer = binding.securityAnswer.text.toString()
                    if (selectedAnswer.isNotEmpty()){
                        if (passwordReset){
                            val selectedQuestions = StoreSharedPrefData.INSTANCE.getPref("securityquestion","this",this)
                            val selectedAnswers = StoreSharedPrefData.INSTANCE.getPref("securityanswer","this",this)
                            if (selectedQuestions==selectedQuestionPosition){
                                if (selectedAnswer==selectedAnswers){
                                    binding.securityAnswer.setText("")
                                    passwordReset = false
                                    StoreSharedPrefData.INSTANCE.savePrefValue("securityquestion","",this)
                                    StoreSharedPrefData.INSTANCE.savePrefValue("securityanswer","",this)
                                    StoreSharedPrefData.INSTANCE.savePrefValue("lockpattern",0,this)
                                    selectedQuestionPosition = 0
                                    binding.secquestionLl.visibility = View.GONE
                                    binding.toolbarPrivacy.toolbartitle.text = resources.getString(R.string.privacy_txt)
                                    getLockedNotes()
                                } else Constants.showToast("Your Answer Is Not Correct.",this)
                            } else Constants.showToast("Please Select Correct Question.",this)
                        }else{
                            binding.securityAnswer.setText("")
                            StoreSharedPrefData.INSTANCE.savePrefValue("securityquestion",selectedQuestionPosition,this)
                            StoreSharedPrefData.INSTANCE.savePrefValue("securityanswer",selectedAnswer,this)
                            StoreSharedPrefData.INSTANCE.savePrefValue("lockpattern",1,this)
                            binding.secquestionLl.visibility = View.GONE
                            binding.toolbarPrivacy.toolbartitle.text = resources.getString(R.string.privacy_txt)
                            if (fromPage=="home-lock" && fromPage!=null && noteId!=null){
                                viewmodel.updateLockbyId(noteId,1)
                                Constants.ReLoad = true
                            }
                            finish()
                            startActivity(Intent(this,Privacy::class.java))
                            Constants.showToast("Set successfully!",this)
                        }

                    }else{
                        Constants.showToast("Input cannot be empty",this)
                    }
                }else{
                    Constants.showToast("Please select question list",this)
                }
            }



    }



    private fun getLockedNotes() {
        lockpattern = StoreSharedPrefData.INSTANCE.getPref("lockpattern",0,this).toString()
        if (lockpattern == "1"){
            binding.patternLl.visibility = View.GONE
            binding.privacynotesLl.visibility= View.VISIBLE
            binding.patternLockViewTest.addPatternLockListener(object :PatternLockViewListener{
                val savedPattern = StoreSharedPrefData.INSTANCE.getPref("securitypattern","",this@Privacy)
                override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                    val confirmpattern = PatternLockUtils.patternToString(binding.patternLockView,pattern)
                    if (savedPattern==confirmpattern){
                        binding.passwordTestll.visibility = View.GONE
                        if (fromPage!=null && fromPage=="home-unlock" && noteId!=null){
                            viewmodel.updateLockbyId(noteId,0)
                            startActivity(Intent(this@Privacy,
                                HomeScreen::class.java))
                            finish()
                            return
                        }else getAllNoteDB()
                    }else{
                        binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG)
                        Constants.showToast("Pattern wrong",this@Privacy)
                    }
                }

                override fun onCleared() {

                }

                override fun onStarted() {

                }

                override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

                }
            })

            binding.privacyResetcodeTest.setOnClickListener {
                binding.secquestionLl.visibility = View.VISIBLE
                binding.passwordTestll.visibility = View.GONE
                passwordReset = true

            }
        }else{
            binding.patternLl.visibility = View.VISIBLE
            binding.privacynotesLl.visibility= View.GONE
        }
    }

    private fun getAllNoteDB() {
        viewmodel.getallNotes()
        viewmodel.allNotes.observe(this, Observer {notes->
            notesize = notes.size
            lokedNotes = ArrayList()
            for (i in 0 until notesize){
                if (notes[i].islocked==1)
                    lokedNotes!!.add(notes[i])
            }
            if (lokedNotes!!.isEmpty()){
                binding.errLayout.root.visibility  = View.VISIBLE
                binding.errLayout.errmsg.text = getString(R.string.privacy_no_notes_txt)
                // mbinding.adView.visibility = View.GONE
                noteDBAdapter =
                    TrashAdapter(lokedNotes!!, this)
                binding.trashRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()
                binding.adView.let { Constants.showBottomAds(this,it) }

            }else{
                noteDBAdapter =
                    TrashAdapter(lokedNotes!!, this)
                binding.trashRecycler.visibility = View.VISIBLE
                binding.trashRecycler.layoutManager = 4.getLayoutManager()
                binding.trashRecycler.adapter = noteDBAdapter
                noteDBAdapter?.notifyDataSetChanged()
                binding.adView.let { Constants.showBottomAds(this,it) }
            }



        })
    }

    private fun Int.getLayoutManager(): RecyclerView.LayoutManager{
        when(this){
            1->{
                return LinearLayoutManager(this@Privacy, LinearLayoutManager.VERTICAL,false)
            }

            2->{
                return GridLayoutManager(this@Privacy,3)
            }


            3->{
                return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }

            else ->{
                return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> {
                onBackPressed()

            }
        }
        return true
    }

    override fun takeActionForNotes(actionFor: String, noteId: Long, position: Int) {

    }

}
