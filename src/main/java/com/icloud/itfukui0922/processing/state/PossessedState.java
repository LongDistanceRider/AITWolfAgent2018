package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class PossessedState implements RoleState {

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        boardSurface.getMyInformation().putDivIdenMap(Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo)), Species.HUMAN);
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface, int day) {
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- coming out -----
        String comingOutPosessedString = UtilState.coming_out(boardSurface.getMyInformation().getAgent(), Role.SEER);
        if (comingOutPosessedString != null) {
            talkQueue.add(comingOutPosessedString);
        }
        // ----- 占い結果報告 -----
        String divinedLierResultString = UtilState.divinedResult(boardSurface);
        if (divinedLierResultString != null) {
            talkQueue.add(divinedLierResultString);
        }
        return talkQueue;
    }

    @Override
    public void finish(GameInfo gameInfo, BoardSurface boardSurface) {

    }
}
