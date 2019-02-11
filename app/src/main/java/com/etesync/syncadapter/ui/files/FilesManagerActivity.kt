package com.etesync.syncadapter.ui.files

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.widget.Toast
import com.etesync.syncadapter.R
import com.etesync.syncadapter.ui.BaseActivity

class FilesManagerActivity : BaseActivity(), FilesUploadDialogFragment.UploadFileCallbacks, FilesReceiverFragment.UploadFile {
    private lateinit var fileUri: Uri
    private lateinit var fileName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files_manager)

        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent)
                } else {
                    handleSendFile(intent)
                }
            }
            intent?.action == Intent.ACTION_SEND_MULTIPLE -> {
                handleSendMultipleFiles(intent)
            }
            else -> {
                // TODO(talh): Show error?
                Toast.makeText(this, "Unsupported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun uploadFile(timeLimit: Int) {
        FilesUploadDialogFragment.newInstance(fileUri, timeLimit).show(supportFragmentManager, null)
    }

    override fun onUpload(url: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.root_element, FileManagerFragment.newInstance(fileName, url), null)
                .commit()

    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // TODO(talh): Call fiels upload dialog? Create a new fragment?
            Toast.makeText(this, "Text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendFile(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { data ->
            fileUri = data
            data.let { returnUri ->
                contentResolver.query(returnUri, null, null, null, null)
            }?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()

                fileName = cursor.getString(nameIndex)


                // TODO(tal): Add size? val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                supportFragmentManager.beginTransaction()
                        .add(R.id.root_element, FilesReceiverFragment.newInstance(fileName), null)
                        .commit()
            }
        }

    }

    private fun handleSendMultipleFiles(intent: Intent) {
        intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
            Toast.makeText(this, "Multiple files", Toast.LENGTH_SHORT).show()
            // TODO(talh): Do we want to support multiple images?
        }
    }
}
