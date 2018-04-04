/**
 * このクラスはBoardSurfeceからのみアクセスする
 * なぜなら，Q学習のためにBoardSurfaceに統一するため
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerInformation {

    /* Agent情報 */
    Agent agent;
    /* comingoutした役職 */
    Role selfCO;
    /* 自分が予測した役職 */
    Role estimateRole;
    /* 確信した役職 */
    Role convictionRole;

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Role getSelfCO() {
        return selfCO;
    }

    public void setSelfCO(Role selfCO) {
        this.selfCO = selfCO;
    }

    public Role getEstimateRole() {
        return estimateRole;
    }

    public void setEstimateRole(Role estimateRole) {
        this.estimateRole = estimateRole;
    }

    public Role getConvictionRole() {
        return convictionRole;
    }

    public void setConvictionRole(Role convictionRole) {
        this.convictionRole = convictionRole;
    }

    /**
     * コンストラクタ
     * @param agent
     */
    public PlayerInformation(Agent agent) {
        this.agent = agent;
    }
}
