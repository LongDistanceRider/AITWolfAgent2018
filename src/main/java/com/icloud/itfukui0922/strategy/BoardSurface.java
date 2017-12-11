/**
 *
 * ゲームの盤面整理
 * このクラスはQ学習における状態sになる
 * 状態集合SはAITWolfクラスで管理する
 *
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.List;

public class BoardSurface {

    /* プレイヤ情報 */
    List<PlayerInformation> playerInformationList = new ArrayList<>();
    /* 自分のプレイヤ情報 */
    PlayerInformation myPlayerInfomation;

    public PlayerInformation getMyPlayerInfomation() {
        return myPlayerInfomation;
    }

    /**
     * constracter
     * @param gameInfo
     */
    public BoardSurface(GameInfo gameInfo) {
        // PlayerInfomationリストを作成
        gameInfo.getAgentList();
        for (Agent agent :
                gameInfo.getAgentList()) {
            playerInformationList.add(new PlayerInformation(agent));
        }
        // 自分自身のプレイヤ情報作成
        myPlayerInfomation = new PlayerInformation(gameInfo.getAgent());
    }

    /**
     * 占い結果を追加します．
     * PlayerInfomationにも同じメソッドがありますが，
     * 盤面状態の変更を伴う必要があるため，
     * 外部クラスから占い結果を追加する時はこちらのメソッドを利用すること
     */
    public void addDivinationMap(Agent agent, Species species) {
        myPlayerInfomation.addDivinationMap(agent, species);
    }

    /**
     * プレイヤー情報リストからプレイヤー情報を返す
     * @param agent 欲しいプレイヤー情報
     * @return プレイヤー情報
     */
    public PlayerInformation getPlayerInformation(Agent agent) {
        for (PlayerInformation playerInformation :
                playerInformationList) {
            if (playerInformation.getAgent().equals(agent)) return playerInformation;
        }
        return null;
    }
}
