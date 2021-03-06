/*
 * Copyright © 2013 – 2016 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.etesync.syncadapter.ui

import android.accounts.Account
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SyncStatusObserver
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.LoaderManager
import android.support.v4.app.NavUtils
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.preference.*
import android.text.TextUtils
import android.view.MenuItem
import com.etesync.syncadapter.AccountSettings
import com.etesync.syncadapter.App
import com.etesync.syncadapter.Constants.KEY_ACCOUNT
import com.etesync.syncadapter.InvalidAccountException
import com.etesync.syncadapter.R
import com.etesync.syncadapter.ui.setup.LoginCredentials
import com.etesync.syncadapter.ui.setup.LoginCredentialsChangeFragment

class AccountSettingsActivity : BaseActivity() {
    private lateinit var account: Account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        account = intent.getParcelableExtra(KEY_ACCOUNT)
        title = getString(R.string.settings_title, account.name)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val frag = AccountSettingsFragment()
            frag.arguments = intent.extras
            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, frag)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra(AccountActivity.EXTRA_ACCOUNT, account)
            NavUtils.navigateUpTo(this, intent)
            return true
        } else
            return false
    }


    class AccountSettingsFragment : PreferenceFragmentCompat(), LoaderManager.LoaderCallbacks<AccountSettings> {
        internal lateinit var account: Account

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            account = arguments?.getParcelable(KEY_ACCOUNT)!!

            loaderManager.initLoader(0, arguments, this)
        }

        override fun onCreatePreferences(bundle: Bundle, s: String) {
            addPreferencesFromResource(R.xml.settings_account)
        }

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<AccountSettings> {
            return AccountSettingsLoader(context!!, args!!.getParcelable(KEY_ACCOUNT) as Account)
        }

        override fun onLoadFinished(loader: Loader<AccountSettings>, settings: AccountSettings?) {
            if (settings == null) {
                activity!!.finish()
                return
            }

            // category: authentication
            val prefPassword = findPreference("password") as EditTextPreference
            prefPassword.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val credentials = if (newValue != null) LoginCredentials(settings.uri, account.name, newValue as String) else null
                LoginCredentialsChangeFragment.newInstance(account, credentials!!).show(fragmentManager!!, null)
                loaderManager.restartLoader(0, arguments, this@AccountSettingsFragment)
                false
            }

            // Category: encryption
            val prefEncryptionPassword = findPreference("encryption_password")
            prefEncryptionPassword.onPreferenceClickListener = Preference.OnPreferenceClickListener { _ ->
                startActivity(ChangeEncryptionPasswordActivity.newIntent(activity!!, account))
                true
            }

            // category: synchronization
            val prefSyncContacts = findPreference("sync_interval_contacts") as ListPreference
            val syncIntervalContacts = settings.getSyncInterval(App.addressBooksAuthority)
            if (syncIntervalContacts != null) {
                prefSyncContacts.value = syncIntervalContacts.toString()
                if (syncIntervalContacts == AccountSettings.SYNC_INTERVAL_MANUALLY)
                    prefSyncContacts.setSummary(R.string.settings_sync_summary_manually)
                else
                    prefSyncContacts.summary = getString(R.string.settings_sync_summary_periodically, prefSyncContacts.entry)
                prefSyncContacts.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                    settings.setSyncInterval(App.addressBooksAuthority, java.lang.Long.parseLong(newValue as String))
                    loaderManager.restartLoader(0, arguments, this@AccountSettingsFragment)
                    false
                }
            } else {
                prefSyncContacts.isEnabled = false
                prefSyncContacts.setSummary(R.string.settings_sync_summary_not_available)
            }

            val prefSyncCalendars = findPreference("sync_interval_calendars") as ListPreference
            val syncIntervalCalendars = settings.getSyncInterval(CalendarContract.AUTHORITY)
            if (syncIntervalCalendars != null) {
                prefSyncCalendars.value = syncIntervalCalendars.toString()
                if (syncIntervalCalendars == AccountSettings.SYNC_INTERVAL_MANUALLY)
                    prefSyncCalendars.setSummary(R.string.settings_sync_summary_manually)
                else
                    prefSyncCalendars.summary = getString(R.string.settings_sync_summary_periodically, prefSyncCalendars.entry)
                prefSyncCalendars.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                    settings.setSyncInterval(CalendarContract.AUTHORITY, java.lang.Long.parseLong(newValue as String))
                    loaderManager.restartLoader(0, arguments, this@AccountSettingsFragment)
                    false
                }
            } else {
                prefSyncCalendars.isEnabled = false
                prefSyncCalendars.setSummary(R.string.settings_sync_summary_not_available)
            }

            val prefWifiOnly = findPreference("sync_wifi_only") as SwitchPreferenceCompat
            prefWifiOnly.isChecked = settings.syncWifiOnly
            prefWifiOnly.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, wifiOnly ->
                settings.setSyncWiFiOnly(wifiOnly as Boolean)
                loaderManager.restartLoader(0, arguments, this@AccountSettingsFragment)
                false
            }

            val prefWifiOnlySSID = findPreference("sync_wifi_only_ssid") as EditTextPreference
            val onlySSID = settings.syncWifiOnlySSID
            prefWifiOnlySSID.text = onlySSID
            if (onlySSID != null)
                prefWifiOnlySSID.summary = getString(R.string.settings_sync_wifi_only_ssid_on, onlySSID)
            else
                prefWifiOnlySSID.setSummary(R.string.settings_sync_wifi_only_ssid_off)
            prefWifiOnlySSID.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val ssid = newValue as String
                settings.syncWifiOnlySSID = if (!TextUtils.isEmpty(ssid)) ssid else null
                loaderManager.restartLoader(0, arguments, this@AccountSettingsFragment)
                false
            }
        }

        override fun onLoaderReset(loader: Loader<AccountSettings>) {}

    }


    private class AccountSettingsLoader(context: Context, internal val account: Account) : AsyncTaskLoader<AccountSettings>(context), SyncStatusObserver {
        internal lateinit var listenerHandle: Any

        override fun onStartLoading() {
            forceLoad()
            listenerHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, this)
        }

        override fun onStopLoading() {
            ContentResolver.removeStatusChangeListener(listenerHandle)
        }

        override fun abandon() {
            onStopLoading()
        }

        override fun loadInBackground(): AccountSettings? {
            val settings: AccountSettings
            try {
                settings = AccountSettings(context, account)
            } catch (e: InvalidAccountException) {
                return null
            }

            return settings
        }

        override fun onStatusChanged(which: Int) {
            App.log.fine("Reloading account settings")
            forceLoad()
        }

    }

}
