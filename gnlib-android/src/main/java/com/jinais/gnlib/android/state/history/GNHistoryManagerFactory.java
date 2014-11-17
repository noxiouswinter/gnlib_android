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

package com.jinais.gnlib.android.state.history;

/**
 * Created by jkader on 11/11/14.
 */
public class GNHistoryManagerFactory {

    private static GNHistoryManager gnHistoryManager = null;

    public static GNHistoryManager get() {
        if(gnHistoryManager == null) {
            gnHistoryManager = new GNHistoryManagerImpl();
        }
        return gnHistoryManager;
    }

    //Private Constructor.
    private GNHistoryManagerFactory() {}

    public static void clearSingleton() {
        gnHistoryManager = null;
    }
}
