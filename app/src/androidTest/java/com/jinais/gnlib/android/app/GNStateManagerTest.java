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

package com.jinais.gnlib.android.app;

import android.test.ActivityInstrumentationTestCase2;
import com.jinais.gnlib.android.app.Storage.StorageActivity;
import com.jinais.gnlib.android.storage.GNStateManager;

/**
 * Created by jkader on 10/29/14.
 */
public class GNStateManagerTest extends ActivityInstrumentationTestCase2<StorageActivity> {

    StorageActivity storageActivity;

    public GNStateManagerTest() {super(StorageActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testStorageAndRetrieval() {

        //Start Activity.
        storageActivity = getActivity();
        assertNotNull(storageActivity);

        //Reset all application entries
        GNStateManager.get().resetAppData();

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
        GNStateManager.get().resetAppData();
    }

    /*
      Yet to implement tests for
      getAllPersistedClassCannonicalNames
      remove
      resetAppData
    * */
}
