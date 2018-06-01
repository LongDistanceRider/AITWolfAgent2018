package com.icloud.itfukui0922.processing.pro;

import com.icloud.itfukui0922.dice.BoardSurface;
import com.icloud.itfukui0922.dice.PlayerInformation;
import com.icloud.itfukui0922.processing.Notice;
import org.aiwolf.client.lib.Content;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;

/**
 * プロトコル部門の処理
 */

public class ProtocolProcessing {

    /**
     * updateで呼び出されるプロトコル部門用の処理
     * talkを引数にboardSurfaceへデータを格納する
     *
     * @param talk 発言内容（発言者，発言日など）
     * @param text 発言内容（発言内容”COMINGOUT AGENT[01] SEER"など）
     * @param boardSurface 現在の盤面状態
     */
    public static void updateTalkInfo(Talk talk, String text, BoardSurface boardSurface) {
        Content content = new Content(text);  // StringからContent型へ変換
        PlayerInformation playerInformation = boardSurface.getPlayerInformation(talk.getAgent());   // 発言者のプレイヤー情報を取得

        // ラベルごとに処理
        switch (content.getTopic()) {
            /* --- 意図表明に関する文 --- */
            case COMINGOUT:
                playerInformation.setSelfCO(content.getRole()); // 宣言した役職を保管
                // coming out した役職によって
                Notice.comingOut(boardSurface, content);
                break;
            case ESTIMATE:
                playerInformation.estimateOtherAgent(content.getTarget(), content.getRole());   // 他のプレイヤーの印象
                break;
            /* --- 能力結果に関する文 --- */
            case DIVINED:
                playerInformation.putDivIdenMap(content.getTarget(), content.getResult()); // 宣言したターゲットと結果を保管
                playerInformation.setSelfCO(Role.SEER); // 事実上のComing out
                break;
            case IDENTIFIED:
                playerInformation.putDivIdenMap(content.getTarget(), content.getResult());   // 宣言したターゲットと結果を保管
                playerInformation.setSelfCO(Role.MEDIUM); // 事実上のComing out
                break;
            case GUARDED:
                playerInformation.addGuardList(content.getTarget());    // 護衛結果を保管
                playerInformation.setSelfCO(Role.BODYGUARD); // 事実上のComing out
                break;
            /* --- ルール行動・能力に関する文 --- */
            case DIVINATION:
                playerInformation.setDivGuardPlanAgent(content.getTarget());
                break;
            case GUARD:
                playerInformation.setDivGuardPlanAgent(content.getTarget());
                break;
            case VOTE:
                playerInformation.setVotePlanAgent(content.getTarget());
                break;
            case ATTACK:
                playerInformation.setAgent(content.getTarget());
                break;
            /* --- 同意・非同意に関する文 --- */
            case AGREE:
                break;
            case DISAGREE:
                break;
            /* --- 発話制御に関する文 --- */
            case OVER:
                break;
            case SKIP:
                break;
            /* --- REQUEST文 --- */
            case OPERATOR:
                break;
            default:
                break;
        }
    }
}
