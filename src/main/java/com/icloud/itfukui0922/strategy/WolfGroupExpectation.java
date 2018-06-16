package com.icloud.itfukui0922.strategy;

import com.icloud.itfukui0922.log.Log;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人狼グループ予想
 */
public class WolfGroupExpectation {

    /* ---------------------------------------------------
     ここに人狼グループ推定に必要な定数を定義しておく
     今後の予定では機械学習にて定数を調整していく
     ---------------------------------------------------*/
    public final static int WOLF_OR_POSESSED = 1000;    // 人狼か狂人（自分に黒出しした占い師など）
    // ---------------------------------------------------
    /* 初期化フラグ */
    private static boolean isInit = false;
    /* 人狼グループ */
    Map<WolfGroup, Boolean> wolfGroupMap = new HashMap<>();
    /* 自分以外のAgents */
    List<Agent> agentList = new ArrayList<>();

    /* Agentの黒評価指数 */
    Map<Agent, Integer> agentLier = new HashMap<>();

    public void putWolfGroupMap(WolfGroup wolfGroup, Boolean boo) {
        wolfGroupMap.put(wolfGroup, boo);
    }

    /* ▼ singleton ▼ */
    private static WolfGroupExpectation wolfGroupExpectation= new WolfGroupExpectation();
    private WolfGroupExpectation(){}
    public static WolfGroupExpectation getInstance() {
        if (!isInit) {
            Log.fatal("WolfGroupExpectationが初期化されていません");
        }
        return wolfGroupExpectation;
    }
    public static void initialize(GameInfo gameInfo) {
        wolfGroupExpectation.agentList = gameInfo.getAgentList();
        wolfGroupExpectation.agentList.remove(gameInfo.getAgent());  // 自分自身は抜く

        for (int i = 0; i < wolfGroupExpectation.agentList.size(); i++) {
            for (int j = i+1; j < wolfGroupExpectation.agentList.size(); j++) {
                for (int k = j+1; k < wolfGroupExpectation.agentList.size(); k++) {
                    List<Agent> agents = new ArrayList<>(); // WolfGroupのメンバーを登録
                    agents.add(wolfGroupExpectation.agentList.get(i));
                    agents.add(wolfGroupExpectation.agentList.get(j));
                    agents.add(wolfGroupExpectation.agentList.get(k));

                    WolfGroup wolfGroup = new WolfGroup(agents);    // WolfGroupの作成
                    wolfGroupExpectation.putWolfGroupMap(wolfGroup, Boolean.TRUE);
                }
            }
        }
        isInit = true;
    }
    /* ▲ singleton ▲ */

    /**
     * 特定のエージェントがいるグループを削除(FALSEをセット）する
     * @param agent
     */
    public void deleteGroup(Agent agent) {
        for (Map.Entry<WolfGroup, Boolean> wolfGroup :
                wolfGroupMap.entrySet()) {   // マップセットを取得
            if (wolfGroup.getKey().inAgent(agent)) {
                wolfGroup.setValue(Boolean.FALSE);
            }
        }
    }

    /**
     * 特定のエージェントの値を更新する
     */
}
