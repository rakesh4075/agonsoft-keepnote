
package com.keepnote.utils

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.keepnote.KeepNoteApplication
import com.keepnote.R
import com.keepnote.model.preferences.StoreSharedPrefData
import com.keepnote.tedpermission.PermissionListener
import com.keepnote.tedpermission.TedPermission
import java.io.File
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class Constants {
    companion object{
        private var confirmPattern: String?=""
        private var temppassword: String?=""
        var testPattern = 0
        var patternLockNumber:String? = ""
        var ReLoad:Boolean= false
        fun showToast(msg:String,context: Context){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }
        var adUnitId = "ca-app-pub-3940256099942544~3347511713"

        fun colorLists():ArrayList<String>{
            val lists = ArrayList<String>()
            lists.add("#FFE66E")
            lists.add("#a0ef9a")
            lists.add("#ffaedf")
            lists.add("#d7afff")
            lists.add("#000000")
            lists.add("#e0e0e0")
            lists.add("#ff8080")
            lists.add("#8C9EFF")
            lists.add("#96D162")
            lists.add("#ffffff")
            lists.add("#EA80FC")
            lists.add("#66ffcc")
            lists.add("#9edfff")
            lists.add("#ffb380")
            lists.add("#bfbfbf")
            return lists
        }

         fun getRandomColor(): Int {
            val colorCode: MutableList<Int> = ArrayList()
            colorCode.add(R.color.edit_yellow)
            colorCode.add(R.color.edit_lightgreen)
            colorCode.add(R.color.edit_pink)
            colorCode.add(R.color.edit_pinklove)
            colorCode.add(R.color.edit_darkbrown)
            colorCode.add(R.color.edit_lightgrey)
            colorCode.add(R.color.edit_red)
            colorCode.add(R.color.edit_darkblue)
            colorCode.add(R.color.edit_darkgreen)
            colorCode.add(R.color.edit_darkpink)
             colorCode.add(R.color.edit_paalgreen)
             colorCode.add(R.color.edit_skyblue)
             colorCode.add(R.color.edit_brown)
             colorCode.add(R.color.edit_darkgrey)
            val randomColor = Random()
            val number: Int = randomColor.nextInt(colorCode.size)
            return colorCode[number]
        }

        fun verifyPermission(context: Context){
            if (Build.VERSION.SDK_INT>=23){
                TedPermission.with(context)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            showToast("Permission Granted",context)

                        }

                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                            showToast("Permission Denied",context)
                        }
                    })
                    .setRationaleTitle(R.string.rationale_title)
                    .setRationaleMessage(R.string.rationale_message)
                    .setDeniedTitle("Permission denied")
                    .setDeniedMessage(
                        "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setGotoSettingButtonText("Settings")
                    .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .setGotoSettingButton(true)
                    .check()
            }

        }

        fun getSortOrder(context: Context):Int{
            val value = StoreSharedPrefData.INSTANCE.getPref("notesortorder",1,context) as Int
            return value
        }
        fun setupProgressDialog(context: Context): ProgressDialog {
            val mProgress = ProgressDialog(context, R.style.AppProgressDialogTheme)
            mProgress.setCancelable(false)
            mProgress.setTitle(context.getString(R.string.please_wait))
            mProgress.setMessage(context.getString(R.string.loading_dots))
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            mProgress.setCanceledOnTouchOutside(false)
            return mProgress
        }

         fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

        // create folder if it not exist
         fun createFolder(context: Context):File {
            val sd = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString())
            if (!sd.exists()) {
                sd.mkdir()
                Log.d("@@@@","folder created")
            } else {
                Log.d("@@@@","folder exists")
            }
            return sd
        }


    }


}