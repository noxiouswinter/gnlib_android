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

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import com.jinais.gnlib.android.app.Launcher.IPayload;
import com.jinais.gnlib.android.app.Launcher.LauncherActivity_1;
import com.jinais.gnlib.android.app.Launcher.LauncherActivity_2;
import com.jinais.gnlib.android.launcher.GNLauncher;

public class GNLauncherTest extends ActivityInstrumentationTestCase2<LauncherActivity_1> {

    LauncherActivity_1 launcherActivity_1;
    LauncherActivity_2 launcherActivity_2;
    ActivityMonitor activityMonitor;

    public GNLauncherTest() {
        super(LauncherActivity_1.class);
    }

    public void testLauncher() {

        launcherActivity_1 = getActivity();
        assertNotNull(launcherActivity_1);

        activityMonitor = new ActivityMonitor(LauncherActivity_2.class.getName(), null, false);
        getInstrumentation().addMonitor(activityMonitor);

        //Launch second activity with payload
        ((IPayload) GNLauncher.get().getProxy(launcherActivity_1, IPayload.class, LauncherActivity_2.class)).sayHello("abcd", 25);

        //Check if second Activity was launched.
        Activity activity = activityMonitor.waitForActivityWithTimeout(10 * 1000);
        assertNotNull(activity);
        assertEquals(LauncherActivity_2.class.getCanonicalName(), activity.getClass().getCanonicalName());

        //Check if the objects were transferred.
        launcherActivity_2 = (LauncherActivity_2) activity;
        assertEquals("abcd", launcherActivity_2.getName());
        assertEquals(25, launcherActivity_2.getAge());
    }
}
