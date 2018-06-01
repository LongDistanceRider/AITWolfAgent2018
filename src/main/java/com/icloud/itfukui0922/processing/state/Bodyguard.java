package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Bodyguard extends RoleState {
    public Bodyguard(GameInfo gameInfo, BoardSurface boardSurface) {
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
