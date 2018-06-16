package com.icloud.itfukui0922.strategy;

import com.icloud.itfukui0922.log.Log;
import org.aiwolf.common.data.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 15人人狼での人狼グループ構造体
 */
public class WolfGroup {

    List<Agent> agentList = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public WolfGroup(List<Agent> agentList) {
         this.agentList = agentList;
    }

    /**
     * あるエージェントがグループ内にいるかを返す
     * @param target
     * @return
     */
    public boolean inAgent(Agent target) {
        for (Agent agent :
                agentList) {
            if (agent.getAgentIdx() == target.getAgentIdx()) {
                return true;
            }
        }
        return false;
    }
}
