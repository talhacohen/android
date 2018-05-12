/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.etesync.syncadapter.resource;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.os.RemoteException;
import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.etesync.syncadapter.Constants;

import net.fortuna.ical4j.model.property.ProdId;

import org.dmfs.provider.tasks.TaskContract.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;
import java.util.logging.Level;

import at.bitfire.ical4android.AndroidTask;
import at.bitfire.ical4android.AndroidTaskFactory;
import at.bitfire.ical4android.AndroidTaskList;
import at.bitfire.ical4android.CalendarStorageException;
import at.bitfire.ical4android.Task;
import at.bitfire.vcard4android.ContactsStorageException;

public class LocalTask extends AndroidTask implements LocalResource {
    static {
        Task.prodId = new ProdId(Constants.PRODID_BASE + " ical4j/2.x");
    }

    protected String uuid;

    static final String COLUMN_ETAG = Tasks.SYNC1,
                        COLUMN_UID = Tasks.SYNC2,
                        COLUMN_SEQUENCE = Tasks.SYNC3;

    protected String fileName;
    protected String eTag;

    public String getUuid() {
        return uuid;
    }

    private String getFileName() {
        return fileName;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public LocalTask(@NonNull AndroidTaskList taskList, Task task, String fileName, String eTag) {
        super(taskList, task);
        this.fileName = fileName;
        this.eTag = eTag;
    }

    protected LocalTask(@NonNull AndroidTaskList taskList, long id, ContentValues baseInfo) {
        super(taskList, id);
        if (baseInfo != null) {
            fileName = baseInfo.getAsString(Events._SYNC_ID);
            eTag = baseInfo.getAsString(COLUMN_ETAG);
        }
    }

    @Override
    public String getContent() throws IOException, CalendarStorageException {
        App.log.log(Level.FINE, "Preparing upload of task " + getFileName(), getTask());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        getTask().write(os);

        return os.toString();    }

    @Override
    public boolean isLocalOnly() {
        return TextUtils.isEmpty(getETag());
    }

    /* process LocalTask-specific fields */

    @Override
    protected void populateTask(ContentValues values) throws FileNotFoundException, RemoteException, ParseException {
        super.populateTask(values);

        fileName = values.getAsString(Events._SYNC_ID);
        eTag = values.getAsString(COLUMN_ETAG);
        task.uid = values.getAsString(COLUMN_UID);

        task.sequence = values.getAsInteger(COLUMN_SEQUENCE);
    }

    @Override
    protected void buildTask(ContentProviderOperation.Builder builder, boolean update) {
        super.buildTask(builder, update);
        builder .withValue(Tasks._SYNC_ID, fileName)
                .withValue(COLUMN_UID, task.uid)
                .withValue(COLUMN_SEQUENCE, task.sequence)
                .withValue(COLUMN_ETAG, eTag);
    }


    /* custom queries */

    public void prepareForUpload() throws CalendarStorageException {
        try {
            final String uid = UUID.randomUUID().toString();
            final String newFileName = uid;

            ContentValues values = new ContentValues(2);
            values.put(Tasks._SYNC_ID, newFileName);
            values.put(COLUMN_UID, uid);
            taskList.provider.client.update(taskSyncURI(), values, null, null);

            fileName = newFileName;
            if (task != null)
                task.uid = uid;

        } catch (RemoteException e) {
            throw new CalendarStorageException("Couldn't update UID", e);
        }
    }

    @Override
    public void clearDirty(String eTag) throws CalendarStorageException {
        try {
            ContentValues values = new ContentValues(2);
            values.put(Tasks._DIRTY, 0);
            values.put(COLUMN_ETAG, eTag);
            if (task != null)
                values.put(COLUMN_SEQUENCE, task.sequence);
            taskList.provider.client.update(taskSyncURI(), values, null, null);

            this.eTag = eTag;
        } catch (RemoteException e) {
            throw new CalendarStorageException("Couldn't update _DIRTY/ETag/SEQUENCE", e);
        }
    }


    static class Factory implements AndroidTaskFactory {
        static final Factory INSTANCE = new Factory();

        @Override
        public LocalTask newInstance(AndroidTaskList taskList, long id, ContentValues baseInfo) {
            return new LocalTask(taskList, id, baseInfo);
        }

        @Override
        public LocalTask newInstance(AndroidTaskList taskList, Task task) {
            return new LocalTask(taskList, task, null, null);
        }

        @Override
        public LocalTask[] newArray(int size) {
            return new LocalTask[size];
        }
    }
}
