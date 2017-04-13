// IEteSyncService.aidl
package com.etesync.syncadapter;

// Declare any non-default types here with import statements

interface IEteSyncService {
    boolean hasPermission();

    void requestPermission();
}
