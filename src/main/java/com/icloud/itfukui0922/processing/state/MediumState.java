package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class MediumState implements RoleState {

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {

    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface, int day) {
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- coming out -----
        String comingOutMediumString = UtilState.coming_out(boardSurface.getMyInformation().getAgent(), Role.MEDIUM);
        if (comingOutMediumString != null) {
            talkQueue.add(comingOutMediumString);
        }
        return talkQueue;
    }

    @Override
    public void finish(GameInfo gameInfo, BoardSurface boardSurface) {

    }
}
