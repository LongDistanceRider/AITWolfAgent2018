package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.processing.state.dice.*;
import com.icloud.itfukui0922.dice.BoardSurface;
import com.icloud.itfukui0922.dice.FlagManagement;
import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import sun.jvm.hotspot.runtime.VM;

import java.util.*;

public class Seer extends RoleState {

    private SeerDice seerDice;

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
                    boardSurface.getPlayerInformation(divination.getTarget()).setConvictionRole(Role.WEREWOLF);   // 確信した役職に人狼を加える
                }
            } else {
                Log.warn("占い結果の取得に失敗");
            }
        }
    }

    @Override
    public LinkedList<String> talk(GameInfo gameInfo, BoardSurface boardSurface) {
        FlagManagement flagManagement = FlagManagement.getInstance();
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- COするかしないかをダイスで決める　状況が変化していない場合は，COしない CO済みならダイスを降らない-----
        // --- 状況チェック ---
        if (!flagManagement.isComingOut()) {  //CO していない
            /* ################################################################
                以下，大規模工事中
             ################################################################ */
            if (seerDice.setDiceState(gameInfo, boardSurface)) {    // 状況が変化した
                if (seerDice.shakeTheDice() > 0) {  // ダイスを振り，0以上が帰ってきたらCOする
                    flagManagement.setComingOut(true);    // フラグセット
                    ContentBuilder builder = new ComingoutContentBuilder(gameInfo.getAgent(), Role.SEER);  // CO発言生成
                    String comingOutSeerString = new Content(builder).getText();    //CO発言
                    talkQueue.add(comingOutSeerString); // 発言キューに追加
                }
            }
        }

        // ----- 占い結果報告 -----
        if (flagManagement.isComingOut() && !flagManagement.isResultReport()) {  // COしていれば結果報告をする && 結果報告をしていない
            flagManagement.setResultReport(true); // 結果報告のフラグセット

            Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap(); // 占い結果取得
            ContentBuilder builder = new DivinedResultContentBuilder(divinationResult.getKey(), divinationResult.getValue());   // 占い結果発言生成
            String divinedResultString = new Content(builder).getText();  // 占い結果報告
            talkQueue.add(divinedResultString); // 発言キューに追加
            Log.trace("占い結果報告 target: " + divinationResult.getKey() + " result: " + divinationResult.getValue());
            // ----- 占い結果で黒が出た場合に投票先発言をする -----
            if (divinationResult.getValue().equals(Species.WEREWOLF)) {
                if (!flagManagement.getVoteUtteranceMap(divinationResult.getKey())) {   // target に投票発言をまだしていないなら
                    flagManagement.putVoteUtteranceMap(divinationResult.getKey(), true);   // フラグセット
                    ContentBuilder vBuilder = new VoteContentBuilder(divinationResult.getKey());    // 投票発言生成
                    String voteString = new Content(vBuilder).getText();    // 投票発言
                    talkQueue.add(voteString); // 発言キューに追加
                    Log.trace("投票先発言 target: " + divinationResult.getKey());
                }
            }
        }

        // ----- 偽占い師に対して投票発言とDISAGREE発言 -----


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
