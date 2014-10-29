package com.jinais.gnlib.android.app.Launcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.jinais.gnlib.android.app.R;
import com.jinais.gnlib.android.launcher.GNLauncher;


public class LauncherActivity_2 extends Activity implements IMyLauncherInterface {

    TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();

        GNLauncher.get().ping(this);
    }

    private void initializeViews() {
        setContentView(R.layout.activity_test_activity_2);
        tvHello = (TextView)findViewById(R.id.tvHello);
    }

    @Override
    public void sayHello(String name, int age) {
        tvHello.setText("Hello " + name + "! \nYour age is: " + age);
    }
}
