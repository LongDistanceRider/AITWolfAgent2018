/**
 * いくつかの再利用する処理
 */
package com.icloud.itfukui0922.util;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    /**
     * リストから要素を一つランダムに返す
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T randomElementSelect(List<T> list) {
        if (list.isEmpty()) return null;
        else return list.get((int) (Math.random() * list.size()));
    }

    /**
     * 生存プレイヤーリストから自分自身を除いたリストを返す
     * @return 生存プレイヤーリスト（自分自身を除く）
     */
    public static List<Agent> aliveAgentListRemoveMe(GameInfo gameInfo) {
        List<Agent> aliveAgentList = gameInfo.getAliveAgentList();
        aliveAgentList.remove(gameInfo.getAgent());
        return aliveAgentList;
    }

    /**
     * ゲーム参加者数からゲーム最大日数を取得
     */
    public static int gameMaxDay(int numPlayer) {
        int maxDay = (numPlayer-1) / 2;
        return maxDay;
    }

    /**
     * ゲームの勝敗を判定
     * 現在生きているプレイヤを調べて，人狼が生きているかで判定する
     * @param gameInfo
     * @retrun 村人勝利か
     */
    public static boolean isVillageSideWin (GameInfo gameInfo) {
        for (Agent agent :
                gameInfo.getAliveAgentList()) {
            if (gameInfo.getRoleMap().get(agent) == Role.WEREWOLF) {
                return false;
            }
        }
        return true;
    }

    /**
     * talkList内にあるtalkの検索を行う
     * @param talkList
     * @param topic 検索する話題
     * @return topic話題の含むtalkのListを返す
     */
    public static List<Talk> searchTalk(List<Talk> talkList, Topic topic) {
        List<Talk> reTalkList = new ArrayList<>();
        for (Talk talk :
                talkList) {
            // TODO ここ自然言語処理対応していないから気をつけること
            String text = talk.getText();
            Content content = new Content(text);
            if (content.getTopic().equals(topic)) {
                reTalkList.add(talk);
            }
        }

        return reTalkList;
    }
    /**
     * talkList内にあるtalkの検索を行う
     *
     * @param day ゲーム日数
     * @return dayで指定された分のtalkList null返却あり
     */
    public static List<Talk> searchTalk(List<Talk> talkList, int day) {
        List<Talk> reTalkList = new ArrayList<>();
        for (Talk talk :
                reTalkList) {
            if (talk.getDay() == day) {
                reTalkList.add(talk);
            }
        }
        return reTalkList;
    }

    /**
     * talkList内にあるtalkの検索を行う
     * @param agent submitエージェント
     * @return 発言者agentがした発言をのtalkをListにして返す　null返却あり
     */
    public static List<Talk> searchTalk(List<Talk> talkList, Agent agent) {
        List<Talk> reTalkList = new ArrayList<>();
        for (Talk talk :
                reTalkList) {
            if (talk.getAgent().equals(agent)) {
                reTalkList.add(talk);
            }
        }
        return reTalkList;
    }
    
    /**
     * talkList内にあるtalkの検索を行う
     * @param talkList
     * @param day ゲーム日
     * @param agent submitエージェント
     * @return
     */
    public static List<Talk> searchTalk(List<Talk> talkList, int day, Agent agent) {
        List<Talk> talkDayList = new ArrayList<>(searchTalk(talkList, day));
        return new ArrayList<>(searchTalk(talkDayList, agent));
    }
}
