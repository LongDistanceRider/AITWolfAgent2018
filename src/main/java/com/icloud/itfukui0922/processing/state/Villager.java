package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.dice.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Villager extends Role {

    public Villager(GameInfo gameInfo, BoardSurface boardSurface) {
        super(gameInfo, boardSurface);
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {

    }

    @Override
    public LinkedList<String> talk(GameInfo gameInfo, BoardSurface boardSurface) {
        return null;
    }

    @Override
    public void finish(GameInfo gameInfo, BoardSurface boardSurface) {

    }
}
