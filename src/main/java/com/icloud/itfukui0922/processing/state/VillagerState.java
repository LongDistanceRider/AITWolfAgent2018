package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class VillagerState implements RoleState {

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {

    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface, int day) {
        return null;
    }

    @Override
    public void finish(GameInfo gameInfo, BoardSurface boardSurface) {

    }
}
