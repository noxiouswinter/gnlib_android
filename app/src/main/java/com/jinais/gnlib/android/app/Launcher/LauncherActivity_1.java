/*
 * Copyright 2014 Jinais Ponnampadikkal Kader
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

        ((IPayload)GNLauncher.get().getProxy(this, IPayload.class, LauncherActivity_2.class)).sayHello(name, age);
    }
}
