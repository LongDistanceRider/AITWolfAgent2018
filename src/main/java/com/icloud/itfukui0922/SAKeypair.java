/**
 * Qlearningで使われるQvalueMapのキーとして利用する構造体
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;

public class SAKeypair {
    BoardSurface state; // 盤面情報
    Action action;  // 状態sにおいて取りうる行動a

    public SAKeypair(BoardSurface state, Action action) {
        this.state = state;
        this.action = action;
    }

    public BoardSurface getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public boolean equals(Object obj) {
        // return super.equals(obj);
        if (obj == null) {
            return false;
        } if (!(obj instanceof SAKeypair)) {
            return false;
        }
        return this.state == ((SAKeypair)obj).getState() && this.action == ((SAKeypair)obj).getAction();
    }
}
