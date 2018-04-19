package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.processing.state.dice.SeerDice;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.client.lib.ComingoutContentBuilder;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.ContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.*;

public class SeerState implements RoleState{

    /* 占い師ダイス */
    private SeerDice seerDice;

    /**
     * コンストラクタ
     * @param maxGameDay
     */
    public SeerState(int maxGameDay) {
        // 占い師ダイス初期化
        seerDice = new SeerDice(maxGameDay);
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        if (0 < gameInfo.getDay()) {    // 0日目は占い結果が取得できないため，回避
            // 占い結果の取り込み
            Judge divination = gameInfo.getDivineResult();
            if (divination != null) {
                boardSurface.putDivIdenMap(divination.getTarget(), divination.getResult());
            } else {
                System.err.println("占い結果取得失敗");
            }
        }
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface, int day) {
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- COするかしないかをダイスで決める　状況が変化していない場合は，COしない CO済みならダイスを降らない-----
        boolean doCO = false;
        // --- 状況チェック ---
        if (!FlagManagement.getInstance().isComingOut()) {  //CO していない
            boolean oppositionCO = false;
            boolean mediumCO = false;
            boolean discoveryWolf = false;
            if (boardSurface.comingoutRoleAgentList(Role.SEER).size() > 0) {
                oppositionCO = true;   // 対抗CO
            }
            if (boardSurface.comingoutRoleAgentList(Role.MEDIUM).size() > 0) {
                mediumCO = true;   // 霊能者CO
            }
            if (boardSurface.getMyInformation().peekDivIdenMap().getValue() == Species.WEREWOLF) {
                discoveryWolf = true;  // 黒出し
            }

            // --- 状況変化チェック ---
            if (seerDice.setDiceState(day, oppositionCO, mediumCO, discoveryWolf)) {
                // ダイスを振る
                if (seerDice.shakeTheDice()) {
                    // COする
                    if (doCO) {
                        String comingOutSeerString = UtilState.coming_out(boardSurface.getMyInformation().getAgent(), Role.SEER);   // すでにCOして入ればnull返却
                        if (comingOutSeerString != null) {
                            talkQueue.add(comingOutSeerString);
                        }
                    }
                }
            }
        }

        // ----- 占い結果報告 -----
        if (FlagManagement.getInstance().isComingOut()) {  // COしていれば結果報告をする
            String divinedResultString = UtilState.divinedResult(boardSurface);
            if (divinedResultString != null) {
                talkQueue.add(divinedResultString);
            }
        }
        return talkQueue;
    }

    /**
     * １ゲーム終了後に呼び出される予定
     *
     * 報酬の計算
     * 報酬内容
     * 勝敗
     * 吊られた
     * 噛まれた
     * 生き残った
     */
    public void finish (GameInfo gameInfo, BoardSurface boardSurface) {
        // 占い師ダイスのQテーブル更新
        seerDice.updateQTable();
    }


}
