package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyInformation {

    /* 自分自身 */
    Agent agent;
    /* 自分の役職 */
    Role myRole;
    /* 占い（霊能・騙り）結果リスト */
    Map<Agent, Species> divIdenMap = new LinkedHashMap<>();

    public Role getMyRole() {
        return myRole;
    }

    public void setMyRole(Role myRole) {
        this.myRole = myRole;
    }

    public Agent getAgent() {
        return agent;
    }

    /**
     * コンストラクタ
     * @param agent
     */
    public MyInformation(Agent agent) {
        this.agent = agent;
    }

    /**
     * 占い霊能結果の追加
     * @param target
     * @param result
     */
    public void putDivIdenMap(Agent target, Species result) {
        divIdenMap.put(target, result);
    }

    /**
     * 占い霊能結果の最後の結果を返します
     * @return 占い結果をMap.Entryで返します．
     *         リストがnullの場合nullを返します．
     */
    public Map.Entry<Agent, Species> peekDivIdenMap() {
        if (divIdenMap == null) {
            return null;
        }

        Map.Entry<Agent, Species> lastSet = null;
        for (Map.Entry<Agent, Species> set :
                divIdenMap.entrySet()) {
            lastSet = set;
        }
        return lastSet;
    }
}
