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
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardSurface {

    /* プレイヤ情報 */
    private List<PlayerInformation> playerInformationList = new ArrayList<>();
    /* 自分のプレイヤ情報 */
    private MyInformation myInformation;
    /* 会話情報 */
    private List<Talk> talkList = new ArrayList<>();
    /* 追放されたエージェントリスト */
    private List<Agent> executedAgentList = new ArrayList<>();
    /* 被害者エージェントリスト */
    private List<Agent> biteAgentList = new ArrayList<>();

    public List<Agent> getExecutedAgentList() {
        return executedAgentList;
    }

    public void setExecutedAgentList(List<Agent> executedAgentList) {
        this.executedAgentList = executedAgentList;
    }

    public List<Agent> getBiteAgentList() {
        return biteAgentList;
    }

    public void setBiteAgentList(List<Agent> biteAgentList) {
        this.biteAgentList = biteAgentList;
    }

    public MyInformation getMyInformation() {
        return myInformation;
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
            if (agent == gameInfo.getAgent()) continue; // 自分自身の場合はスキップ
            playerInformationList.add(new PlayerInformation(agent));
        }
        // 自分自身のプレイヤ情報作成
        myInformation = new MyInformation(gameInfo.getAgent());
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
    public boolean addTalkList(Talk talk) {
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

    /**
     * 特定の役職をCOしたエージェントのリストを返す
     * @param role　調べたい役職
     * @return COしたエージェントのリスト
     */
    public List<Agent> comingoutRoleAgentList(Role role) {
        List<Agent> comingoutAgentList = new ArrayList<>();
        for (PlayerInformation info :
                playerInformationList) {
            if (info.getSelfCO() == role) {
                comingoutAgentList.add(info.getAgent());
            }
        }
        return comingoutAgentList;
    }

    /**
     * 占われた人を返す
     * @return
     */
    public List<Agent> getDivinedAgentList() {
        List<Agent> divinedAgentList = new ArrayList<>();
        // 全てのエージェントの中から，占い師COした人を取り出す
        List<Agent> seerCOAgentList = comingoutRoleAgentList(Role.SEER);
        for (Agent seerCOAgent :
                seerCOAgentList) {
            PlayerInformation playerInformation = getPlayerInformation(seerCOAgent); // 占い師COしたAgent情報
            Map<Agent, Species> map = playerInformation.getDivIdenMap();
            for (Agent divinedAgent :
                    map.keySet()) {
                divinedAgentList.add(divinedAgent);
            }
        }
        return divinedAgentList;
    }
}
