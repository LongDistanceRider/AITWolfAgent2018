
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;

import java.util.ArrayList;
import java.util.List;

public class PlayerInformation {

    /* Agent情報 */
    Agent agent;
    /* comingoutした役職 */
    Role comingoutRole;
    /* 自分が予測した役職 */
    Role estimateRole;
    /* 占い師の時の占い結果リスト */
    List<Judge> divinationList = new ArrayList<>();

    public PlayerInformation(Agent agent) {
        this.agent = agent;
    }


    /**
     * 占い結果を保存します．
     * このメソッドはBoardSurfaceからのみ利用されることを想定します．
     * @param divination
     */
    public void addDivinationList (Judge divination) {
        divinationList.add(divination);
    }


}
