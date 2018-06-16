/**
 *
 * ゲームの盤面整理
 * このクラスはQ学習における状態sになる
 * 状態集合SはAITWolfクラスで管理する
 * プレイヤ情報は盤面クラスで管理するように
 *
 */
package com.icloud.itfukui0922.strategy;

import com.icloud.itfukui0922.log.Log;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 盤面状態を保持するクラス
 * Singleton
 */
public class BoardSurface {

    /* Initializeフラグ */
    private static boolean isInit = false;
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

    public List<Talk> getTalkList() {
        return talkList;
    }

    public List<Agent> getExecutedAgentList() {
        return executedAgentList;
    }

    public List<Agent> getBiteAgentList() {
        return biteAgentList;
    }

    public MyInformation getMyInformation() {
        return myInformation;
    }

    /* ▼ singleton ▼ */
    private static BoardSurface boardSurface = new BoardSurface();
    private BoardSurface(){}
    public static BoardSurface getInstance() {
        if (!isInit) {
            Log.fatal("BoardSurfaceが初期化されていません");
        }
        return boardSurface;
    }
    public static void initialize(GameInfo gameInfo) {
        // PlayerInfomationリストを作成
        for (Agent agent :
                gameInfo.getAgentList()) {
            if (agent == gameInfo.getAgent()) continue; // 自分自身の場合はスキップ
            boardSurface.playerInformationList.add(new PlayerInformation(agent));
        }
        // 自分自身のプレイヤ情報作成
        boardSurface.myInformation = new MyInformation(gameInfo.getAgent());
        isInit = true;
    }

    /* ▲ singleton ▲ */

    /**
     * インスタンスリセット
     *
     */
    public void reset() {
        boardSurface = new BoardSurface();
        isInit = false;
    }

    /**
     * 追放されたエージェントを追加する
     * @param agent
     * @return
     */
    public boolean addExecutedAgentList(Agent agent) {
        return executedAgentList.add(agent);
    }

    /**
     * 襲撃されたエージェントを追加する
     * @param agent
     * @return
     */
    public boolean addBiteAgentList(Agent agent) {
        return biteAgentList.add(agent);
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
     * 占い霊能結果の最後の結果を返す
     * @return リストがからの場合はnullが返却される
     */
    public Map.Entry<Agent, Species> peekDivIdenMap() {
        return myInformation.peekDivIdenMap();
    }

    /**
     * 特定の役職をCOしたエージェントのリストを返す
     * @param role　調べたい役職
     * @return COしたエージェントのリスト（自分自身は含まない）
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

    /**
     * 対抗しているエージェントを返す
     * @return 対抗エージェントリスト
     */
    public List<Agent> getOppositionAgentList() {
        List<Agent> oppositionAgentList = new ArrayList<>();
        // 全てのエージェントの中から，対抗COを取り出す．
        Role myRole = myInformation.getMyRole();
        return comingoutRoleAgentList(myRole);
    }
}
