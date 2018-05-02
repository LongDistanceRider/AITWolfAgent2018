package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.client.lib.ComingoutContentBuilder;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.ContentBuilder;
import org.aiwolf.client.lib.DivinedResultContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

import java.util.Map;

public class UtilState {


    /**
     * coming out処理
     * @param agent　自分自身
     * @return
     */
    public static String coming_out (Agent agent, Role role) {
        if (!FlagManagement.getInstance().isComingOut()) {  // まだcoming　outしていなければ
            ContentBuilder builder = new ComingoutContentBuilder(agent, role);
            FlagManagement.getInstance().setComingOut(true);    // フラグセット
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
    public static String divinedResult(BoardSurface boardSurface) {
        if (!FlagManagement.getInstance().isResultReport()) {   // 一度結果報告していたらnull返却
            return null;
        }
        Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap(); // 占い結果取得
        if (divinationResult != null) {
            ContentBuilder builder = new DivinedResultContentBuilder(divinationResult.getKey(), divinationResult.getValue());
            FlagManagement.getInstance().setResultReport(true);
            return new Content(builder).getText();  // 占い結果報告
        }
        return null;
    }
}
