package com.icloud.itfukui0922.player;

import org.aiwolf.sample.lib.AbstractRoleAssignPlayer;

/**
 * プロトコル部門大会サーバで動かすために用意したクラス
 */
public class AITWolfRoleAssignPlayer extends AbstractRoleAssignPlayer{

    public AITWolfRoleAssignPlayer() {
        setSeerPlayer(new AITWolf());
        setPossessedPlayer(new AITWolf());
        setWerewolfPlayer(new AITWolf());
        setVillagerPlayer(new AITWolf());
    }
    @Override
    public String getName() {
        return "AITWolf";
    }
}
