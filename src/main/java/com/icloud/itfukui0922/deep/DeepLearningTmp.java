package com.icloud.itfukui0922.deep;

import com.icloud.itfukui0922.Action;
import com.icloud.itfukui0922.strategy.BoardSurface;

import java.util.Stack;

public class DeepLearningTmp {

    public static Action decisionMaking (Stack<BoardSurface> boardSurface) {
        return Action.values()[((int)(Math.random() * Action.values().length))];
    }
}
