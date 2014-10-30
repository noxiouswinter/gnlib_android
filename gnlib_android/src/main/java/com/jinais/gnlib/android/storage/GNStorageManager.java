package com.jinais.gnlib.android.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jinais.gnlib.android.LogGN;
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

    /** Get Singleton */
    public static GNStorageManager init(Context context) {

        if(sharedInstance == null) {
            sharedInstance = new GNStorageManager(context);

            //Retrieve the state of GNStorageManager
            get().retrieve(get());

            return get();
        } else {
            LogGN.e("GNStorageManager already init. Use get to get the singleton.");
            return null;
        }
    }

    public static GNStorageManager get() {
        if(sharedInstance == null) {
            LogGN.e("GNStorageManager: Call init with context first.");
            return null;
        }
        return sharedInstance;
    }

    /** Private Constructor */
    private GNStorageManager(Context context) {
        this.context = context;
        this.applicationId = context.getApplicationContext().getPackageName();
        statePersistenceInfos = new ArrayList<StatePersistenceInfo>();
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
    public void retrieve(Object objectToRetrieve) {
        if(objectToRetrieve == null) {
            LogGN.e("Object to retrieve is null!");
            return;
        }

        Class objectClass = objectToRetrieve.getClass();
        String objectClassCanonicalName = objectClass.getCanonicalName();

		SharedPreferences sharedPreferences = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE);
		String jsonString = sharedPreferences.getString(objectClassCanonicalName, "");
        if(jsonString.equals("")) return;

        Gson gson = new Gson();
        Object duplicateObject;
        try {
            duplicateObject = gson.fromJson(jsonString, objectClass);
        } catch (JsonSyntaxException e) {
            LogGN.e(e, "Failed to parse Json String: ", jsonString);
            return;
        }

        try {
            copyObjectAnnotatedFields(duplicateObject, objectToRetrieve);
        } catch (IllegalAccessException e) {
            LogGN.e(e);
        }
    }

    /** Clear all GNStorageManager's data from SharedPreferences. */
    public void resetAppData() {
        context.getSharedPreferences(applicationId, Context.MODE_PRIVATE).edit().clear().apply();
    }

    /** Remove data of provided object's class from GNStorageManager and the SharedPrefs entries. */
    public void remove(Object object) {
        Class componentClass = object.getClass();
        String componentClassCanonicalName =  componentClass.getCanonicalName();
        int indexInStatePersistenceInfos = indexInPersistenceInfos(componentClassCanonicalName);

        if(indexInStatePersistenceInfos != -1) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(applicationId, Context.MODE_PRIVATE);
            sharedPreferences.edit().remove(componentClassCanonicalName).apply();
            statePersistenceInfos.remove(indexInStatePersistenceInfos);
            store(this);

        } else {
            LogGN.d("StorageManager.remove: ", componentClass.getCanonicalName(), " is not present in statePersistenceInfos");
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

        if(objectToCopyFrom == null) {
            LogGN.e("objectToCopyFrom is null!");
            return;
        }

        for(Field field: objectToCopyFrom.getClass().getDeclaredFields()) {
            //If Field is annotated with GNState,
            if(field.getAnnotation(GNState.class) != null) {

                //If class(Type) of field is marked with GNState annotation, drill down.
                if(field.getType().getAnnotation(GNState.class) != null) {
                    //Override field access restrictions
                    field.setAccessible(true);

                    //If the field is null, instantiate an object of it's class and assign the field with it.
                    if(field.get(objectToCopyTo) == null) {
                        LogGN.d("Field ", field.getName(), " is null. Instantiating..");
                        Object newFieldObject;
                        try {
                            newFieldObject = field.getType().newInstance();
                        } catch (InstantiationException e) {
                            LogGN.e(e, "Instantiation of Field Type ", field.getType(), " for field ", field.getName(), " failed!");
                            return;
                        }
                        if(newFieldObject != null) {
                            field.set(objectToCopyTo, newFieldObject);
                        }
                    }
                    //Recurse.
                    copyObjectAnnotatedFields(field.get(objectToCopyFrom), field.get(objectToCopyTo));
                    //Put back access restrictions for the field.
                    field.setAccessible(false);

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