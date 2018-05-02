package com.icloud.itfukui0922.util;

public class DiceUtil {

    /**
     * booleanをintに変換
     * @param boo
     * @return
     */
    public static int convertInteger (boolean boo) {
        if (boo) {
            return 1;
        } else {
            return 0;
        }
    }
}
