package com.example.r34university

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.r34university.databinding.ActivityMainBinding
import kotlin.concurrent.thread

@RequiresApi(Build.VERSION_CODES.R)
class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    private val permissionsDetails = arrayOf(
        mapOf(
            "name" to "write storage",
            "description" to "download images to your phone")
    )

    private var checkedPermissions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ConfigRepo.perfs = getSharedPreferences(cfgFileName, Context.MODE_PRIVATE)

        thread {
            checkPermissions()

            while (checkedPermissions < permissions.size) {
                Thread.sleep(500)
            }
            changeActivity()
        }
    }

    private fun changeActivity() {
        val currActivity = this@MainActivity
        val i = Intent(currActivity, SearchActivity::class.java)
        startActivity(i)
        ActivityCompat.finishAffinity(currActivity)
    }

    private fun checkPermissions() {
        permissions.forEachIndexed() { i, elem ->
            val res = ContextCompat.checkSelfPermission(applicationContext, elem)
            if (res != PackageManager.PERMISSION_GRANTED) {
                askPermission(elem, i)
            } else {
                checkedPermissions += 1
            }
        }
    }

    private fun askPermission(perm: String, code: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(perm), code)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)
            return alertAboutPermission(requestCode)

        checkedPermissions += 1
    }

    private fun alertAboutPermission(permID: Int) {
        val perm = permissionsDetails[permID]

        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setMessage("If you won't allow ${perm["name"]} permission, " +
                "then we won't be able to ${perm["description"]}")
            .setCancelable(false)
            .setPositiveButton("Allow", DialogInterface.OnClickListener {
                _, _ -> askPermission(this.permissions[permID], permID)
            })
            .setNegativeButton("Okay", DialogInterface.OnClickListener{
                dialog, _ -> dialog.cancel()
                checkedPermissions += 1
            })

        val alert = alertBuilder.create()
        alert.setTitle("Allow ${perm["name"]}")
        alert.show()
    }
}