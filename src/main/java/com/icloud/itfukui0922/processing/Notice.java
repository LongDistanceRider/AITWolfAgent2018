package com.icloud.itfukui0922.processing;

import org.aiwolf.client.lib.Topic;

import java.util.LinkedList;

/**
 * 状況変化に対応し，必要に応じて発言するクラス
 */
public class Notice {
    /* 発言キュー */
    private LinkedList<String> talkQueue = new LinkedList<>();

    public void topicHook(Topic topic) {
        // coming out によってhook
        switch (topic) {
            case COMINGOUT:
                // 自身が占い師，
                break;
        }
    }

}
