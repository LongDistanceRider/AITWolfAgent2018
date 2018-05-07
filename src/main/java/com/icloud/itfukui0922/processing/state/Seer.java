package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.processing.state.dice.*;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.*;

public class Seer extends Role {

    SeerDice seerDice;

    /**
     * コンストラクタ
     * @param gameInfo  ゲーム情報
     * @param boardSurface  盤面
     */
    public Seer(GameInfo gameInfo, BoardSurface boardSurface) {
        super(gameInfo, boardSurface);
        seerDice = new SeerDice(new SeerDiceState(gameInfo, boardSurface));
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        // ----- 最新状態に更新 -----
        super.gameInfo = gameInfo;
        super.boardSurface = boardSurface;

        // ----- 占い結果の取り込み -----
        if (0 < gameInfo.getDay()) {    // 0日目は占い結果が取得できないため，回避
            // 占い結果の取り込み
            Judge divination = gameInfo.getDivineResult();
            if (divination != null) {
                boardSurface.putDivIdenMap(divination.getTarget(), divination.getResult());
                Log.debug("占い結果 target: " + divination.getTarget() + " result: " + divination.getResult());

                if (divination.getResult().equals(Species.WEREWOLF)) {  // 黒出ししたら，boardSurfaceに登録
                    boardSurface.getPlayerInformation(divination.getTarget()).setConvictionRole(org.aiwolf.common.data.Role.WEREWOLF);   // 確信した役職に人狼を加える
                }
            } else {
                Log.warn("占い結果の取得に失敗");
            }
        }
    }

    @Override
    public LinkedList<String> talk(GameInfo gameInfo, BoardSurface boardSurface) {
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
                Log.trace("talkQueueに追加: " + comingOutSeerString);
                talkQueue.add(comingOutSeerString);
            }
            // ################################################################
//            if (seerDice.setDiceState(gameInfo, boardSurface)) {    // 状況が変化した
//                if (seerDice.shakeTheDice() > 0) {  // COする
//                    String comingOutSeerString = coming_out(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.SEER);   // すでにCOして入ればnull返却
//                    if (comingOutSeerString != null) {
//                        talkQueue.add(comingOutSeerString);
//                    }
//                }
//            }
//
//            if (DiceState.isDiceStateChange(preDiceState, diceState)) { // 状況が変化した
//                preDiceState = diceState;
//                Dice dice = new SeerDice(diceState);
//                if (dice.shakeTheDice() > 0) {  // COする
//                    String comingOutSeerString = coming_out(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.SEER);   // すでにCOして入ればnull返却
//                    if (comingOutSeerString != null) {
//                        talkQueue.add(comingOutSeerString);
//                    }
//                }
//            }

//            boolean oppositionCO = false;   // 対抗COが出ているか
//            boolean mediumCO = false;       // 霊能者COが出ているか
//            boolean discoveryWolf = false;  // 人狼を発見しているか
//            if (boardSurface.comingoutRoleAgentList(org.aiwolf.common.data.Role.SEER).size() > 0) {
//                oppositionCO = true;   // 対抗CO
//            }
//            if (boardSurface.comingoutRoleAgentList(org.aiwolf.common.data.Role.MEDIUM).size() > 0) {
//                mediumCO = true;   // 霊能者CO
//            }
//            if (boardSurface.getMyInformation().peekDivIdenMap().getValue() == Species.WEREWOLF) {
//                discoveryWolf = true;  // 黒出し
//            }
//
//            // --- 状況変化チェック ---
//            if (dice.setDiceState(gameInfo, boardSurface)) {
//                // ダイスを振る
//                if (dice.shakeTheDice() == 1) {
//                    // COする
//                    String comingOutSeerString = coming_out(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.SEER);   // すでにCOして入ればnull返却
//                    if (comingOutSeerString != null) {
//                        talkQueue.add(comingOutSeerString);
//                    }
//                }
//            }
        }

        // ----- 占い結果報告 -----
        if (FlagManagement.getInstance().isComingOut()) {  // COしていれば結果報告をする
            String divinedResultString = divinedResult(boardSurface);
            if (divinedResultString != null) {
                Log.trace("talkQueueに追加: " + divinedResultString);

                talkQueue.add(divinedResultString);
            }
        }

        // ----- 占い結果で黒が出た場合に投票先発言をする -----
        Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap(); // 占い結果取得
        if (divinationResult.getValue().equals(Species.WEREWOLF)) {
            String voteUtterance = voteUtterance(divinationResult.getKey());
            if (voteUtterance != null) {
                Log.trace("talkQueueに追加: " + voteUtterance);
                talkQueue.add(voteUtterance);
            }
        }
        return talkQueue;
    }

    /**
     * １ゲーム終了後に呼び出される予定
     */
    public void finish (GameInfo gameInfo, BoardSurface boardSurface) {
        // 占い師ダイスのQテーブル更新
        seerDice.updateQTable(gameInfo, boardSurface);
    }
}
