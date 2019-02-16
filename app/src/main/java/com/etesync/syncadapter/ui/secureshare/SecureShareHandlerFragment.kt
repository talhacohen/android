package com.etesync.syncadapter.ui.secureshare

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import com.etesync.syncadapter.R
import com.etesync.syncadapter.model.SecureShare

class SecureShareHandlerFragment : Fragment() {
    private lateinit var secureShare: SecureShare

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secureShare = arguments!!.getParcelable(SecureShare.KEY_SECURE_SHARE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_files_receiver, container, false)

        val fileNameView = view.findViewById<View>(R.id.fileName) as TextView
        fileNameView.text = secureShare.fileName

        val spinner = view.findViewById<View>(R.id.time_limit_spinner) as Spinner

        spinner.setSelection(spinner.count - 1)
        view.findViewById<View>(R.id.uploadFile).setOnClickListener {
            val timeLimit = context!!.resources.getIntArray(R.array.time_limits_values)[spinner.selectedItemPosition]
            UploadDialogFragment.newInstance(secureShare, timeLimit).show(childFragmentManager, null)
        }

        return view
    }

    companion object {
        fun newInstance(secureShare : SecureShare): SecureShareHandlerFragment {
            val frag = SecureShareHandlerFragment()
            val args = Bundle(1)
            args.putParcelable(SecureShare.KEY_SECURE_SHARE, secureShare)
            frag.arguments = args
            return frag
        }
    }
}