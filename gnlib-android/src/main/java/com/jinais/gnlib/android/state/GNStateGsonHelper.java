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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jinais.gnlib.android.LogGN;

import java.lang.reflect.Field;

/**
 * Created by jkader on 11/10/14.
 */
public class GNStateGsonHelper {

    public String getStateJsonString(Object object) {
        String jsonString = null;
        Class objectClass = object.getClass();
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new GNStateGsonExclusionStrategy())
                .create();
        jsonString = gson.toJson(object, objectClass);
        return jsonString;
    }

    public void injectStateFromJson(Object object, String jsonString) {

        Class objectClass = object.getClass();

        Gson gson = new Gson();
        Object duplicateObject;
        try {
            duplicateObject = gson.fromJson(jsonString, objectClass);
        } catch (JsonSyntaxException e) {
            LogGN.e(e, "Failed to parse Json String: ", jsonString);
            return;
        }

        try {
            copyObjectAnnotatedFields(duplicateObject, object);
        } catch (IllegalAccessException e) {
            LogGN.e(e);
        }
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
}
