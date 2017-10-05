/**
 * ローカルホストを立ち上げ，Participantsリストにあるプレイヤーを参加させ，実行するクラス
 *
 */

package com.icloud.itfukui0922;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;

import java.util.ArrayList;
import java.util.List;

public class LocalHostStarter {

    /* 参加プレイヤー */
    List<Player> participantsPlayerList = new ArrayList<>();
    /* 希望役職 */
    List<Role> requestRoleList = null;
    /* 対戦回数 */
    int gameNum = 1;
    /* ポート番号 */
    int port = 10000;

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
     * @param requestRoleList 希望役職リスト
     * @param gameNum 対戦回数
     */
    public LocalHostStarter(List<Player> participantsPlayerList, List<Role>requestRoleList, int gameNum) {
        this.participantsPlayerList = participantsPlayerList;
        this.requestRoleList = requestRoleList;
        this.gameNum = gameNum;
    }

    public void start() {
        int participant_players = participantsPlayerList.size();    // 参加人数取得
        Starter.startServer(port, gameNum, participant_players);    // ローカルホストを立ち上げ

        for (Player player :
                participantsPlayerList) {
            if (requestRoleList != null) {  // 希望役職リストが提出されている場合は希望役職リスト通りにクライアントへ
                for (Role role :
                        requestRoleList) {
                    // 希望役職を提出してクライアント起動
                    Starter.startClient(player.getClass().getName(), player.getName(), "localhost", port, participant_players, role);

                }
            } else {
                // 希望役職なしでクライアント起動
                Starter.startClient(player.getClass().getName(), player.getName(), "localhost", port, participant_players, null);
            }
        }
    }

}
