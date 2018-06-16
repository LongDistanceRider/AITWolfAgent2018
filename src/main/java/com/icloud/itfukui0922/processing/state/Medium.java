package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Medium extends RoleState {

    public Medium(GameInfo gameInfo) {
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
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- coming out -----
        String comingOutMediumString = comingOut(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.MEDIUM);
        if (comingOutMediumString != null) {
            talkQueue.add(comingOutMediumString);
        }
        return talkQueue;
    }

    @Override
    public void finish(BoardSurface boardSurface) {

    }
}
