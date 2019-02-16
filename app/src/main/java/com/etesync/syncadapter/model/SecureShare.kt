package com.etesync.syncadapter.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class SecureShare(var fileUri: Uri, var fileName: String) : Parcelable {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(fileUri, Uri.CONTENTS_FILE_DESCRIPTOR)
        dest.writeString(fileName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<SecureShare> {
            override fun createFromParcel(source: Parcel): SecureShare {
                return SecureShare(
                        source.readParcelable(null)!!,
                        source.readString()!!
                )
            }

            override fun newArray(size: Int): Array<SecureShare?> {
                return arrayOfNulls(size)
            }
        }

        val KEY_SECURE_SHARE = "secureShare"
    }
}