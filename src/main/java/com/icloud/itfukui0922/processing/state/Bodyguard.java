package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Bodyguard extends RoleState {
    public Bodyguard(GameInfo gameInfo) {
        super(gameInfo);
    }

    @Override
    public void update(GameInfo gameInfo) {
        super.gameInfo = gameInfo;
    }

    @Override
    public void dayStart(BoardSurface boardSurface) {

    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface) {
        return null;
    }

    @Override
    public void finish(BoardSurface boardSurface) {

    }
}
