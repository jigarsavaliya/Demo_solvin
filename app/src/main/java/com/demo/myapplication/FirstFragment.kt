package com.demo.myapplication

import DownloadAdapter
import ItemModel
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.MultiplePermissionsReport
import kotlinx.android.synthetic.main.fragment_first.*
import java.io.File


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(),PermissionManager.PermissionGrantedListener {

    lateinit var mAdapter: DownloadAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = DownloadAdapter(ArrayList()) { item ->
if (!item.isDownloading){
    val b:Bundle=Bundle().apply {
        putSerializable("MODEL",item)
    }
    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,b)
}
        }
        rv_items.adapter=mAdapter

        btn_download.setOnClickListener {
            if (et_input.text!!.trim().isNotEmpty())
            PermissionManager(requireActivity())
                .handlePermission(this, PermissionManager.permissionList());
           else
                Toast.makeText(requireContext(), "Please enter url", Toast.LENGTH_LONG).show();

        }
//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }
    private fun downloadFile(url: String) {
        val model=ItemModel(mAdapter.list.size, url)
      if (url.contains("http") && mAdapter.AddItems(model)) {

          val directory = File(Environment.DIRECTORY_DOWNLOADS)

          if (!directory.exists()) {
              directory.mkdirs()
          }
          model.Path= directory.toString()+File.separator+ model.title;
          val downloadManager =
              requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

          val downloadUri = Uri.parse(url)


          val request = DownloadManager.Request(downloadUri).apply {
              setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                  .setAllowedOverRoaming(false)
                  .setTitle(url.substring(url.lastIndexOf("/") + 1))
                  .setDescription("")
                  .setDestinationInExternalPublicDir(
                      directory.toString(),
                      url.substring(url.lastIndexOf("/") + 1)
                  )
          }



                  val downloadId = downloadManager.enqueue(request)
                  val query = DownloadManager.Query().setFilterById(downloadId)

          Thread {
              var downloading = true
              while (downloading) {

                  val cursor: Cursor = downloadManager.query(query)
                  cursor.moveToFirst()

                  val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val  totalBytesDownloaded = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )

                 val totalBytes = cursor.getInt(
                     cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                 )
                  if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                      val fileSizeInKB: Int = totalBytes / 1024;
                      model.Size=fileSizeInKB.toString()
                      downloading = false

                  }
                  val des = cursor.getString(
                      cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)
                  )
                  val downloadProgress =
                      (totalBytesDownloaded.toDouble() / totalBytes.toDouble() * 100f).toInt()


                  rv_items.post {

                      mAdapter.setDownloading(model, downloading);
                      mAdapter.setProgress(
                          model,
                          downloadProgress
                      );
                      mAdapter.setTitle(model, status);
                      cursor.close()
                  }

              }
          }.start()

      }else
          Toast.makeText(requireContext(), "Already Exist", Toast.LENGTH_LONG).show();
    }

    /*private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }*/
    override fun onPermissionGranted(report: MultiplePermissionsReport?) {
            downloadFile(et_input.text.toString())
    }
}