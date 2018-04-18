package com.icloud.itfukui0922.player;

import com.icloud.itfukui0922.processing.ProtocolProcessing;
import com.icloud.itfukui0922.processing.state.*;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * AITWolfエージェント　メイン部分
 *
 * 可能な限り行数を減らしたい＝クラスの分化
 */

public class AITWolf implements Player {

    /* 自然言語処理部門対応スイッチ */
    private boolean NLSwitch = false;   // 自然言語処理部門に参加する場合はtrue，プロトコル部門はfalse
    /* トークリストをどこまで読み込んだか */
    private int talkListHead;
    /* ゲーム情報 */
    private GameInfo gameInfo;
    /* ゲーム設定情報 */
    private GameSetting gameSetting;
    /* 盤面クラス */
    private BoardSurface boardSurface;
    /* 発言リスト */
    private LinkedList<String> talkQueue = new LinkedList<>();
    /* 自分自身の役職 */
    private RoleState roleState = null;

    @Override
    public String getName() {
        return "AITWolf";
    }   // プレイヤ名を返す

    @Override
    public void update(GameInfo gameInfo) {
        this.gameInfo = gameInfo;   // ゲーム情報更新

        // 発言内容取得
        for (int i = talkListHead; i < gameInfo.getTalkList().size(); i++) {
            Talk talk = gameInfo.getTalkList().get(i);  // 新規Talkを取得
            boardSurface.addTalkList(talk);     // 盤面クラスへtalkを保管
            // TODO talkに対する処理をここに書く
            if (NLSwitch) {
                // TODO 自然言語処理をここに書く
                // 自然言語をプロトコル言語に変換したあと，共通処理で処理する
            } else {
                // TODO プロトコル部門のみの処理をここに書く
            }
            // TODO NLPとプロトコル共通処理をここに書く
            // Talk内容を読み取り，BoardSurfaceへ保管する
            ProtocolProcessing.updateTalkInfo(talk, boardSurface);
        }
        // talkListHeadの更新
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        // ----- フィールド初期化処理 -----
        this.gameInfo = gameInfo;   // ゲーム情報の初期化
        this.gameSetting = gameSetting; // ゲーム設定の初期化
        this.boardSurface = new BoardSurface(gameInfo); // 盤面クラスの初期化
    }

    @Override
    public void dayStart() {
        // ----- 特定日時に実行させる処理 -----
        switch (gameInfo.getDay()) {
            case 0: // 0日目
                if (NLSwitch) {
                    // 挨拶を返す
                    if (!FlagManagement.getInstance().isGreeting()) {
                        talkQueue.add("0日目の挨拶です．");
                        FlagManagement.getInstance().setGreeting(true);
                    }
                    return;
                }
                break;

            case 1: // 1日目
                roleSet();  // 役職セット
                boardSurface.getMyInformation().setMyRole(gameInfo.getRole());   // プレイヤ情報に保管
                break;
            case 2: // 2日目
                // 被投票者
                Agent executedAgent = gameInfo.getExecutedAgent();
                boardSurface.getExecutedAgentList().add(executedAgent);
                // 被噛み　null の場合はGJ発生
                Agent attackedAgent = null;
                for (Agent agent :
                        gameInfo.getLastDeadAgentList()) {  // ここには前日の追放者と被害者の2つのAgentが入ってくると思っているが正しいか？
                    if (!agent.equals(executedAgent)) {
                        attackedAgent = agent;
                    }
                }
                if (attackedAgent != null) {
                    boardSurface.getBiteAgentList().add(attackedAgent);
                } else {
                    // TODO GJ発生時の処理を書くこと
                }

                break;
            default:

        }
        // ----- 各役職ごとの処理 -----
        roleState.dayStart(gameInfo, boardSurface);
        // ----- 日をまたぐごとに初期化するフラグ -----
        FlagManagement.getInstance().setResultReport(false);
    }

    @Override
    public String talk() {
        // ----- 各役職ごとの処理 -----
        LinkedList<String> roleTalkQueue = roleState.talk(boardSurface, gameInfo.getDay());
        if (NLSwitch) {
            // TODO 自然言語処理に関する処理をここに書く
        } else {
            // TODO プロトコル部門の処理に関する処理をここに書く
        }
        if (!roleTalkQueue.isEmpty()) {
            return roleTalkQueue.poll();
        }
        return "OVER";
    }

    @Override
    public String whisper() {
        return null;
    }

    @Override
    public Agent vote() {
        // 生存プレイヤー内（自分自身を除く）からランダムに投票
        return Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
    }

    @Override
    public Agent attack() {
        // とりあえず占い師COした人物を対象に，いない場合は霊能者，次に狩人の順で対象を決定する
        List<Role> checkRole = new ArrayList<>(Arrays.asList(Role.SEER, Role.BODYGUARD, Role.BODYGUARD));
        List<Agent> coming_outAgentList;    // 初期化してないけどいいのかな
        for (Role role :
                checkRole) {
            coming_outAgentList = boardSurface.comingoutRoleAgentList(role);
            if(!coming_outAgentList.isEmpty()) {
                return Utility.randomElementSelect(coming_outAgentList);    // リストが空でなければリストからランダムに返す
            }
        }
        return Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));   // 誰もいなければランダムに返す
    }

    @Override
    public Agent divine() {
        // 生存プレイヤー内（自分自身を除く）からランダムに占う
        return Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
    }

    @Override
    public Agent guard() {
        // ◯-◯進行をチェック
        // 占い師1ならその占い師を，2以上なら霊能者を護衛（霊能者が二人の時は適当に選ぶ），霊能者が不在なら適当に選ぶ
        List<Agent> comingoutSeerAgentList = boardSurface.comingoutRoleAgentList(Role.SEER);
        List<Agent> comingoutMediumAgentList = boardSurface.comingoutRoleAgentList(Role.MEDIUM);

        int numberOfSeer = comingoutSeerAgentList.size();
        int numberOfMedium = comingoutMediumAgentList.size();

        if (numberOfSeer == 1) {
            return comingoutSeerAgentList.get(0);    // 占い師護衛
        } else if (numberOfSeer > 1 && numberOfMedium == 1) {
            return comingoutMediumAgentList.get(0);  // 霊能者護衛
        }
        return Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));   // 適当なプレイヤーを返す
    }

    @Override
    public void finish() {
        // TODO メモリ，フィールドの初期化


        // 役職finish()時の処理
        roleState.finish(gameInfo, boardSurface);
    }

    /**
     * 役職セット
     */
    private void roleSet() {
        switch (gameInfo.getRole()) {
            case SEER:
                roleState = new SeerState(gameMaxDay());
                break;
            case MEDIUM:
                roleState = new MediumState();
                break;
            case BODYGUARD:
                roleState = new BodyguardState();
                break;
            case POSSESSED:
                roleState = new PossessedState();
                break;
            case WEREWOLF:
                roleState = new WerewolfState();
                break;
            default:
                roleState = new VillagerState();
        }
    }

    /**
     * ゲーム参加者数からゲーム最大日数を取得
     */
    private int gameMaxDay() {
        int numPlayer = gameInfo.getAgentList().size();
        int maxDay = (numPlayer-1) / 2;
        return maxDay;
    }
}
