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

import com.jinais.gnlib.android.LogGN;
import com.jinais.gnlib.android.state.GNStateGsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkader on 11/11/14.
 */
public class GNHistoryManagerImpl implements GNHistoryManager {

    Map<String, HistoryInfo> historyInfoLists;
    GNStateGsonHelper gnStateGsonHelper;

    public GNHistoryManagerImpl() {
        this.historyInfoLists = new HashMap<String, HistoryInfo>();
        gnStateGsonHelper = new GNStateGsonHelper();
    }

    /**@inheritDoc*/
    @Override
    public void addHistory(Object object) {
        if(object == null) {
            LogGN.e("HistoryManager.addHistory: object cannot be null.");
            return;
        }
        String objectClassCanonicalName = object.getClass().getCanonicalName();
        //If the class has no entry in the map of lists, create.
        if(!historyInfoLists.containsKey(objectClassCanonicalName)) {
            HistoryInfo historyInfo = new HistoryInfo(new ArrayList<StateInfo>());
            historyInfoLists.put(objectClassCanonicalName, historyInfo);
        }
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        List<StateInfo> stateInfos = historyInfo.getStateInfos();
        String objectStateJsonString = gnStateGsonHelper.getStateJsonString(object);
        stateInfos.add(new StateInfo(objectStateJsonString));
    }

