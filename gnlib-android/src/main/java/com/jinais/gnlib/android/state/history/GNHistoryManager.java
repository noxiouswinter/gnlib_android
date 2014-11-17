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
 * Created by jkader on 11/10/14.
 */
public interface GNHistoryManager {

    /**@param object object, the state of which should be added to the history.*/
    public void addHistory(Object object);

    /**@param objectClass, The class with the history
     * @return Size of current history for this class.*/
    public Integer getHistorySize(Class objectClass);

    /**
     * @param position Position in history [1 - size].
     * @param object Object to which the history state should be injected into. */
    public void stepIntoHistory(int position, Object object);

    /**Sets state from tempCachedState and sets CurrentPositionInHistory to null.
     * {@link com.jinais.gnlib.android.state.history.GNHistoryManager#stepBackInHistory(Object)}
     * will start at the end of the history next time it is called.*/
    public void stepOutOfHistory(Object object);

    /**@param object Object to inject state from history into.
     * @return true if did step back. False if already at beginning of list.
     * Current state will be added to history temporarily if not currently stepping through history.*/
    public boolean stepBackInHistory(Object object);

    /**@param object Object to inject state from history into.
     * @return true if did step forward. False if already at the end of list.*/
    public boolean stepForwardInHistory(Object object);

    /**@return Current position in history where the state of the object is at.
     * this will be null when the state of the object is current.*/
    public Integer getCurrentPositionInHistory(Class objectClass);

    /**@return true if an entry was removed.
     * Removes all history entries of the given class. */
    public Boolean clearHistory(Class objectClass);

    /**
     * @param position position in history.
     * Makes state at the current position the first element the class's history.
     * Removes everything in history from before here.  */
    public boolean clearHistoryBefore(int position, Class objectClass);

    /**@param position position in history.
     * Makes the current position state the present state of the class. Clears
     * everything in history from after here.  */
    public boolean clearHistoryAfter(int position, Class objectClass);
}
