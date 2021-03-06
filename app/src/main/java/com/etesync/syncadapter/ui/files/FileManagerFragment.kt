package com.etesync.syncadapter.ui.files

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.etesync.syncadapter.R


class FileManagerFragment : Fragment() {
    private lateinit var fileName: String
    private lateinit var fileUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = arguments!!.getString(ARG_FILE_NAME)!!
        fileUrl = arguments!!.getString(ARG_FILE_URL)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_file_manager, container, false)

        val fileNameView = view.findViewById<View>(R.id.fileName) as TextView
        fileNameView.text = "$fileName: $fileUrl"

        (view.findViewById<View>(R.id.shareFile) as Button).setOnClickListener {
            shareUrlExcludingEteSync()
        }

        return view
    }

    private fun shareUrlExcludingEteSync() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, fileUrl);
        val shareIntentsLists = ArrayList<Intent>()

        val resInfos = context!!.packageManager.queryIntentActivities(shareIntent, 0)
        if (!resInfos.isEmpty()) {
            for (resInfo in resInfos) {
                val packageName = resInfo.activityInfo.packageName
                if (!packageName.equals(context!!.applicationContext.packageName)) {
                    val intent = Intent()
                    intent.component = ComponentName(packageName, resInfo.activityInfo.name)
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain"
                    intent.setPackage(packageName)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, fileUrl);
                    shareIntentsLists.add(intent)
                }
            }
            if (!shareIntentsLists.isEmpty()) {
                val chooserIntent = Intent.createChooser(shareIntent, "Share $fileName")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentsLists.toArray(arrayOf<Parcelable>()))
                startActivity(chooserIntent)
            } else
                Log.e("Error", "No Apps can perform your task")

        }
    }

    companion object {
        private val ARG_FILE_NAME = "fileName"
        private val ARG_FILE_URL = "fileUrl"

        fun newInstance(fileName: String, fileUrl: String): FileManagerFragment {
            val frag = FileManagerFragment()
            val args = Bundle(1)
            args.putString(ARG_FILE_NAME, fileName)
            args.putString(ARG_FILE_URL, fileUrl)
            frag.arguments = args
            return frag
        }
    }
}