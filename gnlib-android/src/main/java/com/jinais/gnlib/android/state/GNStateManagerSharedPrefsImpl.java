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

package com.jinais.gnlib.android.state;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jinais.gnlib.android.LogGN;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkader on 10/31/14.
 */
public class GNStateManagerSharedPrefsImpl implements GNStateManager {

    private final String gnStateManagerSharedPreferenceId;
    private final Context context;

    private final GNStateGsonHelper gnStateGsonHelper;

    @GNState
    private ArrayList<StatePersistenceInfo> statePersistenceInfos = null;

    public GNStateManagerSharedPrefsImpl(Context context) {
        this.context = context;
        this.gnStateManagerSharedPreferenceId = "com.jinais.gnlib.android.gnstatemanager:" + context.getApplicationContext().getPackageName();
        statePersistenceInfos = new ArrayList<StatePersistenceInfo>();
        gnStateGsonHelper = new GNStateGsonHelper();
        //Retrieve the state of GNStateManagerImpl
        retrieve(this);
    }

    /** Jsonify the object and store in SharedPreferences with the Object's class name. */
    @Override
    public void store(Object object) {
        Class objectClass = object.getClass();
        String jsonString = gnStateGsonHelper.getStateJsonString(object);

        SharedPreferences sharedPreferences = context.getSharedPreferences(gnStateManagerSharedPreferenceId, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(objectClass.getCanonicalName(), jsonString).apply();

        //Store the state of GNStateManager if StatePersistenceInfos was modified.
        if(addToStatePersistenceInfos(objectClass.getCanonicalName())) {
            store(this);
        }
    }

    /** @return true if StatePersistenceInfos was modified. */
    private boolean addToStatePersistenceInfos(String objectClassCannonicalName) {
        if(indexInPersistenceInfos(objectClassCannonicalName) == -1) {
            statePersistenceInfos.add(new StatePersistenceInfo(objectClassCannonicalName));
            return true;
        }
        return false;
    }

    /** @return Index of object if found. Returns -1 if not found. */
    private int indexInPersistenceInfos(String componentClassCanonicalName) {
        for(StatePersistenceInfo statePersistenceInfo : statePersistenceInfos) {
            String storedComponetntClassCanonicalName = statePersistenceInfo.componentClassCanonicalName;
            if(storedComponetntClassCanonicalName.equals(componentClassCanonicalName)) {
                return statePersistenceInfos.indexOf(statePersistenceInfo);
            }
        }
        return -1;
    }

    /** Get the JSON from SharedPreferences with the Object's class name. Create a dummy object and copy all the
     Jsonified values to the original object. */
    @Override
    public void retrieve(Object objectToRetrieve) {
        if(objectToRetrieve == null) {
            LogGN.e("Object to retrieve is null!");
            return;
        }

        Class objectClass = objectToRetrieve.getClass();
        String objectClassCanonicalName = objectClass.getCanonicalName();

        SharedPreferences sharedPreferences = context.getSharedPreferences(gnStateManagerSharedPreferenceId, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(objectClassCanonicalName, "");
        if(jsonString.equals("")) return;

        gnStateGsonHelper.injectStateFromJson(objectToRetrieve, jsonString);
    }

    /** Clear all GNStateManager's data from SharedPreferences. */
    @Override
    public void resetAppData() {
        context.getSharedPreferences(gnStateManagerSharedPreferenceId, Context.MODE_PRIVATE).edit().clear().apply();
    }

    /** Remove data of provided object's class from GNStateManager and the SharedPrefs entries. */
    @Override
    public void remove(Class objectClass) {

        //TODO  this might be buggy. Testing required.

        String componentClassCanonicalName =  objectClass.getCanonicalName();
        int indexInStatePersistenceInfos = indexInPersistenceInfos(componentClassCanonicalName);

        if(indexInStatePersistenceInfos != -1) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(gnStateManagerSharedPreferenceId, Context.MODE_PRIVATE);
            sharedPreferences.edit().remove(componentClassCanonicalName).apply();
            statePersistenceInfos.remove(indexInStatePersistenceInfos);
            store(this);

        } else {
            LogGN.d("GNStateManager.remove: ", objectClass.getCanonicalName(), " is not present in statePersistenceInfos");
        }
    }

    @Override
    public List<String> getAllPersistedClassCannonicalNames() {
        List<String> persistedClassCannonicalNames = new ArrayList<String>();
        for(StatePersistenceInfo statePersistenceInfo: statePersistenceInfos) {
            persistedClassCannonicalNames.add(statePersistenceInfo.getComponentClassCanonicalName());
        }
        return persistedClassCannonicalNames;
    }

    @GNState
    public static class StatePersistenceInfo {
        @GNState
        private String componentClassCanonicalName = null;
        public StatePersistenceInfo(String componentClassCanonicalName) {
            this.componentClassCanonicalName = componentClassCanonicalName;
        }
        public String getComponentClassCanonicalName() {
            return componentClassCanonicalName;
        }
    }
}
