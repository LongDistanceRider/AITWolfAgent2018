package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.processing.state.dice.PossessedDice;
import com.icloud.itfukui0922.processing.state.dice.SeerDice;
import com.icloud.itfukui0922.processing.state.dice.SeerDiceState;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Possessed extends Role {

    /* 裏切り者ダイス */
    private PossessedDice possessedDice;

    public Possessed(GameInfo gameInfo, BoardSurface boardSurface) {
        super(gameInfo, boardSurface);
//        possessedDice = new SeerDice(new SeerDiceState(gameInfo, boardSurface));
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
            String comingOutSeerString = coming_out(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.SEER);   // すでにCOして入ればnull返却
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
        // 裏切り者ダイスのQテーブル更新
//        possessedDice.updateQTable(gameInfo, boardSurface);
    }
}
