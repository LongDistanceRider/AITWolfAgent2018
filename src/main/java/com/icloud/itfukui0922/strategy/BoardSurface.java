/**
 * ゲームの盤面整理
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Role;

public class BoardSurface {

    /* プレイヤ情報 */

    /* 自分のプレイヤ情報 */
    int myPlayerInfomation = 0;

    /* シングルトン */
    private static BoardSurface boardSurface = new BoardSurface();
    private BoardSurface(){}
    public static BoardSurface getInstance() {
        return boardSurface;
    }

    public inisialize(){

    }
}
