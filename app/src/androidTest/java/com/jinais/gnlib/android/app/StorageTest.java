package com.jinais.gnlib.android.app;

import android.test.ActivityInstrumentationTestCase2;
import com.jinais.gnlib.android.LogGN;
import com.jinais.gnlib.android.app.Storage.StorageActivity;
import com.jinais.gnlib.android.storage.GNStorageManager;

/**
 * Created by jkader on 10/29/14.
 */
public class StorageTest extends ActivityInstrumentationTestCase2<StorageActivity> {

    StorageActivity storageActivity;

    public StorageTest() {super(StorageActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testStorage() {

        //Start Activity.
        storageActivity = getActivity();
        assertNotNull(storageActivity);

        //Reset all application entries
        GNStorageManager.get().resetAppData();

        StorageActivity.StudentInfo studentInfo = new StorageActivity.StudentInfo();
        studentInfo.name = "abcd";
        studentInfo.age = 25;

        storageActivity.setStudentInfo(studentInfo);

        //Close activity. Finish is asynchronous and will only call OnPause after execution of the current method.
        storageActivity.finish();
        setActivity(null);

        //Start Activity again.
        storageActivity = getActivity();

        studentInfo = storageActivity.getStudentInfo();
        assertEquals("abcd", studentInfo.name);
        assertEquals(25, studentInfo.age);

        //Reset all application entries
        GNStorageManager.get().resetAppData();
    }
}
