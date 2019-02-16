package com.etesync.syncadapter.ui.secureshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import com.etesync.syncadapter.R
import com.etesync.syncadapter.model.SecureShare
import com.etesync.syncadapter.ui.BaseActivity

class SecureShareActivity : BaseActivity(), UploadDialogFragment.UploadFileCallbacks {
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
            else -> {
                // We should never get here
                finish()
            }
        }
    }

    override fun onUpload(secureShare: SecureShare, url: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.root_element, ShareManagerFragment.newInstance(secureShare, url), null)
                .commit()

    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            supportFragmentManager.beginTransaction()
                    .add(R.id.root_element, SecureShareHandlerFragment.newInstance(SecureShare(Uri.EMPTY, it)), null)
                    .commit()
        }
    }

    private fun handleSendFile(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { data ->
            data.let { returnUri ->
                contentResolver.query(returnUri, null, null, null, null)
            }?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()

                val fileName = cursor.getString(nameIndex)

                supportFragmentManager.beginTransaction()
                        .add(R.id.root_element, SecureShareHandlerFragment.newInstance(SecureShare(data, fileName)), null)
                        .commit()
            }
        }
    }
}
