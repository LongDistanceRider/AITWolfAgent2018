/**
 * いくつかの再利用する処理
 */
package com.icloud.itfukui0922.util;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

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

}
