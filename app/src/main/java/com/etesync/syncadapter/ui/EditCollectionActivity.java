/*
 * Copyright © 2013 – 2016 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.etesync.syncadapter.ui;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.etesync.syncadapter.R;
import com.etesync.syncadapter.model.CollectionInfo;
import com.etesync.syncadapter.resource.LocalCalendar;

public class EditCollectionActivity extends CreateCollectionActivity {
    private final static String EXTRA_ALLOW_DELETE = "allowDelete";

    protected boolean allowDelete;

    public static Intent newIntent(Context context, Account account, CollectionInfo info, boolean allowDelete) {
        Intent intent = new Intent(context, EditCollectionActivity.class);
        intent.putExtra(CreateCollectionActivity.EXTRA_ACCOUNT, account);
        intent.putExtra(CreateCollectionActivity.EXTRA_COLLECTION_INFO, info);
        intent.putExtra(EXTRA_ALLOW_DELETE, allowDelete);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allowDelete = getIntent().getExtras().getBoolean(EXTRA_ALLOW_DELETE, false);

        setTitle(R.string.edit_collection);

        if (info.type == CollectionInfo.Type.CALENDAR) {
            final View colorSquare = findViewById(R.id.color);
            if (info.color != null) {
                colorSquare.setBackgroundColor(info.color);
            } else {
                colorSquare.setBackgroundColor(LocalCalendar.defaultColor);
            }
        }

        final EditText edit = (EditText) findViewById(R.id.display_name);
        edit.setText(info.displayName);

        final EditText desc = (EditText) findViewById(R.id.description);
        desc.setText(info.description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (info.type == CollectionInfo.Type.ADDRESS_BOOK) {
            getMenuInflater().inflate(R.menu.activity_create_collection, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_edit_collection, menu);
        }
        return true;
    }

    public void onDeleteCollection(MenuItem item) {
        if (!allowDelete) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_error_dark)
                    .setTitle(R.string.account_delete_collection_last_title)
                    .setMessage(R.string.account_delete_collection_last_text)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            DeleteCollectionFragment.ConfirmDeleteCollectionFragment.newInstance(account, info).show(getSupportFragmentManager(), null);
        }
    }
}
