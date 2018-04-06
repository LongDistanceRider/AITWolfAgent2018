/**
 *
 * ゲームの盤面整理
 * このクラスはQ学習における状態sになる
 * 状態集合SはAITWolfクラスで管理する
 * プレイヤ情報は盤面クラスで管理するように
 *
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardSurface {

    /* プレイヤ情報 */
    List<PlayerInformation> playerInformationList = new ArrayList<>();
    /* 自分のプレイヤ情報 */
    MyInformation myInformation;
    /* 会話情報 */
    List<Talk> talkList = new ArrayList<>();

    public List<Talk> getTalkList() {
        return talkList;
    }

    public MyInformation getMyInformation() {
        return myInformation;
    }

    public void setMyInformation(MyInformation myInformation) {
        this.myInformation = myInformation;
    }

    /**
     * コンストラクタ
     *
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
        myInformation = new MyInformation();
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

    /**
     * talkListへtalkを追加する
     *
     * @param talk 追加するtalk
     * @return 追加できた=true
     */
    public boolean setTalkList(Talk talk) {
        return talkList.add(talk);
    }

    /**
     * 占い霊能結果の追加
     * @param target
     * @param result
     */
    public void putDivIdenMap(Agent target, Species result) {
        myInformation.putDivIdenMap(target, result);
    }

    /**
     * 占い霊能結果の最後の結果を返す
     * @return リストがからの場合はnullが返却される
     */
    public Map.Entry<Agent, Species> peekDivIdenMap() {
        return myInformation.peekDivIdenMap();
    }
}
