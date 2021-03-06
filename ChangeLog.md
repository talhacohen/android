# Changelog

## Version 1.0.1
* Email invitations: fix rare crash for events with no end date.

## Version 1.0.0
* Implement changing the encryption password
* Add support for importing contat groups from account and file

## Version 0.25.0
* Add support for the new associate account type
* Handle exceptions for read only journals
* Fix date being sometimes incorrect in the journal item preview.

## Version 0.24.1
* Fix issue with Calendars missing from the account view.

## Version 0.24.0
* Fix crash when setting up user info for the first time
* Email invitations: fix invitations not being attached on event updates in some rare cases.
* Email invitations: add a way to send an invite from the journal log.

## Version 0.23.2
* Tasks: add ability to create, edit, deleted and view task journals

## Version 0.23.1
* Catch IllegalStateExceptions in the account changed receiver.

## Version 0.23.0
* Add Tasks support via OpenTasks!
* Minimum Android version is now KitKat (4.4)
* Migrate almost all of the code to Kotlin
* Upgraded vcard4android and ical4android deps to latest (after more than a year!)
* Drop the custom password entry widget in favour of the stock one
* Debug handler now sends the report as body if ProtonMail (doesn't support attachments) is installed
* Fix the shared-to user in shared journals to be case insensitive
* Update HTTPS trusted ciphers list.
* Fix account deletion issues on some devices.

## Version 0.22.6
* Fix rare crash when importing / creating events with a missing or invalid timezone.

## Version 0.22.5
* Fix rare crash when trying to import contacts from an account on the phone.

## Version 0.22.4
* Event invitations: fix issue with times showing wrong across timezones.
* Fix group memberships to show with more Contacts apps
* Fix bug causing groups to get duplicated on modifications rather than just updated.

## Version 0.22.3
* Event invitations: add timezone and location information to email summary.
* Update ical4j dependency.

## Version 0.22.2
* Really fix the crashes because of the missing support lib dependencies
* Fix crash when importing a contact with a remote (not-embedded) picture

## Version 0.22.1
* Fix crashes because of missing support lib dependencies

## Version 0.22.0
* Add a setting to disable the "New Journal Entries" notifications

## Version 0.21.6
* Fix handling of partial dates in the jorunal contact view.

## Version 0.21.5
* Fix issue when viewing journal entries for contacts with malformed (empty but existing) nicknames

## Version 0.21.4
* Fix previous broken build because of bad submodules.

## Version 0.21.3
* Fix issue with events being saved with both duration and an end date.

## Version 0.21.2
* Fix sync when events have attendees and a duration rather than an end date.

## Version 0.21.1
* Trigger F-Droid build

## Version 0.21.0
* Add a notification to send email invites whenever an event has atendees.

## Version 0.20.4
* Restore the webview state after device rotation.

## Version 0.20.3
* Fix adding journal members on some devices.
* Don't crash when trying to delete non-existent records.

## Version 0.20.2
* Make HTTP request/response logging more verbose when logging to file (useful when debugging).

## Version 0.20.1
* Fix issue with contacts sometimes not syncing or syncing very slowly after big and heavy imports.
* Remove some potentially sensitive info from logs (needed now that we suggest sharing logs on crashes).
* ACRA: increase the number of last log-lines shared in crash reports to 500.
* Use ACRA when sharing the debug log from the debug activity (it also shares more info).

## Version 0.20.0
* Automatically generate stack traces on crashes and offer to send them by email. (Powered by ACRA).
* Detect and alert potential vendor specific bugs (namely with Xiaomi devices).
* Import: fix showing of the "import has finished" dialog.
* Import: remove duplicate detection, this didn't work well and was causing issues.
* Contact import: fix potential double-import.
* Make journal ownership tests case insensitive (as emails are).
* Update gradle, support libs and requery, and get rid of lombok.
* Make it more obvious that file-logging notification is persistent.

## Version 0.19.6
* Fix confusing error message when creating/fetching user info.

## Version 0.19.5
* Update store description.

## Version 0.19.4
* Improve error message when using the wrong encryption password on account creation.
* Update target SDK

## Version 0.19.3
* Update Polish translation.

## Version 0.19.2
* Fix a crash on import when app is in the background.
* Fix a few minor leaks.
* Update Polish translation.

## Version 0.19.1
* Update description to include self-hosting information on f-droid.

## Version 0.19.0
* Added support for setting a custom server address (needed for self-hosting support)
* Add support for anniversaries without a year

## Version 0.18.2
* Added fastlane data for f-droid

## Version 0.18.1
* Add support for birthdays without the year.
* Notify the user on journal modification.
* Add a debug option to force the UI language.

## Version 0.18.0
* Add back support for contact groups - thanks to user "359" for reporting this.
* Fix an issue causing local cache corruption in some rare cases - thanks to user "359" for reporting this.
* Clean up the vObject PRODID.
* Update okhttp

## Version 0.17.0
* Download journal in chunks instead of all at once
    * Improves behaviour on slow and unreliable internet connections.
    * Fixes Android cancelling the sync mid-way on some Android versions.
* Fix crash when exiting some activities before content loading has finished.

## Version 0.16.0
* Improve the look and feel of the journal viewer and show contacts and events in a prettier way.
* Increase default RSA key length to 3072 bit.
* Fix an issue with account addition not working in some cases.
* Add a unique constraint on journal UIDs (possible thanks to a bug fix in requery)

## Version 0.15.0
* Add support for multiple address books (adjusted from the DAVdroid solution).
    * This works around the Android limitation of one address book per account.
* Change the fingerprint format to be numeric instead of hex (thanks to Dominik Schürmann for the suggestion)
* Fix UUID generation - in some cases weird UUIDs were generated for events.
* Fix opening of dashboard in the external browser.
* Fix a rare crash on the login page.
* Fix potential crash when viewing journals before they have been sync.

## Version 0.14.0
* Add support for sharing journals and using shared journals.
    * This change includes viewing and verifying user's public key fingerprints, as well as automatic upload of encrypted private key.
* Add support for read-only journals (not controlling them, just treating existing read-only ones as such)
* Add icons to menu operations.
* Update dependencies (e.g. okhttp, requery, ical4android)

## Version 0.13.0
* Added a showcase wizard to showcase some features on first use.
* Make the sync more robust in case of interruptions.
* Changed the allowed TLS ciphers to only include a secure list.
* Changed the minimum required version to Android 4.1 (sdk version 16)
* Show a message when system-wide auto-sync is disabled
* Set correct PROID for Contacts, Events and Tasks
* Added many tests for the crypto and service.
* Code cleanups and refactoring in the sync manager.
* Update {cert,ical,vcard}4android to latest.
* Minor fixes

## Version 0.12.0
* Add import from local account (import calendars/contacts)
* Update the crypto protocol to version 2.
* Refactoring

## Version 0.11.1
* Fix potential crash when updating the app.

## Version 0.11.0
* Add import from file (vCard/iCal).
* Fix bug preventing from re-adding a removed account.
* Fix issue with some entries marked as "ADD" instead of "CHANGE" in some cases.
* Fix issues with embedded webview not showing all pages it should.
* Always log deletes, even when not previously added to server.
* Refresh the collection view when editing/deleting.
* Login: add a "forgot password" link.

## Version 0.10.0
* Open FAQ, user guide and signup page inside the app
* Add calendar/contacts view and edits screens
* Show the change journal on calendar/contacts view screen
* Journals are now cached locally
* Setup account page: added more info about the encryption password.
* Update German translation

## Version 0.9.2
* Reword some parts of the UI
* Add a "Coming soon" section for the Change Journal

## Version 0.9.1
* Add links to the usage guide.
* Open the account's dashboard when getting a "UserInactive" exception.
* Change how notifications launch activities.
* Fix issue with only one of the notifications being clickable.
* Shorten notification error title so account name is visible.
* Internal changes to how HttpExceptions are handled.
* Update strings.

## Version 0.9.0
* Rename the Android package to EteSync to avoid clashes with DAVdroid
* Optimise proguard rules and fix warnings
* Implement sha256 using bouncy-castle.
* Pin gradle plugin version to 2.2.3.

## Version 0.8.1
* Request permissions on app launch instead of only when needed.
* Update vcard4droid and ical4droid.

## Version 0.8.0
* Initial release.
