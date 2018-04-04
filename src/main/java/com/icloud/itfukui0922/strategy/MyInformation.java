package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyInformation {

    /* 自分の役職 */
    Role myRole;
    /* 占い（霊能）結果リスト */
    Map<Agent, Species> divIdenMap = new LinkedHashMap<>();

    public Role getMyRole() {
        return myRole;
    }

    public void setMyRole(Role myRole) {
        this.myRole = myRole;
    }

    public Map<Agent, Species> getDivIdenMap() {
        return divIdenMap;
    }

    public void setDivIdenMap(Map<Agent, Species> divinationMap) {
        this.divIdenMap = divinationMap;
    }
}
