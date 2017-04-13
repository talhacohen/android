/*
 * Copyright © 2013 – 2016 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.etesync.syncadapter.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.etesync.syncadapter.IEteSyncService;

public class RemoteService extends Service {

    private ApiPermissionHelper mApiPermissionHelper;

    private final IEteSyncService.Stub mBinder = new IEteSyncService.Stub() {
        @Override
        public boolean hasPermission() throws RemoteException {
            return mApiPermissionHelper.isAllowedIgnoreErrors();
        }

        @Override
        public void requestPermission() throws RemoteException {
            if (mApiPermissionHelper.isAllowedIgnoreErrors()) {
                return;
            }

            RemoteRegisterActivity.startActivity(RemoteService.this, mApiPermissionHelper.getCurrentCallingPackage());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mApiPermissionHelper = new ApiPermissionHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
