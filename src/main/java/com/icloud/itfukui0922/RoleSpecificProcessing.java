/**
 * 役職固有の処理
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.ContentBuilder;
import org.aiwolf.client.lib.DivinedResultContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RoleSpecificProcessing {
    /* 自分自身の役職 */
    Role myRole = Role.VILLAGER;

    /* 自分自身の役職をセットする */
    public void setMyRole(Role myRole) {
        this.myRole = myRole;
    }


    /**
     * 役職ごとにdayStart関数ですべき処理をまとめる
     * @param gameInfo
     * @param boardSurface
     */
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        switch (myRole) {
            case MEDIUM:    // 占い師の処理
                if (0 < gameInfo.getDay()) {    // 0日目は占い結果が取得できないため，回避
                    // 占い結果の取り込み
                    Judge divination = gameInfo.getDivineResult();
                    if (divination != null) {
                        boardSurface.getMyPlayerInfomation().addDivinationMap(divination.getTarget(), divination.getResult());
                    } else {
                        System.err.println("占い結果取得失敗");
                    }
                }
                break;
            default:
        }
    }

    /**
     * 役職ごとにtalk関数ですべき処理をまとめる
     * @param boardSurface
     * @return 発言リスト
     */
    public LinkedList<String> talk(BoardSurface boardSurface) {
        LinkedList<String> talkQueue = new LinkedList<>();

        switch (myRole) {
            case MEDIUM:
                // TODO ここで自分自身の役職をカミングアウトする（PlayerInfomation周りの処理を実装ご)
                Map<Agent, Species> divinationList = boardSurface.getMyPlayerInfomation().getDivinationMap();
                if (divinationList != null) {
                    for (Map.Entry<Agent, Species> entry:
                         divinationList.entrySet()) {
                        ContentBuilder builder = new DivinedResultContentBuilder(entry.getKey(), entry.getValue());
                        talkQueue.add(new Content(builder).getText());
                    }
                }
                break;
            default:
        }
        return talkQueue;
    }
}
