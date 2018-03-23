/**
 * プロトコル部門の処理
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.PlayerInformation;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.IdentContentBuilder;
import org.aiwolf.common.data.Talk;

public class ProtocolProcessing {

    /**
     * updateで呼び出されるプロトコル部門用の処理
     * talkを引数にboardSurfaceへデータを格納する
     *
     * @param talk 発言内容
     * @param boardSurface 現在の盤面状態
     */
    public static void updateTalkInfo(Talk talk, BoardSurface boardSurface) {
        Content content = new Content(talk.getText());  // StringからContent型へ変換
        PlayerInformation playerInformation = boardSurface.getPlayerInformation(talk.getAgent());   // 発言者のプレイヤー情報を取得

        // ラベルごとに処理
        switch (content.getTopic()) {
            case COMINGOUT:
                playerInformation.setSelfCO(content.getRole()); // 宣言した役職を保管
                break;
            case DIVINED:
                playerInformation.addDivinationMap(content.getTarget(), content.getResult());   // 宣言したターゲットと結果を保管
                break;
            case IDENTIFIED:
                playerInformation.addIdentifiedMap(content.getTarget(), content.getResult());   // 宣言したターゲットと結果を保管
                break;
            default:
                break;
        }
    }
}
