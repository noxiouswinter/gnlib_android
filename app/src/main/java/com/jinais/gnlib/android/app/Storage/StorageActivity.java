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

package com.jinais.gnlib.android.app.Storage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jinais.gnlib.android.LogGN;
import com.jinais.gnlib.android.app.R;
import com.jinais.gnlib.android.state.GNState;
import com.jinais.gnlib.android.state.GNStateManagerFactory;


public class StorageActivity extends Activity {

    EditText etName;
    EditText etAge;
    TextView tvGreetingText;

    @GNState
    StudentInfo studentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();
        LogGN.d("ON CREATE");

        GNStateManagerFactory.init(this);
        GNStateManagerFactory.get().retrieve(this);

        if(this.studentInfo != null) {
            setGreetingTextFromStudentInfo();
        } else {
            LogGN.e("onCreate: studentInfo is null.");
        }
    }

    @Override
    protected void onPause() {
        LogGN.d("ON PAUSE");
        GNStateManagerFactory.get().store(this);
        super.onPause();
    }

    private void initializeViews() {
        setContentView(R.layout.activity_storage);
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        tvGreetingText = (TextView) findViewById(R.id.tvGreetingText);
    }

    public void OnSetGreetingClick(View view) {
        setGreetingAndStudentInfoFromUserInput();
    }

    private void setGreetingAndStudentInfoFromUserInput() {
        if(this.studentInfo == null) {
            this.studentInfo = new StudentInfo();
        }
        this.studentInfo.name = etName.getText().toString();
        try {
            this.studentInfo.age = Integer.parseInt(etAge.getText().toString());
        } catch (NumberFormatException e) {
            LogGN.e(e, "Error parsing Age.");
        }
        setGreetingTextFromStudentInfo();
    }

    private void setGreetingTextFromStudentInfo() {
        if(this.studentInfo == null) {
            LogGN.e("Cached StudentInfo is null!");
            return;
        }
        tvGreetingText.setText("Hello " + this.studentInfo.name + "! \nYour age is: " + this.studentInfo.age);
    }

    public StudentInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
    }

    @GNState
    public static class StudentInfo {
        @GNState
        public String name;
        @GNState
        public int age = 0;
    }
}
