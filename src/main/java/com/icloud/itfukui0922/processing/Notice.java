package com.icloud.itfukui0922.processing;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

import java.util.LinkedList;

/**
 * 状況変化に対応し，必要に応じて発言するクラス
 */
public class Notice {
    /* 発言キュー */
    private LinkedList<String> talkQueue = new LinkedList<>();

//    public void topicHook(Topic topic) {
//        // coming out によってhook
//        switch (topic) {
//            case COMINGOUT:
//                // 対抗COしてきたプレイヤの取得
//                break;
//        }
//    }

    /**
     * 他プレイヤーの発言に対して情報取得できることがあるかをチェックし
     * 必要に応じてBoardSurfaceへ導入する
     * @param talk
     */
    public static void comingOut(BoardSurface boardSurface, Talk talk) {
        // TODO 占い師対抗
//        if (boardSurface.getMyInformation().getMyRole().equals(Role.SEER)) {
//            Content content = new Content(talk.getText());
//            if (content.getRole().equals(Role.SEER)) {  // 対抗
//                content.
//            }
//        }
    }
}
