/**
 * 役職固有の処理
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Util;
import org.aiwolf.client.lib.ComingoutContentBuilder;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.ContentBuilder;
import org.aiwolf.client.lib.DivinedResultContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import sun.jvm.hotspot.runtime.VM;

import java.util.LinkedList;
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
                        boardSurface.putDivIdenMap(divination.getTarget(), divination.getResult());
                    } else {
                        System.err.println("占い結果取得失敗");
                    }
                }
                break;
            case POSSESSED:
                // 偽占い結果を作成
                boardSurface.getMyInformation().putDivIdenMap(Util.randomElementSelect(Util.aliveAgentListRemoveMe(gameInfo)), Species.HUMAN);
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
            case SEER:
                // ----- coming out -----
                String comingOutSeerString = coming_out(boardSurface.getMyInformation().getAgent(), myRole);
                if (comingOutSeerString != null) {
                    talkQueue.add(comingOutSeerString);
                }
                // ----- 占い結果報告 -----
                String divinedResultString = divinedResult(boardSurface);
                if (divinedResultString != null) {
                    talkQueue.add(divinedResultString);
                }
                break;
            case MEDIUM:
                // ----- coming out -----
                String comingOutMediumString = coming_out(boardSurface.getMyInformation().getAgent(), myRole);
                if (comingOutMediumString != null) {
                    talkQueue.add(comingOutMediumString);
                }
                break;
            case POSSESSED:
                // ----- coming out -----
                String comingOutPosessedString = coming_out(boardSurface.getMyInformation().getAgent(), Role.SEER);
                if (comingOutPosessedString != null) {
                    talkQueue.add(comingOutPosessedString);
                }
                // ----- 占い結果報告 -----
                String divinedLierResultString = divinedResult(boardSurface);
                if (divinedLierResultString != null) {
                    talkQueue.add(divinedLierResultString);
                }
                break;
            default:
        }
        return talkQueue;
    }

    /**
     * 占い結果報告処理
     * @param boardSurface
     * @return 占い結果がなければnull返却
     */
    private String divinedResult(BoardSurface boardSurface) {
        if (!FlagManagement.getInstance().isResultReport()) {
            return null;
        }
        Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap();
        if (divinationResult != null) {
            ContentBuilder builder = new DivinedResultContentBuilder(divinationResult.getKey(), divinationResult.getValue());
            FlagManagement.getInstance().setResultReport(true);
            return new Content(builder).getText();  // 占い結果報告
        }
        return null;
    }

    /**
     * coming out処理
     * @param agent　自分自身
     * @return
     */
    private String coming_out (Agent agent, Role role) {
        if (!FlagManagement.getInstance().isComingOut()) {  // まだcoming　outしていなければ
            ContentBuilder builder = new ComingoutContentBuilder(agent, role);
            FlagManagement.getInstance().setComingOut(true);    // フラグセット
            return new Content(builder).getText();
        }
        return null;
    }
}
