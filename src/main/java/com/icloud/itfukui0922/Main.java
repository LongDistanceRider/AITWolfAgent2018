package com.icloud.itfukui0922;

import com.icloud.itfukui0922.log.LogLevel;
import com.icloud.itfukui0922.player.AITWolf;
import com.icloud.itfukui0922.player.Dammy;
import com.icloud.itfukui0922.starter.LocalHostStarter;
import com.icloud.itfukui0922.starter.Starter;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // ----- ローカルホストで対戦 -----
        Starter.startServer(10000, 1, 5 );
        // ここでスリープしないとサーバが立ち上がる前にクライアント接続する
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Starter.startClient(new AITWolf(LogLevel.TRACE, LogLevel.TRACE), "localhost", 10000, Role.SEER);
        Starter.startClient(new Dammy(), "localhost", 10000, null);
        Starter.startClient(new Dammy(), "localhost", 10000, null);
        Starter.startClient(new Dammy(), "localhost", 10000, null);
        Starter.startClient(new Dammy(), "localhost", 10000, null);
//
//        // ----- ローカルホストで対戦開始 -----
//        // 参加プレイヤーリスト作成
//        List<Player> participantsPlayerList = new ArrayList<Player>() {
//            {
//                add(new AITWolf(LogLevel.DEBUG, LogLevel.DEBUG));
//                add(new Dammy());
//                add(new Dammy());
//                add(new Dammy());
//                add(new Dammy());
//            }
//        };
//
//        // 役職希望提出
//        Role requestRole = Role.SEER;
//
//        // 対戦回数を指定
//        int gameNum = 1;
//
////        KanolabStater kanolabStater = new KanolabStater(participantsPlayerList);
////        kanolabStater.start();
//        LocalHostStarter localHostStarter = new LocalHostStarter(participantsPlayerList, requestRole, gameNum);  // インスタンス生成
//        localHostStarter.start();   // ローカルホストで対戦開始
    }
}
