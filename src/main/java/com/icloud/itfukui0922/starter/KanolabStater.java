package com.icloud.itfukui0922.starter;

import org.aiwolf.common.data.Player;

import java.util.List;

public class KanolabStater {

    List<Player> participantsPlayerList;
    public KanolabStater(List<Player> participantsPlayerList) {
        this.participantsPlayerList = participantsPlayerList;
    }

    public void start() {
        for (Player player :
                participantsPlayerList) {
            // 希望役職なしでクライアント起動
            Starter.startClient(player.getClass().getName(), player.getName(), "49.212.130.102", 10000, null);
        }
    }
}
