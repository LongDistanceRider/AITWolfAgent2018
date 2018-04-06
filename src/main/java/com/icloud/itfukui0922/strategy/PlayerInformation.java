/**
 * このクラスはBoardSurfeceからのみアクセスする
 * なぜなら，Q学習のためにBoardSurfaceに統一するため
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import java.util.*;

public class PlayerInformation {

    /* Agent情報 */
    Agent agent;
    /* comingoutした役職 */
    Role selfCO;
    /* 自分が予測した役職 */
    Role estimateRole;
    /* 確信した役職 */
    Role convictionRole;
    /* 他プレイヤーへの印象 */
    Map<Agent, Role> estimateOtherAgentRole;
    /* 占い（霊能）結果リスト */
    Map<Agent, Species> divIdenMap;
    /* 護衛結果リスト */
    List<Agent> guardList;
    /* 占い・護衛予定Agent */
    Agent divGuardPlanAgent;
    /* 投票予定Agent */
    Agent votePlanAgent;
    /* 襲撃予定Agent */
    Agent attackPlanAgent;

    public Agent getAttackPlanAgent() {
        return attackPlanAgent;
    }

    public void setAttackPlanAgent(Agent attackPlanAgent) {
        this.attackPlanAgent = attackPlanAgent;
    }

    public Agent getVotePlanAgent() {
        return votePlanAgent;
    }

    public void setVotePlanAgent(Agent votePlanAgent) {
        this.votePlanAgent = votePlanAgent;
    }

    public Agent getDivGuardPlanAgent() {
        return divGuardPlanAgent;
    }

    public void setDivGuardPlanAgent(Agent divGuardPlanAgent) {
        this.divGuardPlanAgent = divGuardPlanAgent;
    }

    public Agent getAgent() {
        return agent;
    }

    public Map<Agent, Species> getDivIdenMap() {
        return divIdenMap;
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
        this.estimateOtherAgentRole = new HashMap<>();
        this.divIdenMap = new LinkedHashMap<>();
        this.guardList = new LinkedList<>();
    }

    /**
     * 占い結果リストへ追加
     */
    public void putDivIdenMap (Agent target, Species species) {
        divIdenMap.put(target, species);
    }

    /**
     * 他プレイヤーへの印象（情報）の入力
     */

    public void estimateOtherAgent(Agent agent, Role estimateRole) {
        estimateOtherAgentRole.put(agent, estimateRole);
    }

    public void addGuardList(Agent target) {
        guardList.add(target);
    }
}
