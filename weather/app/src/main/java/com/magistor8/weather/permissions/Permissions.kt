package com.magistor8.weather.permissions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
const val CALL = Manifest.permission.CALL_PHONE
const val CONTACTS = Manifest.permission.READ_CONTACTS

class Permissions(private val activity: Activity, private val fragment: Fragment) {

    private lateinit var callBackFun: () -> Unit

    // Проверяем, на разрешение
    fun checkPermission(permissionType: String, func : () -> Unit) {
        callBackFun = func
        fragment.context?.let {
            when {
                ContextCompat.checkSelfPermission(it, permissionType) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    //Доступ есть
                    callBackFun()
                }
                //Опционально: если нужно пояснение перед запросом разрешений
                shouldShowRequestPermissionRationale(activity, permissionType) -> {
                    alertDialog(it, permissionType)
                }
                else -> {
                    //Запрашиваем разрешение
                    when(permissionType) {
                        LOCATION -> regResLocation.launch(LOCATION)
                        CONTACTS -> regResContacts.launch(CONTACTS)
                        CALL -> regResCall.launch(CALL)
                        else -> Unit
                    }
                }
            }
        }
    }

    fun alertDialog(it: Context, permissionType: String) {
        with(AlertDialog.Builder(it)) {
            when (permissionType) {
                LOCATION -> {
                    this.setTitle("Доступ к геолокации")
                        .setMessage("Для работы приложения необходим доступ к вашему местоположению")
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            regResLocation.launch(LOCATION)
                        }
                        .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                CONTACTS -> {
                    this.setTitle("Доступ к контактам")
                        .setMessage("Для работы приложения необходим доступ к контактам")
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            regResContacts.launch(CONTACTS)
                        }
                        .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                CALL -> {
                    this.setTitle("Доступ к совершению звонков")
                        .setMessage("Для работы приложения необходим доступ к совершению звонков")
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            regResCall.launch(CALL)
                        }
                        .setNegativeButton("Не надо") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }
    }

    private val regResLocation = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            //callBackFun
        } else {
            fragment.context?.let {
                alertDialog(it, LOCATION)
            }
        }
    }
    private val regResContacts = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            //callBackFun
        } else {
            fragment.context?.let {
                alertDialog(it, CONTACTS)
            }
        }
    }

    private val regResCall = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            //callBackFun
        } else {
            fragment.context?.let {
                alertDialog(it, CALL)
            }
        }
    }
}