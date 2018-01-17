/**
 * 盤面状態が変化した時に，注視すべき状態が発生しているかをチェックするクラス
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Role;

import java.util.ArrayDeque;

public class Annotation {

    // TODO AnnotationQueueがStringで管理されている問題を解決する
    private ArrayDeque<String> annotationQueue = new ArrayDeque<>();

    /**
     * アノテーションキューのゲッター
     * @return アノテーションキュー
     */
    public ArrayDeque<String> getAnnotationQueue() {
        return annotationQueue;
    }


    /**
     * Comingoutがあった際にチェックする項目
     * @param playerInformation
     * @param selfCO
     */
    public void comingoutCheck(PlayerInformation playerInformation, Role selfCO) {
        if (isCoSlide(playerInformation.getSelfCO(), selfCO)) {// COスライドチェック
            annotationQueue.add("coSlide");
        }
    }

    /**
     * COスライドチェック
     */
    private boolean isCoSlide(Role preSelfCO, Role selfCO) {
        if (preSelfCO != null && preSelfCO != selfCO ) {
            return true;
        }
        return false;
    }

}
