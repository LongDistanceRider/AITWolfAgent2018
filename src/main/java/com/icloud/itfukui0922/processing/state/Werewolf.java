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
     * @param boardSurface  盤面
     */
    public Werewolf(GameInfo gameInfo, BoardSurface boardSurface) {
        super(gameInfo, boardSurface);
//        super.dice = new WerewolfDice(gameInfo, boardSurface);
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        // 偽占い結果を保管
        boardSurface.getMyInformation().putDivIdenMap(Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo)), Species.HUMAN);
    }

    @Override
    public LinkedList<String> talk(GameInfo gameInfo, BoardSurface boardSurface) {
        super.gameInfo = gameInfo;
        super.boardSurface = boardSurface;
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- COするかしないかをダイスで決める　状況が変化していない場合は，COしない CO済みならダイスを降らない-----
        // --- 状況チェック ---
        if (!FlagManagement.getInstance().isComingOut()) {  //CO していない
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
        if (FlagManagement.getInstance().isComingOut()) {   // COしていれば結果報告する
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
    public void finish (GameInfo gameInfo, BoardSurface boardSurface) {
        // 人狼ダイスのQテーブル更新
//        dice.updateQTable(gameInfo, boardSurface);
    }
}
