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
import com.jinais.gnlib.android.LogGN;

public class GNStateManagerFactory {

    //Singleton Instance
    private static GNStateManagerSharedPrefsImpl sharedInstance = null;

    /** Get Singleton */
    public static GNStateManager init(Context context) {

        if(sharedInstance == null) {
            sharedInstance = new GNStateManagerSharedPrefsImpl(context);
            return sharedInstance;
        } else {
            LogGN.e("GNStateManagerFactory already init. Use get() to get the GNStateManager");
            return null;
        }
    }

    public static GNStateManager get() {
        if(sharedInstance == null) {
            LogGN.e("GNStateManagerFactory: Call init with context first.");
            return null;
        }
        return sharedInstance;
    }
}