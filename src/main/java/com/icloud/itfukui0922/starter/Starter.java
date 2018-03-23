/**
 * ローカルホストの立ち上げとクライアント接続をおこなうための処理をまとめたstaticクラス
 */

package com.icloud.itfukui0922.starter;

import org.aiwolf.common.data.Player;
import org.aiwolf.common.net.GameSetting;
import org.aiwolf.common.net.TcpipClient;
import org.aiwolf.server.AIWolfGame;
import org.aiwolf.server.net.TcpipServer;
import org.aiwolf.server.util.FileGameLogger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class Starter {
    /**
     * ローカルホストのサーバを立ち上げます
     * また，ゲームログをlogディレクトリ下に保存します
     *
     * @param port    接続先ポート番号
     * @param gameNum    ゲーム試行回数
     * @param participant_players    ゲーム参加者数
     */
    public static void startServer(int port, int gameNum, int participant_players) {
        GameSetting gameSetting = GameSetting.getDefaultGame(participant_players);
        gameSetting.setValidateUtterance(false);
        gameSetting.setTalkOnFirstDay(true);
        gameSetting.setTimeLimit(5000);

        new Thread(() -> {
            try {
                TcpipServer gameServer = new TcpipServer(port, participant_players, gameSetting);
                gameServer.waitForConnection();
                AIWolfGame game = new AIWolfGame(gameSetting, gameServer);
                game.setShowConsoleLog(true);        // サーバログ出力をfalseに

                for (int i = 0; i < gameNum; i++) {
                    game.setRand(new Random(i));
                    Calendar calendar = Calendar.getInstance();
                    game.setGameLogger(new FileGameLogger(new File("log/" + calendar.getTime() + "ServerLog.txt")));
                    System.out.println("\n----- Server ready -----\n");

                    game.start();
                }
            } catch (IOException e) {
                System.err.println("ローカルサーバ立ち上げでIOException発生．ログの出力に失敗した可能性があります．");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * クライアントを接続します
     *
     * @param classPass    接続するエージェントのクラスパスを指定します（Playerインターフェースを実装しているクラス）
     * @param playerName    プレイヤー名を指定します
     * @param host    接続先ホスト名を指定します
     * @param port    接続先ポート番号を指定します
     */
    public static void startClient(String classPass, String playerName, String host, int port, org.aiwolf.common.data.Role role) {

        TcpipClient client;

        if (role != null) {
            client = new TcpipClient(host, port);
        } else {
            client = new TcpipClient(host, port, role);
        }

        Class<?> class1;
        Player player = null;
        try {
            class1 = Class.forName(classPass);
            player = (Player) class1.newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("接続するエージェントのクラスが見つかりません．クラスパスが正しいか確認してください．");
            e.printStackTrace();
            System.exit(-1);
        } catch (IllegalAccessException e) {
            System.err.println("クラスがロードできませんでした．クラスのアクセス修飾子を確認してください．");
            e.printStackTrace();
            System.exit(-1);
        } catch (InstantiationException e) {
            System.err.println("クラスのインスタンス作成に失敗しました．インターフェースまたはabstructクラスではないことを確認してください．");
            e.printStackTrace();
            System.exit(-1);
        }
        client.connect(player);
        client.setName(playerName);
        System.out.println(playerName + " is connected");
    }

}
