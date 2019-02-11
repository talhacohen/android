package com.etesync.syncadapter.ui.files

import android.app.Dialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.etesync.syncadapter.R

class FilesUploadDialogFragment : DialogFragment() {
    private lateinit var fileUri: Uri
    private var timeLimit: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileUri = arguments!!.getParcelable(KEY_FILE_URI)!!
        timeLimit = arguments!!.getInt(KEY_TIME_LIMIT)

        UploadFile().execute()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progress = ProgressDialog(context)
        progress.setTitle(getString(R.string.uploading_files))
        progress.setMessage(getString(R.string.please_wait))
        progress.isIndeterminate = true
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.setCanceledOnTouchOutside(false)
        isCancelable = false
        return progress
    }

    private inner class UploadFile : AsyncTask<Void, Int, Void>() {

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
            dismiss()
            if (activity is UploadFileCallbacks) {
                (activity as UploadFileCallbacks).onUpload("FILE_URL")
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            (dialog as ProgressDialog).progress = values[0]!!
        }
    }

    interface UploadFileCallbacks {
        fun onUpload(url: String)
    }

    companion object {
        private val KEY_FILE_URI = "fileUri"
        private val KEY_TIME_LIMIT = "timeLimit"

        fun newInstance(uri: Uri, timeLimit: Int): FilesUploadDialogFragment {
            val frag = FilesUploadDialogFragment()
            val args = Bundle(2)
            args.putParcelable(KEY_FILE_URI, uri)
            args.putInt(KEY_TIME_LIMIT, timeLimit)
            frag.arguments = args
            return frag
        }
    }
}
