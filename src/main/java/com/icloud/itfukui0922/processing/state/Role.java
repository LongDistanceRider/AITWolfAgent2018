package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.processing.TransNL;
import com.icloud.itfukui0922.processing.state.dice.Dice;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.client.lib.ComingoutContentBuilder;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.ContentBuilder;
import org.aiwolf.client.lib.DivinedResultContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;
import java.util.Map;

/**
 * デザインパターン「State」
 */
public abstract class Role {

    protected GameInfo gameInfo;
    protected BoardSurface boardSurface;

    public Role(GameInfo gameInfo, BoardSurface boardSurface) {
        this.gameInfo = gameInfo;
        this.boardSurface = boardSurface;
    }

    /**
     * 一日の初めの処理
     * @param gameInfo
     * @param boardSurface
     */
    public abstract void dayStart(GameInfo gameInfo, BoardSurface boardSurface);

    /**
     * 発言処理
     * @param gameInfo
     * @param boardSurface
     * @return
     */
    public abstract LinkedList<String> talk(GameInfo gameInfo, BoardSurface boardSurface);

    /**
     * ゲーム終了時に呼び出される処理
     * 主に状態の初期化と強化学習「ダイス」の評価
     * @param gameInfo
     * @param boardSurface
     */
    public abstract void finish(GameInfo gameInfo, BoardSurface boardSurface);


    /**
     * coming out処理
     * @param agent　自分自身
     * @return
     */
    protected static String coming_out (Agent agent, org.aiwolf.common.data.Role role) {
        if (!FlagManagement.getInstance().isComingOut()) {  // まだcoming　outしていなければ
            FlagManagement.getInstance().setComingOut(true);    // フラグセット
            if (FlagManagement.getInstance().isNLSwitch()) {
                String comingoutString = "私は" + TransNL.translateNL(role) + "です。";
                return comingoutString;
            } else {
                ContentBuilder builder = new ComingoutContentBuilder(agent, role);
                return new Content(builder).getText();
            }
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
        if (FlagManagement.getInstance().isResultReport()) {   // 一度結果報告していたらnull返却
            return null;
        }
        Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap(); // 占い結果取得
        if (divinationResult != null) {
            FlagManagement.getInstance().setResultReport(true);
            if (FlagManagement.getInstance().isNLSwitch()) {
                String divinedString = divinationResult.getKey() + "は" + TransNL.translateNL(divinationResult.getValue()) + "でした。";
                return divinedString;
            } else {
                ContentBuilder builder = new DivinedResultContentBuilder(divinationResult.getKey(), divinationResult.getValue());
                Log.trace("占い結果報告 target: " + divinationResult.getKey() + " result: " + divinationResult.getValue());
                return new Content(builder).getText();  // 占い結果報告
            }
        }
        return null;
    }
}
