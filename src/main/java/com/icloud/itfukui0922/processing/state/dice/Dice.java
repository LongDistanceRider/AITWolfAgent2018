package com.icloud.itfukui0922.processing.state.dice;

import java.util.List;
import java.util.Map;

public class Dice {

    /* Qランダム値の最大値 */
    protected static final int INIT_Q_MAX = 30;
    /* ε-greedy法のε */
    protected static final double EPSILON = 0.3;
    /* 学習率 */
    protected static final double ALPHA = 0.1;
    /* 割引率 */
    protected static final double GAMMA = 0.9;
}
