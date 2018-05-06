package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class Medium extends Role {

    public Medium(GameInfo gameInfo, BoardSurface boardSurface) {
        super(gameInfo, boardSurface);
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {

    }

    @Override
    public LinkedList<String> talk(GameInfo gameInfo, BoardSurface boardSurface) {
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- coming out -----
        String comingOutMediumString = coming_out(boardSurface.getMyInformation().getAgent(), org.aiwolf.common.data.Role.MEDIUM);
        if (comingOutMediumString != null) {
            talkQueue.add(comingOutMediumString);
        }
        return talkQueue;
    }

    @Override
    public void finish(GameInfo gameInfo, BoardSurface boardSurface) {

    }
}
