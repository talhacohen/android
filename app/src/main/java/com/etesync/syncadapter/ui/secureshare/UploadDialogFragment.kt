package com.etesync.syncadapter.ui.secureshare

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.etesync.syncadapter.R
import com.etesync.syncadapter.model.SecureShare

class UploadDialogFragment : DialogFragment() {
    private lateinit var secureShare: SecureShare
    private var timeLimit: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secureShare = arguments!!.getParcelable(SecureShare.KEY_SECURE_SHARE)!!

        UploadFile().execute()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progress = ProgressDialog(context)
        progress.setTitle(getString(R.string.uploading_file))
        progress.setMessage(getString(R.string.please_wait))
        progress.isIndeterminate = true
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.setCanceledOnTouchOutside(false)
        isCancelable = false
        return progress
    }

    private inner class UploadFile : AsyncTask<Void, Int, Void>() {
        private var notificationManager : NotificationManagerCompat = NotificationManagerCompat.from(context!!)

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.notification_channel_name)
                val descriptionText = getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                        context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            }

        override fun onPreExecute() {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_sync_dark)
                    .setContentTitle(context!!.getString(R.string.app_name))
                    .setContentText(context!!.getString(R.string.uploading_file))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()

            notificationManager.notify(secureShare.title.hashCode(), builder)
        }

        override fun doInBackground(vararg voids: Void): Void? {
            // TODO(talh): need to call UploadFile only after the dialog is created
            Thread.sleep(1000)
            publishProgress(10)
            Thread.sleep(1000)
            publishProgress(90)
            Thread.sleep(2000)
            return null

        }

        override fun onPostExecute(result: Void) {
            notificationManager.cancel(secureShare.title.hashCode())
            dismiss()
            if (activity is UploadFileCallbacks) {
                (activity as UploadFileCallbacks).onUpload(secureShare, "FILE_URL")
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            (dialog as ProgressDialog).progress = values[0]!!
        }
    }

    interface UploadFileCallbacks {
        fun onUpload(secureShare: SecureShare, url: String)
    }

    companion object {
        private val CHANNEL_ID = "EteSync_SecureShare"

        private val KEY_TIME_LIMIT = "timeLimit"

        fun newInstance(secureShare: SecureShare, timeLimit: Int): UploadDialogFragment {
            val frag = UploadDialogFragment()
            val args = Bundle(2)
            args.putParcelable(SecureShare.KEY_SECURE_SHARE, secureShare)
            args.putInt(KEY_TIME_LIMIT, timeLimit)
            frag.arguments = args
            return frag
        }
    }
}
