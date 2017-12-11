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

    Talk talk;

    public ProtocolProcessing(Talk talk, BoardSurface boardSurface) {
        this.talk = talk;   // フィールドへ保管
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
                break;
            default:
                break;
        }
    }

    public void update() {

    }

}
