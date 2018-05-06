/**
 * ローカルホストを立ち上げ，Participantsリストにあるプレイヤーを参加させ，実行するクラス
 *
 */

package com.icloud.itfukui0922.starter;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;

import java.util.ArrayList;
import java.util.List;

public class LocalHostStarter {

    /* 参加プレイヤー */
    private List<Player> participantsPlayerList = new ArrayList<>();
    /* 希望役職 */
    Role requestRole = null;
    /* 対戦回数 */
    private int gameNum = 1;
    /* ポート番号 */
    private int port = 10000;

    /**
     * コンストラクタ
     *
     * @param participantsPlayerList 参加プレイヤーリスト
     * @param gameNum 対戦回数
     */
    public LocalHostStarter(List<Player> participantsPlayerList, int gameNum) {
        this.participantsPlayerList = participantsPlayerList;
        this.gameNum = gameNum;
    }

    /**
     * コンストラクタ　
     *
     * @param participantsPlayerList 参加プレイヤーリスト
     * @param requestRole 希望役職リスト
     * @param gameNum 対戦回数
     */
    public LocalHostStarter(List<Player> participantsPlayerList, Role requestRole, int gameNum) {
        this.participantsPlayerList = participantsPlayerList;
        this.requestRole = requestRole;
        this.gameNum = gameNum;
    }

    /**
     * ローカルホストで対戦開始
     */
    public void start() {
        int participant_players = participantsPlayerList.size();    // 参加人数取得
        Starter.startServer(port, gameNum, participant_players);    // ローカルホストを立ち上げ

        // TODO スレッド同期しろよ（反省）
        // ここでスリープしないとサーバが立ち上がる前にクライアント接続する
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Player player :
                participantsPlayerList) {
            if (requestRole != null) {  // 希望役職リストが提出されている場合は希望役職リスト通りにクライアントへ
                // 希望役職を提出してクライアント起動
                Starter.startClient(player, "localhost", port, requestRole);
                requestRole = null;
            } else {
                // 希望役職なしでクライアント起動
                Starter.startClient(player, "localhost", port, null);
            }
        }
    }

}
