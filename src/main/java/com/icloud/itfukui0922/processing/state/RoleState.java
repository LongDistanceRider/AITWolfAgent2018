package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;
import java.util.Map;

/**
 * デザインパターン「State」
 */
public abstract class RoleState {

    protected GameInfo gameInfo;

    public RoleState(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * update処理
     */
    public abstract void update(GameInfo gameInfo);
    /**
     * 一日の初めの処理
     * @param boardSurface
     */
    public abstract void dayStart(BoardSurface boardSurface);

    /**
     * 発言処理
     * @param boardSurface
     * @return
     */
    public abstract LinkedList<String> talk(BoardSurface boardSurface);

    /**
     * ゲーム終了時に呼び出される処理
     * 主に状態の初期化と強化学習「ダイス」の評価
     * @param boardSurface
     */
    public abstract void finish(BoardSurface boardSurface);


    /**
     * 対抗COしてきた相手
     */
    /**
     * coming out処理
     * @param agent　自分自身
     * @return
     */
    protected static String comingOut(Agent agent, org.aiwolf.common.data.Role role) {
        FlagManagement flagManagement = FlagManagement.getInstance();
        if (!flagManagement.isComingOut()) {  // まだcoming　outしていなければ
            flagManagement.setComingOut(true);    // フラグセット
            ContentBuilder builder = new ComingoutContentBuilder(agent, role);
            return new Content(builder).getText();
        }
        // ここでnullが返却されないように上部処理を施すことを遵守するため，warningを出す
        Log.warn("予想しないnull返却");
        return null;
    }

    /**
     * 占い結果報告処理
     * @param boardSurface
     * @return 一度結果報告をしているか，占い結果がなければnull返却
     */
    protected static String divinedResult(BoardSurface boardSurface) {
        FlagManagement flagManagement = FlagManagement.getInstance();
        if (flagManagement.isResultReport()) {   // 一度結果報告していたらnull返却
            return null;
        }
        Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap(); // 占い結果取得
        if (divinationResult != null) {
            flagManagement.setResultReport(true);
            ContentBuilder builder = new DivinedResultContentBuilder(divinationResult.getKey(), divinationResult.getValue());
            Log.trace("占い結果報告 target: " + divinationResult.getKey() + " result: " + divinationResult.getValue());
            return new Content(builder).getText();  // 占い結果報告
        }
        return null;
    }

    /**
     * 投票先を発言
     * @param target 投票先
     * @return 過去に投票先発言をしている場合はnull返却
     */
    protected  static String voteUtterance (Agent target) {
        FlagManagement flagManagement = FlagManagement.getInstance();
        if (!flagManagement.getVoteUtteranceMap(target)) { // 投票先発言をしたことがなければ


        }
        return null;
    }
}
