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
import android.widget.TextView;
import com.jinais.gnlib.android.app.R;
import com.jinais.gnlib.android.launcher.GNLauncher;


public class LauncherActivity_2 extends Activity implements IPayload {

    TextView tvHello;

    String name;
    int age;

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
        this.name = name;
        this.age = age;
        setGreeting();
    }

    private void setGreeting() {
        tvHello.setText("Hello " + this.name + "! \nYour age is: " + this.age);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
