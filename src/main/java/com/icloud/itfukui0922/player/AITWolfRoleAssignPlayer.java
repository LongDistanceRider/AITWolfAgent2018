package com.icloud.itfukui0922.player;

import org.aiwolf.sample.lib.AbstractRoleAssignPlayer;

/**
 * プロトコル部門大会サーバで動かすために用意したクラス
 */
public class AITWolfRoleAssignPlayer extends AbstractRoleAssignPlayer{

    public AITWolfRoleAssignPlayer() {
        setSeerPlayer(new AITWolfPro());
        setPossessedPlayer(new AITWolfPro());
        setWerewolfPlayer(new AITWolfPro());
        setVillagerPlayer(new AITWolfPro());
    }
    @Override
    public String getName() {
        return "AITWolf";
    }
}
