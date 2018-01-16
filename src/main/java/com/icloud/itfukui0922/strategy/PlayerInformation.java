/**
 * このクラスはBoardSurfeceからのみアクセスする
 * なぜなら，Q学習のためにBoardSurfaceに統一するため
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerInformation {

    public Agent getAgent() {
        return agent;
    }

    /* Agent情報 */
    Agent agent;
    /* comingoutした役職 */
    Role selfCO;
    /* 自分が予測した役職 */
    Role estimateRole;
    /* 占い師の時の占い結果リスト */
    Map<Agent, Species> divinationMap = new LinkedHashMap<>();

    public PlayerInformation(Agent agent) {
        this.agent = agent;
    }

    public Map<Agent, Species> getDivinationList() {
        return divinationMap;
    }

    /**
     * 占い結果を保存します．
     * このメソッドはBoardSurfaceからのみ利用されることを想定します．
     * @param agent
     * @param species
     */
    public void addDivinationMap (Agent agent, Species species) {
        divinationMap.put(agent, species);
    }


    /**
     * 自身がCOした役職を保管する
     * @param selfCO
     */
    public void setSelfCO(Role selfCO) {
        this.selfCO = selfCO;
    }

    /**
     * 霊能結果を保存します．
     * このメソッドはBoardSurface
     * @param target
     * @param result
     */
    public void addIdentifiedMap(Agent target, Species result) {

    }
}
