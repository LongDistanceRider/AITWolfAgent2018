package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.dice.BoardSurface;
import org.aiwolf.common.net.GameInfo;

/**
 * 強化学習「ダイス」の抽象化クラス
 */
public abstract class Dice {

    DiceState diceState;

    /* Qランダム値の最大値 */
    protected static final int INIT_Q_MAX = 30;
    /* ε-greedy法のε */
    protected static final double EPSILON = 0.3;
    /* 学習率 */
    protected static final double ALPHA = 0.1;
    /* 割引率 */
    protected static final double GAMMA = 0.9;

    public Dice(DiceState diceState) {
        this.diceState = diceState;
    }

    public abstract boolean setDiceState(GameInfo gameInfo, BoardSurface boardSurface); // ダイス状態セット
    public abstract int shakeTheDice(); // ダイスを振る
    public abstract void updateQTable(GameInfo gameInfo, BoardSurface boardSurface);
    protected abstract int reward();
    protected abstract void routeRecord(int action);
    protected abstract void initQ();
}
