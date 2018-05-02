package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.processing.state.dice.PossessedDice;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class PossessedState implements RoleState {

    /* 裏切り者ダイス */
    private PossessedDice possessedDice;

    public PossessedState(int maxGameDay, int playerNum) {
        // 裏切り者ダイス初期化
        possessedDice = new PossessedDice(maxGameDay, playerNum);
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        // 偽占い結果を保管
        boardSurface.getMyInformation().putDivIdenMap(Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo)), Species.HUMAN);
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface, int day) {
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- 偽占い師COするかをダイスで決める　状況が変化していない場合はCOしない CO済みならダイスを振らない
        boolean doCO = false;
        // --- 状況チェック ---
        if (!FlagManagement.getInstance().isComingOut()) {  // COしていない
            int seerCONum = boardSurface.comingoutRoleAgentList(Role.SEER).size();     // 占い師COしているプレイヤの人数
            boolean mediumCO = false;   // 霊能者COしているプレイヤがいるか

            if (boardSurface.comingoutRoleAgentList(Role.MEDIUM).size() > 0) {
                mediumCO = true;    // 霊能者CO
            }

            // --- 状況変化チェック ---
            if (possessedDice.setDiceState(day, seerCONum, mediumCO)) {
                // ダイスを振る
                if (possessedDice.shakeTheDice()) {
                    // COする
                    String comingOutSeerString = UtilState.coming_out(boardSurface.getMyInformation().getAgent(), Role.SEER);   // すでにCOして入ればnull返却
                    if (comingOutSeerString != null) {
                        talkQueue.add(comingOutSeerString);
                    }
                }
            }
        }

        // ----- 占い結果報告 -----
        if (FlagManagement.getInstance().isComingOut()) {   // COしていれば結果報告する
            String divinedResultString = UtilState.divinedResult(boardSurface);
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
        possessedDice.updateQTable(gameInfo, boardSurface);
    }
}
