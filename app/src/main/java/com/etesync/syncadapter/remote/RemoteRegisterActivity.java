package com.etesync.syncadapter.remote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etesync.syncadapter.R;

/**
 * Created by tal on 13/04/17.
 */

public class RemoteRegisterActivity extends AppCompatActivity {

    private static final String KEY_PACKAGE = "package_name";

    public static void startActivity(Context context, String packageName) {
        Intent intent = new Intent(context, RemoteRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_PACKAGE, packageName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_register);

        setTitle(R.string.api_register_title);

        final String packageName = getIntent().getStringExtra(KEY_PACKAGE);

        ((TextView) findViewById(R.id.api_register_text))
                .setText(String.format(getString(R.string.api_register_text), packageName));

        (findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                Toast.makeText(RemoteRegisterActivity.this, R.string.api_permission_not_granted, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        (findViewById(R.id.button_allow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View aView) {
                Toast.makeText(RemoteRegisterActivity.this, R.string.api_permission_granted, Toast.LENGTH_SHORT).show();
                ApiPermissionHelper.addCertificate(RemoteRegisterActivity.this, packageName);
                finish();
            }
        });
    }
}
