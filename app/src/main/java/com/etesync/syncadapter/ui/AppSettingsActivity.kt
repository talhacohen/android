/*
 * Copyright © 2013 – 2016 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.etesync.syncadapter.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.*
import com.etesync.syncadapter.App
import com.etesync.syncadapter.R
import com.etesync.syncadapter.model.ServiceDB
import com.etesync.syncadapter.model.Settings
import com.etesync.syncadapter.utils.HintManager
import com.etesync.syncadapter.utils.LanguageUtils
import java.net.URI
import java.net.URISyntaxException

class AppSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, SettingsFragment())
                    .commit()
        }
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        internal lateinit var dbHelper: ServiceDB.OpenHelper
        internal lateinit var settings: Settings

        internal lateinit var prefResetHints: Preference
        internal lateinit var prefResetCertificates: Preference
        internal lateinit var prefOverrideProxy: SwitchPreferenceCompat
        internal lateinit var prefDistrustSystemCerts: SwitchPreferenceCompat
        internal lateinit var prefLogToExternalStorage: SwitchPreferenceCompat

        internal lateinit var prefProxyHost: EditTextPreference
        internal lateinit var prefProxyPort: EditTextPreference

        override fun onCreate(savedInstanceState: Bundle?) {
            dbHelper = ServiceDB.OpenHelper(context)
            settings = Settings(dbHelper.readableDatabase)

            super.onCreate(savedInstanceState)
        }

        override fun onDestroy() {
            super.onDestroy()
            dbHelper.close()
        }

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.settings_app)

            prefResetHints = findPreference("reset_hints")

            prefOverrideProxy = findPreference("override_proxy") as SwitchPreferenceCompat
            prefOverrideProxy.isChecked = settings.getBoolean(App.OVERRIDE_PROXY, false)
            prefOverrideProxy.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                settings.putBoolean(App.OVERRIDE_PROXY, newValue as Boolean)
                true
            }

            prefProxyHost = findPreference("proxy_host") as EditTextPreference
            val proxyHost = settings.getString(App.OVERRIDE_PROXY_HOST, App.OVERRIDE_PROXY_HOST_DEFAULT)
            prefProxyHost.text = proxyHost
            prefProxyHost.summary = proxyHost
            prefProxyHost.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val host = newValue as String
                try {
                    URI(null, host, null, null)
                } catch (e: URISyntaxException) {
                    Snackbar.make(view!!, e.localizedMessage, Snackbar.LENGTH_LONG).show()
                    return@OnPreferenceChangeListener false
                }

                settings.putString(App.OVERRIDE_PROXY_HOST, host)
                prefProxyHost.summary = host
                true
            }

            prefProxyPort = findPreference("proxy_port") as EditTextPreference
            val proxyPort = settings.getString(App.OVERRIDE_PROXY_PORT, App.OVERRIDE_PROXY_PORT_DEFAULT.toString())
            prefProxyPort.text = proxyPort
            prefProxyPort.summary = proxyPort
            prefProxyPort.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                var port: Int
                try {
                    port = Integer.parseInt(newValue as String)
                } catch (e: NumberFormatException) {
                    port = App.OVERRIDE_PROXY_PORT_DEFAULT
                }

                settings.putInt(App.OVERRIDE_PROXY_PORT, port)
                prefProxyPort.text = port.toString()
                prefProxyPort.summary = port.toString()
                true
            }

            prefDistrustSystemCerts = findPreference("distrust_system_certs") as SwitchPreferenceCompat
            prefDistrustSystemCerts.isChecked = settings.getBoolean(App.DISTRUST_SYSTEM_CERTIFICATES, false)

            prefResetCertificates = findPreference("reset_certificates")

            prefLogToExternalStorage = findPreference("log_to_external_storage") as SwitchPreferenceCompat
            prefLogToExternalStorage.isChecked = settings.getBoolean(App.LOG_TO_EXTERNAL_STORAGE, false)

            initSelectLanguageList()
        }

        private fun initSelectLanguageList() {
            val listPreference = findPreference("select_language") as ListPreference
            LanguageTask(listPreference).execute()
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            if (preference === prefResetHints)
                resetHints()
            else if (preference === prefDistrustSystemCerts)
                setDistrustSystemCerts(preference.isChecked)
            else if (preference === prefResetCertificates)
                resetCertificates()
            else if (preference === prefLogToExternalStorage)
                setExternalLogging(preference.isChecked)
            else
                return false
            return true
        }

        private fun resetHints() {
            HintManager.resetHints(context!!)
            Snackbar.make(view!!, R.string.app_settings_reset_hints_success, Snackbar.LENGTH_LONG).show()
        }

        private fun setDistrustSystemCerts(distrust: Boolean) {
            settings.putBoolean(App.DISTRUST_SYSTEM_CERTIFICATES, distrust)

            // re-initialize certificate manager
            val app = context!!.applicationContext as App
            app.reinitCertManager()

            // reinitialize certificate manager of :sync process
            context!!.sendBroadcast(Intent(App.ReinitSettingsReceiver.ACTION_REINIT_SETTINGS))
        }

        private fun resetCertificates() {
            (context!!.applicationContext as App).certManager?.resetCertificates()
            Snackbar.make(view!!, getString(R.string.app_settings_reset_certificates_success), Snackbar.LENGTH_LONG).show()
        }

        private fun setExternalLogging(externalLogging: Boolean) {
            settings.putBoolean(App.LOG_TO_EXTERNAL_STORAGE, externalLogging)

            // reinitialize logger of default process
            val app = context!!.applicationContext as App
            app.reinitLogger()

            // reinitialize logger of :sync process
            context!!.sendBroadcast(Intent(App.ReinitSettingsReceiver.ACTION_REINIT_SETTINGS))
        }

        private inner class LanguageTask internal constructor(private val mListPreference: ListPreference) : AsyncTask<Void, Void, LanguageUtils.LocaleList>() {

            override fun doInBackground(vararg voids: Void): LanguageUtils.LocaleList {
                return LanguageUtils.getAppLanguages(context!!)

            }

            override fun onPostExecute(locales: LanguageUtils.LocaleList) {

                mListPreference.entries = locales.displayNames
                mListPreference.entryValues = locales.localeData

                mListPreference.value = settings.getString(App.FORCE_LANGUAGE,
                        App.DEFAULT_LANGUAGE)
                mListPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                    val value = newValue.toString()
                    if (value == (preference as ListPreference).value) return@OnPreferenceChangeListener true

                    LanguageUtils.setLanguage(context!!, value)

                    settings.putString(App.FORCE_LANGUAGE, newValue.toString())

                    val intent = Intent(context, AccountsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    false
                }
            }
        }
    }
}
