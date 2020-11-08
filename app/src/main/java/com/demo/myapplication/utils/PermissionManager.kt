package com.demo.myapplication.utils
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionManager(private val activity: Activity) {


    private val REQ_CODE_SETTING: Int=100;

    fun handlePermission(listener: PermissionGrantedListener, PermissionList: Array<String?>?) {
        Dexter.withActivity(activity)

            .withPermissions(*permissionList())
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.deniedPermissionResponses != null
                        && report.deniedPermissionResponses.size > 0
                    ) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    } else {
                        listener.onPermissionGranted(report)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(activity, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(
            activity
        )
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, REQ_CODE_SETTING)

    }

    interface PermissionGrantedListener {
        /**
         * Method called whenever a requested permission has been granted
         *
         * @param report A response object that contains the permission that has been requested and
         * any additional flags relevant to this response
         */
        fun  onPermissionGranted(report: MultiplePermissionsReport?)
    }

    companion object {
        fun permissionList(): Array<String?> {
            return arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,

            )
        }
    }
}