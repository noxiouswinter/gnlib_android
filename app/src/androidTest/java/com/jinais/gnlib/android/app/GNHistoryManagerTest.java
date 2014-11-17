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

import android.content.Context;
import android.test.InstrumentationTestCase;
import com.jinais.gnlib.android.state.GNState;
import com.jinais.gnlib.android.state.GNStateManagerFactory;
import com.jinais.gnlib.android.state.history.GNHistoryManager;
import com.jinais.gnlib.android.state.history.GNHistoryManagerFactory;

/**
 * Created by jkader on 11/11/14.
 */
public class GNHistoryManagerTest extends InstrumentationTestCase {

    Context applicationContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        applicationContext = this.getInstrumentation().getTargetContext().getApplicationContext();
    }

    public void testStepInStepOut() {

        GNHistoryManagerFactory.clearSingleton();
        GNHistoryManager historyManager = GNHistoryManagerFactory.init(applicationContext);

        HistoryManagerClient historyManagerClient = new HistoryManagerClient();
        historyManagerClient.setName("abc");
        historyManagerClient.setAge(25);

        historyManager.addHistory(historyManagerClient);

        historyManagerClient.setName("def");
        historyManagerClient.setAge(30);

        historyManager.stepIntoHistory(1, historyManagerClient);

        assertEquals("abc", historyManagerClient.getName());
        assertEquals(25, historyManagerClient.getAge());

        historyManager.stepOutOfHistory(historyManagerClient);

        assertEquals("def", historyManagerClient.getName());
        assertEquals(30, historyManagerClient.getAge());
    }

    public void testGetHistorySizeAndCurrentPositionInHistory() {

        GNHistoryManagerFactory.clearSingleton();
        GNHistoryManager historyManager = GNHistoryManagerFactory.init(applicationContext);

        HistoryManagerClient historyManagerClient = new HistoryManagerClient();

        //There should be no history before object is added for the first time.
        Integer historySize = historyManager.getHistorySize(historyManager.getClass());
        assertNull(historySize);
        Integer currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertNull(currentPositionInHistory);

        //Add to history
        historyManagerClient.setName("abc");
        historyManagerClient.setAge(25);
        historyManager.addHistory(historyManagerClient);

        //After adding the first entry, size of history should be 1
        historySize = historyManager.getHistorySize(historyManagerClient.getClass());
        assertNotNull(historySize);
        assertEquals(1, historyManager.getHistorySize(historyManagerClient.getClass()).intValue());
        //Current position in history should still be null as we are not stepping through history yet.
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertNull(currentPositionInHistory);
    }

    public void testStepBackStepForwardAndCurrentPositionPositionInHistory() {

        GNHistoryManagerFactory.clearSingleton();
        GNStateManagerFactory.init(applicationContext).resetAppData();
        GNHistoryManager historyManager = GNHistoryManagerFactory.init(applicationContext);

        HistoryManagerClient historyManagerClient = new HistoryManagerClient();
        Integer currentPositionInHistory;

        //Add to history
        historyManagerClient.setName("abc");
        historyManagerClient.setAge(25);
        historyManager.addHistory(historyManagerClient);

        //Add to history
        historyManagerClient.setName("def");
        historyManagerClient.setAge(30);
        historyManager.addHistory(historyManagerClient);

        //Change state but don't add to history. Let's call this initial state.
        historyManagerClient.setName("ghi");
        historyManagerClient.setAge(35);

        //Step back in history. State should switch to second entry. Current position in history should be 2.
        historyManager.stepBackInHistory(historyManagerClient);
        assertEquals("def", historyManagerClient.getName());
        assertEquals(30, historyManagerClient.getAge());
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertEquals(2, currentPositionInHistory.intValue());

        //Step back in history again. State should switch to first entry. Current position in history should be 1.
        historyManager.stepBackInHistory(historyManagerClient);
        assertEquals("abc", historyManagerClient.getName());
        assertEquals(25, historyManagerClient.getAge());
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertEquals(1, currentPositionInHistory.intValue());

        //Step back in history again but the state should still be at first state. Current position in history should still be 1.
        historyManager.stepBackInHistory(historyManagerClient);
        assertEquals("abc", historyManagerClient.getName());
        assertEquals(25, historyManagerClient.getAge());
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertEquals(1, currentPositionInHistory.intValue());

        //Step forward in history. State should switch to second state. Current position in history should still be 2.
        historyManager.stepForwardInHistory(historyManagerClient);
        assertEquals("def", historyManagerClient.getName());
        assertEquals(30, historyManagerClient.getAge());
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertEquals(2, currentPositionInHistory.intValue());

        //Step forward in history. State should step out of history and switch to initial state. Current position in history should be null.
        historyManager.stepForwardInHistory(historyManagerClient);
        assertEquals("ghi", historyManagerClient.getName());
        assertEquals(35, historyManagerClient.getAge());
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertNull(currentPositionInHistory);
    }

    public void testStepIntoStepOutOfHistory() {

        GNHistoryManagerFactory.clearSingleton();
        GNStateManagerFactory.init(applicationContext).resetAppData();
        GNHistoryManager historyManager = GNHistoryManagerFactory.init(applicationContext);

        HistoryManagerClient historyManagerClient = new HistoryManagerClient();
        Integer currentPositionInHistory;

        //Add to history
        historyManagerClient.setName("abc");
        historyManagerClient.setAge(25);
        historyManager.addHistory(historyManagerClient);

        //Add to history
        historyManagerClient.setName("def");
        historyManagerClient.setAge(30);
        historyManager.addHistory(historyManagerClient);

        //Add to history
        historyManagerClient.setName("ghi");
        historyManagerClient.setAge(35);
        historyManager.addHistory(historyManagerClient);

        //Change state but don't add to history. Let's call this initial state.
        historyManagerClient.setName("jkh");
        historyManagerClient.setAge(40);

        //Current position in history should be null.
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertNull(currentPositionInHistory);

        //Size of history should be 3.
        Integer currentHistorySize = historyManager.getHistorySize(historyManagerClient.getClass());
        assertNotNull(currentHistorySize);
        assertEquals(3, currentHistorySize.intValue());

        //Step into the middle entry in history.
        historyManager.stepIntoHistory(2, historyManagerClient);

        //Current position in history should be 2.
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertNotNull(currentPositionInHistory);
        assertEquals(2, currentPositionInHistory.intValue());

        //State should match the second entry in history.
        assertEquals("def", historyManagerClient.getName());
        assertEquals(30, historyManagerClient.getAge() );

        //Step out of history.
        historyManager.stepOutOfHistory(historyManagerClient);

        //Current position in history should be null.
        currentPositionInHistory = historyManager.getCurrentPositionInHistory(historyManagerClient.getClass());
        assertNull(currentPositionInHistory);

        //State should match initial state values.
        assertEquals("jkh", historyManagerClient.getName());
        assertEquals(40, historyManagerClient.getAge() );

    }

    //TODO write tests for clearBefore and ClearAfter methods.
}

class HistoryManagerClient {

    @GNState
    private String name;

    @GNState
    private int age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
