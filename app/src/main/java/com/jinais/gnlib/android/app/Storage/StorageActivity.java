package com.jinais.gnlib.android.app.Storage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jinais.gnlib.android.LogGN;
import com.jinais.gnlib.android.app.R;
import com.jinais.gnlib.android.storage.GNState;
import com.jinais.gnlib.android.storage.GNStateManager;


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

        GNStateManager.init(this);
        GNStateManager.get().retrieve(this);

        if(this.studentInfo != null) {
            setGreetingTextFromStudentInfo();
        } else {
            LogGN.e("onCreate: studentInfo is null.");
        }
    }

    @Override
    protected void onPause() {
        LogGN.d("ON PAUSE");
        GNStateManager.get().store(this);
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
