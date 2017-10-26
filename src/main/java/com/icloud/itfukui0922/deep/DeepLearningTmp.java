package com.icloud.itfukui0922.deep;

import com.icloud.itfukui0922.Action;
import com.icloud.itfukui0922.strategy.BoardSurface;

public class DeepLearningTmp {


    public  Action decisionMaking (BoardSurface boardSurface) {
        return Action.values()[((int)(Math.random() * Action.values().length))];
    }
}
