package com.jinais.gnlib.android.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jinais.gnlib.android.LogM;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GNStorageManager {

    //Singleton Instance
    private static GNStorageManager sharedInstance = null;
    private final Context context;
    private final String applicationId;

    @GNState
    private ArrayList<StatePersistenceInfo> statePersistenceInfos = null;

    public static GNStorageManager init(Context context) {

        if(sharedInstance == null) {
            sharedInstance = new GNStorageManager(context);

            //Retrieve the state of GNStorageManager
            getSharedInstance().retrieve(getSharedInstance());

            return getSharedInstance();
        } else {
            LogM.e("GNStorageManager already init. Use get to get the singleton.");
            return null;
        }
    }

    public static GNStorageManager getSharedInstance() {
        if(sharedInstance == null) {
            LogM.e("GNStorageManager: Call init with context first.");
            return null;
        }
        return sharedInstance;
    }

    //private Constructor
    private GNStorageManager(Context context) {
        this.context = context;
        this.applicationId = context.getApplicationContext().getPackageName();
        statePersistenceInfos = new ArrayList<>();
    }
    
    /** Jsonify the object and store in SharedPreferences with the Object's class name. */
    public void store(Object object) {
        Class objectClass = object.getClass();
        Gson gson = new GsonBuilder()
            .setExclusionStrategies(new GNStateGsonExclusionStrategy())
            .create();
        String jsonString = gson.toJson(object, objectClass);

		SharedPreferences sharedPreferences = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(objectClass.getCanonicalName(), jsonString).apply();

        //Store the state of GNStorageManager if StatePersistenceInfos was modified.
        if(addToStatePersistenceInfos(objectClass.getCanonicalName())) {
            getSharedInstance().store(getSharedInstance());
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
    public void retrieve(Object object) {
        Class objectClass = object.getClass();
        String objectClassCanonicalName = objectClass.getCanonicalName();

		SharedPreferences sharedPreferences = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE);
		String jsonString = sharedPreferences.getString(objectClassCanonicalName, "");
        if(jsonString.equals("")) return;

        Gson gson = new Gson();
        Object duplicateObject = gson.fromJson(jsonString, objectClass);

        try {
            copyObjectAnnotatedFields(duplicateObject, object);
        } catch (IllegalAccessException e) {
            LogM.e(e);
        }
    }

    /** Clear all GNStorageManager's data from SharedPreferences. */
    public void clearAll() {
        context.getSharedPreferences(applicationId, Context.MODE_PRIVATE).edit().clear().commit();
    }

    /** Remove data of provided object's class from GNStorageManager and the SharedPrefs entries. */
    public void remove(Object object) {
        Class componentClass = object.getClass();
        String componentClassCanonicalName =  componentClass.getCanonicalName();
        int indexInStatePersistenceInfos = indexInPersistenceInfos(componentClassCanonicalName);

        if(indexInStatePersistenceInfos != -1) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE);
            sharedPreferences.edit().remove(componentClassCanonicalName).commit();
            statePersistenceInfos.remove(indexInStatePersistenceInfos);
            store(getSharedInstance());

        } else {
            LogM.d("StorageManager.remove: ", componentClass.getCanonicalName(), " is not present in statePersistenceInfos");
        }
    }

    public List<String> getAllPersistedClassCannonicalNames() {
        List<String> persistedClassCannonicalNames = new ArrayList<String>();
        for(StatePersistenceInfo statePersistenceInfo: statePersistenceInfos) {
            persistedClassCannonicalNames.add(statePersistenceInfo.getComponentClassCanonicalName());
        }
        return persistedClassCannonicalNames;
    }

    /** Copies fields marked with @GNState from first object to the second. All custom classes and their state fields should
     * be marked with the annotation too.*/
    private void copyObjectAnnotatedFields(Object objectToCopyFrom, Object objectToCopyTo) throws IllegalAccessException {

        Class classOfObjectToCopyFrom = objectToCopyFrom.getClass();
        if(!classOfObjectToCopyFrom.equals(objectToCopyTo.getClass())) {
            LogM.e("Classes of Objects to copy does not match.");
            return;
        }

        for(Field field: classOfObjectToCopyFrom.getDeclaredFields()) {
            if(field.getAnnotation(GNState.class) != null) {

                //If class(Type) of field is marked with GNState annotation, drill down.
                if(field.getType().getAnnotation(GNState.class) != null) {
                    copyObjectAnnotatedFields(field.get(objectToCopyFrom), field.get(objectToCopyTo));
                } else { //Else Just copy the reference
                    field.setAccessible(true);
                    Object fieldObjectToCopyFrom = field.get(objectToCopyFrom);
                    field.set(objectToCopyTo, fieldObjectToCopyFrom);
                    field.setAccessible(false);
                }
            }
        }
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