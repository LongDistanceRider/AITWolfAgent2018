/**
 *
 * ゲームの盤面整理
 * このクラスはQ学習における状態sになる
 * 状態集合SはAITWolfクラスで管理する
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Judge;
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
        // 自分自身のプレイヤ情報作成
        myPlayerInfomation = new PlayerInformation(gameInfo.getAgent());
    }

    /**
     * 占い結果を追加します．
     * PlayerInfomationにも同じメソッドがありますが，
     * 盤面状態の変更を伴う必要があるため，
     * 外部クラスから占い結果を追加する時はこちらのメソッドを利用すること
     *
     * @param divination
     */
    public void addDivinationList(Judge divination) {
        myPlayerInfomation.addDivinationList(divination);

    }
}
