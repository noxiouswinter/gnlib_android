package com.jinais.gnlib.android.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.jinais.gnlib.android.app.Launcher.LauncherActivity_1;
import com.jinais.gnlib.android.app.Storage.StorageActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStorageActivityClick(View view) {
        Intent intent = new Intent(this, StorageActivity.class);
        startActivity(intent);
    }

    public void onLauncherActivityClick(View view) {
        Intent intent = new Intent(this, LauncherActivity_1.class);
        startActivity(intent);
    }
}
