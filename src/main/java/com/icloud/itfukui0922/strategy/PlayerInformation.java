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
    /* 占い師の時の占い結果リスト */

    Map<Agent, Species> divinationMap = new LinkedHashMap<>();
    /* 霊能者の時の霊能結果リスト */
    Map<Agent, Species> identifiedMap = new LinkedHashMap<>();

    public void setEstimateRole(Role role) {
        this.estimateRole = role;
    }

    /**
     * コンストラクタ
     * @param agent
     */
    public PlayerInformation(Agent agent) {
        this.agent = agent;
    }

    /* GETTER */
    public Agent getAgent() {
        return agent;
    }
    public Map<Agent, Species> getDivinationMap() {
        return divinationMap;
    }
    public Role getSelfCO() { return selfCO;}

    /**
     * 自身がCOした役職を保管する
     * @param selfCO
     */
    public void setSelfCO(Role selfCO) {
        Annotation.comingoutCheck(this, selfCO);
        this.selfCO = selfCO;
    }

    /**
     * 占い結果を保存します．
     * @param target 占い対象
     * @param result 占い結果
     */
    public void addDivinationMap (Agent target, Species result) {
        divinationMap.put(target, result);
    }

    /**
     * 霊能結果を保存します．
     *
     * @param target 霊能対象
     * @param result 霊能結果
     */
    public void addIdentifiedMap(Agent target, Species result) {
        identifiedMap.put(target, result);
    }
}
