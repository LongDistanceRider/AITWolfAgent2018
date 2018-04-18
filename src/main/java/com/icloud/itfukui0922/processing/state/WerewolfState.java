package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

public class WerewolfState implements RoleState {

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        boardSurface.getMyInformation().putDivIdenMap(Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo)), Species.HUMAN);
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface) {
        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- coming out -----
        String comingOutSeerString = UtilState.coming_out(boardSurface.getMyInformation().getAgent(), Role.SEER);
        if (comingOutSeerString != null) {
            talkQueue.add(comingOutSeerString);
        }
        // ----- 占い結果報告 -----
        String divinedResultString = UtilState.divinedResult(boardSurface);
        if (divinedResultString != null) {
            talkQueue.add(divinedResultString);
        }
        return talkQueue;
    }
}
