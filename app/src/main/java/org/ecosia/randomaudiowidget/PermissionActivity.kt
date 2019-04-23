package org.ecosia.randomaudiowidget

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.os.Bundle

// Created by martinsmelkis on 03/04/2019.
class PermissionActivity : Activity() {

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var permissionCheck = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionCheck != 0) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

}
