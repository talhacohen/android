package com.etesync.syncadapter.ui.files

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import com.etesync.syncadapter.R

class FilesReceiverFragment : Fragment() {
    private lateinit var fileName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName = arguments!!.getString(ARG_FILE_NAME)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_files_receiver, container, false)

        val fileNameView = view.findViewById<View>(R.id.fileName) as TextView
        fileNameView.text = fileName

        val spinner = view.findViewById<View>(R.id.time_limit_spinner) as Spinner
        spinner.setSelection(5)
        view.findViewById<View>(R.id.uploadFile).setOnClickListener {
            val timeLimit = context!!.resources.getIntArray(R.array.time_limits_values)[spinner.selectedItemPosition]
            if (activity is FilesReceiverFragment.UploadFile) {
                (activity as FilesReceiverFragment.UploadFile).uploadFile(timeLimit)
            }
        }

        return view
    }

    interface UploadFile {
        fun uploadFile(timeLimit: Int)
    }

    companion object {
        private val ARG_FILE_NAME = "fileName"

        fun newInstance(fileName : String): FilesReceiverFragment {
            val frag = FilesReceiverFragment()
            val args = Bundle(1)
            args.putString(ARG_FILE_NAME, fileName)
            frag.arguments = args
            return frag
        }
    }
}