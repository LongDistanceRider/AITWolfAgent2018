package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Werewolf extends RoleState {

    /**
     * コンストラクタ
     * @param gameInfo  ゲーム情報
     */
    public Werewolf(GameInfo gameInfo) {
        super(gameInfo);
    }

    @Override
    public void update(GameInfo gameInfo) {
        super.gameInfo = gameInfo;
    }

    @Override
    public void dayStart(BoardSurface boardSurface) {
        // 偽占い結果を保管
        boardSurface.getMyInformation().putDivIdenMap(Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo)), Species.HUMAN);
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface) {
        super.gameInfo = gameInfo;
        FlagManagement flagManagement = FlagManagement.getInstance();
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- COするかしないかをダイスで決める　状況が変化していない場合は，COしない CO済みならダイスを降らない-----
        // --- 状況チェック ---
        if (!flagManagement.isComingOut()) {  //CO していない
            /* ################################################################
                以下，大規模工事中
                とりあえず，COしていなければCOするようにしておく
             ################################################################ */
            String comingOutSeerString = comingOut(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.SEER);   // すでにCOして入ればnull返却
            if (comingOutSeerString != null) {
                talkQueue.add(comingOutSeerString);
            }
        }

        // ----- 占い結果報告 -----
        if (flagManagement.isComingOut()) {   // COしていれば結果報告する
            String divinedResultString = divinedResult(boardSurface);
            if (divinedResultString != null) {
                talkQueue.add(divinedResultString);
            }
        }
        return talkQueue;
    }

    /**
     * １ゲーム終了後に呼び出される予定
     */
    public void finish (BoardSurface boardSurface) {
        // 人狼ダイスのQテーブル更新
//        dice.updateQTable(gameInfo, boardSurface);
    }
}
