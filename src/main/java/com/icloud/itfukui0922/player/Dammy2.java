package com.icloud.itfukui0922.player;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.LinkedList;

public class Dammy2 implements Player {

    GameInfo gameInfo;

    // 初日に喋らす発言
    private LinkedList<String> day0Queue = new LinkedList<>();
    private LinkedList<String> day1Queue = new LinkedList<>();
    private LinkedList<String> day2Queue = new LinkedList<>();

    @Override
    public String getName() {
        return "Dammy";
    }

    @Override
    public void update(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        // 初日に喋らす発言
        day0Queue.add("こんにちは");
        day0Queue.add("これからよろしくね");
        day1Queue.add("私は役職ありませんよ");
        day1Queue.add("Agent[2]が怪しいと思う");
        day1Queue.add("Agent[4]に投票します．");
        this.gameInfo = gameInfo;
    }

    @Override
    public void dayStart() {

    }

    @Override
    public String talk() {
        if (gameInfo.getDay() == 0 && !day0Queue.isEmpty()) {
            return day0Queue.poll();
        }
        if (gameInfo.getDay() == 1 && !day1Queue.isEmpty()) {
            return day1Queue.poll();
        }
        if (gameInfo.getDay() == 2 && !day2Queue.isEmpty()) {
            return day2Queue.poll();
        }
        return "Over";
    }

    @Override
    public String whisper() {
        return null;
    }

    @Override
    public Agent vote() {
        return null;
    }

    @Override
    public Agent attack() {
        return null;
    }

    @Override
    public Agent divine() {
        return null;
    }

    @Override
    public Agent guard() {
        return null;
    }

    @Override
    public void finish() {

    }
}
