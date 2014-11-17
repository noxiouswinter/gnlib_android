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

package com.jinais.gnlib.android.app.History;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jinais.gnlib.android.LogGN;
import com.jinais.gnlib.android.app.R;
import com.jinais.gnlib.android.state.GNState;
import com.jinais.gnlib.android.state.history.GNHistoryManager;
import com.jinais.gnlib.android.state.history.GNHistoryManagerFactory;

public class HistoryActivity extends Activity {

    GNHistoryManager gnHistoryManager;

    @GNState
    String name;

    @GNState
    Integer age;

    EditText etName;
    EditText etAge;
    TextView tvHistoryPosition;
    TextView tvHistoryLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initializeViews();

        gnHistoryManager = GNHistoryManagerFactory.get();
        setHistoryStatusViews();
    }

    private void initializeViews() {
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        tvHistoryPosition = (TextView) findViewById(R.id.tvHistoryPosition);
        tvHistoryLength = (TextView) findViewById(R.id.tvHistoryLength);
    }

    private void updateViewsFromState() {
        etName.setText(this.name == null? "" : this.name);
        etAge.setText((this.age == null)? "" : Integer.toString(this.age));
    }

    private void updateStateFromViews() {
        String nameString = etName.getText().toString();
        this.name = nameString.equals("") ? null : nameString;
        String ageString = etAge.getText().toString();
        Integer age = null;
        try{
            age = Integer.parseInt(ageString);
        } catch(NumberFormatException e) {
            LogGN.e(e);
        }
        this.age = age;
    }

    private void resetState() {
        this.name = null;
        this.age = null;
    }

    private void setHistoryStatusViews() {
        Integer currentPositionInHistory = gnHistoryManager.getCurrentPositionInHistory(this.getClass());
        tvHistoryPosition.setText(currentPositionInHistory == null ? "Not in history." : currentPositionInHistory.toString());
        Integer currentHistorySize = gnHistoryManager.getHistorySize(this.getClass());
        tvHistoryLength.setText(currentHistorySize == null ? "No history record yet." : currentHistorySize.toString());
    }

    private void setStateFromViews() {
        this.name = etName.getText().toString();
        Integer intValue = null;
        try{
          intValue = Integer.parseInt(etAge.getText().toString());
        } catch(NumberFormatException e) {
            LogGN.e(e);
        }
        this.age = intValue;
    }

    public void onPreviousButtonClicked(View view) {
        LogGN.d("Previous");
        updateStateFromViews();
        if(gnHistoryManager.stepBackInHistory(this)) {
            updateViewsFromState();
        }
        setHistoryStatusViews();
    }
    public void onNextButtonClicked(View view) {
        LogGN.d("Next");
        setStateFromViews();

        //If currently not stepping through history, add this state to the history.
        if(gnHistoryManager.getCurrentPositionInHistory(this.getClass()) == null) {
            updateStateFromViews();
            if(name == null && age == null) {
                //If state is default. Nothing new to add.
                return;
            }
            gnHistoryManager.addHistory(this);
            //New screen.
            resetState();
        } else { //Else step forward in history.
            gnHistoryManager.stepForwardInHistory(this);
        }
        updateViewsFromState();
        setHistoryStatusViews();
    }
}