    /**@inheritDoc*/
    @Override
    public Integer getHistorySize(Class objectClass) {
        String objectClassCanonicalName = objectClass.getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.getHistorySize: the class does not have any record in historyInfoLists.");
            return null;
        }
        List<StateInfo> stateInfoList = historyInfo.getStateInfos();
        return stateInfoList.size();
    }

    /**@inheritDoc*/
    @Override
    public void stepIntoHistory(int position, Object object) {
        if(object == null) {
            LogGN.e("HistoryManager.getHistory: object cannot be null.");
            return;
        }
        String objectClassCanonicalName = object.getClass().getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.getHistory: the class does not have any record in historyInfoLists.");
            return;
        }

        List<StateInfo> stateInfoList = historyInfo.getStateInfos();
        StateInfo stateInfo = null;
        try {
            stateInfo = stateInfoList.get(position - 1);
        } catch(IndexOutOfBoundsException e) {
            LogGN.e(e);
        }
        if(stateInfo == null) {
            LogGN.e("GNHistoryManagerImpl.getHistory: historyInfo for the index is null.");
        }
        //Cache current state.
        historyInfo.setTempCurrentStateInfo(new StateInfo(gnStateGsonHelper.getStateJsonString(object)));

        String objectStateJsonString = stateInfo.getJsonString();
        gnStateGsonHelper.injectStateFromJson(object, objectStateJsonString);
        historyInfo.setCurrentPositionInHistory(position);
    }

    /**@inheritDoc*/
    @Override
    public void stepOutOfHistory(Object object) {
        String objectClassCanonicalName = object.getClass().getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.stepOutOfHistory: the class does not have any record in historyInfoLists.");
            return;
        }
        if(historyInfo.getCurrentPositionInHistory() == null || historyInfo.getTempCurrentStateInfo() == null) {
            LogGN.e("Currently not in history.");
        }
        //Set the state back from cachedTempState.
        gnStateGsonHelper.injectStateFromJson(object, historyInfo.getTempCurrentStateInfo().getJsonString());
        historyInfo.setCurrentPositionInHistory(null);
    }

    /**@inheritDoc*/
    @Override
    public boolean stepBackInHistory(Object object) {
        if(object == null) {
            LogGN.e("HistoryManager.stepBackInHistory: object cannot be null.");
            return false;
        }
        String objectClassCanonicalName = object.getClass().getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.stepBackInHistory: the class does not have any record in historyInfoLists.");
            return false;
        }
        List<StateInfo> stateInfos = historyInfo.getStateInfos();
        Integer currentPositionInHistory = historyInfo.getCurrentPositionInHistory();

        //Check if at the beginning of the list. If yes, return false.
        if(currentPositionInHistory == null  && stateInfos.size() == 0) {
            LogGN.d("GNHistoryManager.stepBackInHistory: No history list is empty. Nothing to step back to.");
            return false;
        }
        if(currentPositionInHistory != null && currentPositionInHistory.equals(1)){
            LogGN.d("GNHistoryManager.stepBackInHistory: State is at beginning of History. Nothing to step back to.");
            return false;
        }

        //If currently not in history.
        if(currentPositionInHistory == null) {
            //Temporarily add current state to history.
            historyInfo.setTempCurrentStateInfo(new StateInfo(gnStateGsonHelper.getStateJsonString(object)));
            //Set state of the object to the last entry.
            StateInfo stateInfo =  stateInfos.get(stateInfos.size() - 1);
            gnStateGsonHelper.injectStateFromJson(object, stateInfo.getJsonString());
            historyInfo.setCurrentPositionInHistory(stateInfos.size());
            return true;
        }

        //If currently stepping through history.
        //Step back, currentPositionInHistory--
        if(currentPositionInHistory != null  && !currentPositionInHistory.equals(1)) {
            StateInfo stateInfoOneBeforeCurrent =  stateInfos.get(currentPositionInHistory - 2);
            gnStateGsonHelper.injectStateFromJson(object, stateInfoOneBeforeCurrent.getJsonString());
            historyInfo.setCurrentPositionInHistory(currentPositionInHistory-1);
            return true;
        }
        return false;
    }

    /**@inheritDoc*/
    @Override
    public boolean stepForwardInHistory(Object object) {
        if(object == null) {
            LogGN.e("HistoryManager.stepForwardInHistory: object cannot be null.");
            return false;
        }
        String objectClassCanonicalName = object.getClass().getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.stepForwardInHistory: the class does not have any record in historyInfoLists.");
            return false;
        }
        List<StateInfo> stateInfos = historyInfo.getStateInfos();

        //Check if currentPositionInHistory == null. If yes, return false.
        if(historyInfo.getCurrentPositionInHistory() == null) {
            LogGN.d("GNHistoryManager.stepForwardInHistory: Currently not in history. Can't step forward.");
            return false;
        }

        //If if at last element in the history, set state with tempCurrentStateInfo,
        // set tempCurrentStateInfo and currentPositionInHistory to null.
        if(historyInfo.getCurrentPositionInHistory().equals(stateInfos.size())) {
            LogGN.d("HistoryManager.stepForwardInHistory: Reached end of history. Stepping out of history.");
            gnStateGsonHelper.injectStateFromJson(object, historyInfo.getTempCurrentStateInfo().getJsonString());
            historyInfo.setTempCurrentStateInfo(null);
            historyInfo.setCurrentPositionInHistory(null);
            return true;
        }

        //Else step forward, currentPositionInHistory++ Check if currentPositionInHistory == sizeOfHistory. If yes, set currentPositionInHistory = null, return true.
        StateInfo stateInfoOneAfterCurrent =  stateInfos.get(historyInfo.getCurrentPositionInHistory());
        gnStateGsonHelper.injectStateFromJson(object, stateInfoOneAfterCurrent.getJsonString());
        historyInfo.setCurrentPositionInHistory(historyInfo.getCurrentPositionInHistory() + 1);
        return true;
    }

    /**@inheritDoc*/
    @Override
    public Integer getCurrentPositionInHistory(Class objectClass) {
        String objectClassCanonicalName = objectClass.getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.getCurrentPositionInHistory: the class does not have any record in historyInfoLists.");
            return null;
        }
        return historyInfo.getCurrentPositionInHistory();
    }

    /**@inheritDoc*/
    @Override
    public Boolean clearHistory(Class objectClass) {
        String objectClassCanonicalName = objectClass.getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.clearHistory: the class does not have any record in historyInfoLists.");
            return false;
        }
        historyInfoLists.remove(objectClassCanonicalName);
        return true;
    }

    /**@inheritDoc*/
    @Override
    public boolean clearHistoryBefore(int position, Class objectClass) {
        String objectClassCanonicalName = objectClass.getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.clearHistoryBefore: the class does not have any record in historyInfoLists.");
            return false;
        }

        List<StateInfo> stateInfos = historyInfo.getStateInfos();
        if(stateInfos == null) {
            LogGN.d("GNHistoryManager.clearHistoryBefore: stateInfos is null");
            return false;
        }

        if(position < 2 || position > stateInfos.size()) {
            LogGN.d("GNHistoryManager.clearHistoryBefore: position has to be between 2 and the current history size inclusive.");
            return false;
        }

        List<StateInfo> tempStateInfos = new ArrayList<StateInfo>();
        for(int i = position-1; i < stateInfos.size(); i++) {
            tempStateInfos.add(stateInfos.get(i));
        }
        //Replace with the new list.
        historyInfo.setStateInfos(tempStateInfos);
        return true;
    }

    /**@inheritDoc*/
    @Override
    public boolean clearHistoryAfter(int position, Class objectClass) {
        String objectClassCanonicalName = objectClass.getCanonicalName();
        HistoryInfo historyInfo = historyInfoLists.get(objectClassCanonicalName);
        if(historyInfo == null) {
            LogGN.d("GNHistoryManager.clearHistoryAfter: the class does not have any record in historyInfoLists.");
            return false;
        }

        List<StateInfo> stateInfos = historyInfo.getStateInfos();
        if(stateInfos == null) {
            LogGN.d("GNHistoryManager.clearHistoryAfter: stateInfos is null");
            return false;
        }

        if(position < 1 || position >= stateInfos.size()) {
            LogGN.e("GNHistoryManager.clearHistoryAfter: position has to be between 1 and the current history size-1 inclusive.");
            return false;
        }

        List<StateInfo> tempStateInfos = new ArrayList<StateInfo>();
        for(int i = 0; i < position; i++) {
            tempStateInfos.add(stateInfos.get(i));
        }
        //Replace with the new list.
        historyInfo.setStateInfos(tempStateInfos);
        return true;
    }
}

class StateInfo {

    String jsonString;

    StateInfo(String jsonString) {
        this.jsonString = jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
    public String getJsonString() {
        return jsonString;
    }
}

class HistoryInfo {

    List<StateInfo> stateInfos;
    Integer currentPositionInHistory;
    StateInfo tempCurrentStateInfo;

    HistoryInfo(List<StateInfo> stateInfos) {
        this.stateInfos = stateInfos;

    }

    public Integer getCurrentPositionInHistory() {
        return currentPositionInHistory;
    }
    public void setCurrentPositionInHistory(Integer currentPositionInHistory) {
        this.currentPositionInHistory = currentPositionInHistory;
    }
    public List<StateInfo> getStateInfos() {
        return stateInfos;
    }
    public void setStateInfos(List<StateInfo> stateInfos) {
        this.stateInfos = stateInfos;
    }
    public StateInfo getTempCurrentStateInfo() {
        return tempCurrentStateInfo;
    }
    public void setTempCurrentStateInfo(StateInfo tempCurrentStateInfo) {
        this.tempCurrentStateInfo = tempCurrentStateInfo;
    }
}
