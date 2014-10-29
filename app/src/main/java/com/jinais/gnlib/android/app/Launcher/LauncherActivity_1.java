package com.jinais.gnlib.android.app.Launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jinais.gnlib.android.LogGN;
import com.jinais.gnlib.android.app.R;
import com.jinais.gnlib.android.launcher.GNLauncher;

public class LauncherActivity_1 extends Activity  implements View.OnClickListener {

    EditText etName;
    EditText etAge;
    Button bLaunchLauncherTestActivity_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();
    }

    private void initializeViews() {
        setContentView(R.layout.activity_test_activity_1);
        etName = (EditText) findViewById(R.id.tvName);
        etAge = (EditText) findViewById(R.id.etAge);
        bLaunchLauncherTestActivity_2 = (Button)findViewById(R.id.bLaunchLauncherTestActivity_2);
        bLaunchLauncherTestActivity_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String name = null;
        int age = 0;

        if(etName.getText() != null) {
            name = etName.getText().toString();
        }

        if(etAge.getText() != null) {
            try{
                age = Integer.parseInt(etAge.getText().toString());
            } catch(NumberFormatException e) {
                LogGN.e(e, "Error parsing: ", etAge.getText().toString());
            }
        }

        ((IMyLauncherInterface)GNLauncher.get().getProxy(this, IMyLauncherInterface.class, LauncherActivity_2.class)).sayHello(name, age);
    }
}
