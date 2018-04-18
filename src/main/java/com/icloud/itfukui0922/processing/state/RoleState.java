package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.LinkedList;

/**
 * デザインパターン「State」
 */
public interface RoleState {

    void dayStart(GameInfo gameInfo, BoardSurface boardSurface);
    LinkedList<String> talk(BoardSurface boardSurface, int day);
    void finish(GameInfo gameInfo, BoardSurface boardSurface);
}
