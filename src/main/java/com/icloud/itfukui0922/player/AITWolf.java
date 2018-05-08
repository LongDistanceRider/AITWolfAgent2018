package com.icloud.itfukui0922.player;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.log.LogCategory;
import com.icloud.itfukui0922.log.LogLevel;
import com.icloud.itfukui0922.processing.NaturalLanguageProcessing;
import com.icloud.itfukui0922.processing.ProtocolProcessing;
import com.icloud.itfukui0922.processing.TransNL;
import com.icloud.itfukui0922.processing.state.*;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;
import sun.jvm.hotspot.runtime.VM;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * AITWolfエージェント　メイン部分
 *
 * 可能な限り行数を減らしたい＝クラスの分化
 */

public class AITWolf implements Player {

    /* コンソール出力レベル */
    LogLevel consoleLevel;
    /* ファイル出力レベル */
    LogLevel writeLevel;
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
    private Role roleState = null;

    /**
     * コンストラクタ
     * @param consoleLevel
     * @param writeLevel
     */
    public AITWolf(LogLevel consoleLevel, LogLevel writeLevel) {
        this.consoleLevel = consoleLevel;
        this.writeLevel = writeLevel;
    }

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
            boardSurface.addTalkList(talk);     // 盤面クラスへtalkを保管（自分自身の発言が入る）
            Log.info("発言内容 : " + talk.getAgent() + " > " + talk.getText()); // 発言者 > 発言内容
            if (talk.getAgent().equals(gameInfo.getAgent())) {  // 自分自身の発言はスキップ
                continue;
            }

            String text = talk.getText();
            if (FlagManagement.getInstance().isNLSwitch()) {
                // TODO 自然言語処理をここに書く
                Log.submit(LogLevel.INFO, LogCategory.NATURAL, "NL発言内容 : " + talk.getAgent() + " > " + talk.getText());   // 処理前発言
                // 別スレッド実行
                ExecutorService ex = Executors.newSingleThreadExecutor();
                Future<String> future = ex.submit(new NaturalLanguageProcessing(gameInfo, talk));

                try {
                    text = future.get();    // ここで同期する（talkメソッドに移動予定）
                } catch (InterruptedException e) {
                    Log.fatal("自然言語処理時にInterruptedExceptionが発生: " + e.getMessage());
                } catch (ExecutionException e) {
                    Log.fatal("自然言語処理時にExecutionExceptionが発生: " + e.getMessage());
                }
                Log.submit(LogLevel.INFO, LogCategory.NATURAL, "PRO発言内容 : " + talk.getAgent() + " > " + text);   // 処理前発言
                // 自然言語をプロトコル言語に変換したあと，共通処理で処理する
            } else {
                // TODO プロトコル部門のみの処理をここに書く
                // ログに受け取った発言を出力する
            }
            // TODO NLPとプロトコル共通処理をここに書く
            // Talk内容を読み取り，BoardSurfaceへ保管する
            ProtocolProcessing.updateTalkInfo(talk, text, boardSurface);
        }
        // talkListHeadの更新
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        // ----- ログ出力開始 -----
        Log.init(consoleLevel, writeLevel);   // コンソール出力レベル, ファイル出力レベル
        Log.debug("initialize実行");
        // ----- フィールド初期化処理 -----
        this.gameInfo = gameInfo;   // ゲーム情報の初期化
        this.gameSetting = gameSetting; // ゲーム設定の初期化
        this.boardSurface = new BoardSurface(gameInfo); // 盤面クラスの初期化
        FlagManagement.getInstance().setFinish(false);  // フィニッシュフラグをリセット
    }

    @Override
    public void dayStart() {
        Log.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        Log.info("\t\t" + gameInfo.getDay() + "day start");
        Log.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        // ----- 特定日時に実行させる処理 -----
        switch (gameInfo.getDay()) {
            case 1: // 1日目
                roleSet();  // 役職セット
                boardSurface.getMyInformation().setMyRole(gameInfo.getRole());   // プレイヤ情報に保管
                // ----- 各役職ごとの処理 -----
                roleState.dayStart(gameInfo, boardSurface);
                break;
            case 2: // 2日目
                // 被投票者
                Agent executedAgent = gameInfo.getExecutedAgent();
                boardSurface.getExecutedAgentList().add(executedAgent);
                Log.info("追放者 : " + executedAgent);
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
                    Log.info("被害者 : " + executedAgent);
                } else {
                    // TODO GJ発生時の処理を書くこと
                    Log.info("被害者 : なし（GJ発生）");
                }
                // ----- 各役職ごとの処理 -----
                roleState.dayStart(gameInfo, boardSurface);
                break;
            default:

        }

        // ----- 日をまたぐごとに初期化するフラグ -----
        FlagManagement.getInstance().dayReset();
    }

    @Override
    public String talk() {
        // ----- 0日目挨拶 -----
        if (gameInfo.getDay() == 0) {
            if (FlagManagement.getInstance().isGreeting()) {
                return "Over";
            } else {
                FlagManagement.getInstance().setGreeting(true);
                return "こんにちは";
            }
        }
        // ----- 各役職ごとの処理 -----
        LinkedList<String> roleTalkQueue = roleState.talk(gameInfo, boardSurface); // nullが入ってくることがある
        if (roleTalkQueue != null) {
            talkQueue.addAll(roleTalkQueue);
        }

        if (FlagManagement.getInstance().isNLSwitch()) {
            // TODO 自然言語処理に関する処理をここに書く
        } else {
            // TODO プロトコル部門の処理に関する処理をここに書く
        }
        if (talkQueue != null &&!talkQueue.isEmpty()) { // nullチェックいらないかも
            return talkQueue.poll();
        }
        return "Over";
    }

    @Override
    public String whisper() {
        return null;
    }

    @Override
    public Agent vote() {
        // 生存プレイヤー内（自分自身を除く）からランダムに投票
        Agent voteAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
        Log.info("投票先 : " + voteAgent);
        return voteAgent;
    }

    @Override
    public Agent attack() {
        Agent attackAgent;
        // とりあえず占い師COした人物を対象に，いない場合は霊能者，次に狩人の順で対象を決定する
        List<org.aiwolf.common.data.Role> checkRole = new ArrayList<>(Arrays.asList(org.aiwolf.common.data.Role.SEER, org.aiwolf.common.data.Role.BODYGUARD, org.aiwolf.common.data.Role.BODYGUARD));
        List<Agent> coming_outAgentList;    // 初期化してないけどいいのかな
        for (org.aiwolf.common.data.Role role :
                checkRole) {
            coming_outAgentList = boardSurface.comingoutRoleAgentList(role);
            if(!coming_outAgentList.isEmpty()) {
                attackAgent = Utility.randomElementSelect(coming_outAgentList);    // リストが空でなければリストからランダムに返す
                Log.info("襲撃先 : " + attackAgent);
                return attackAgent;
            }
        }
        attackAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));   // 誰もいなければランダムに返す
        Log.info("襲撃先 : " + attackAgent);
        return attackAgent;
    }

    @Override
    public Agent divine() {
        // 生存プレイヤー内（自分自身を除く）からランダムに占う
        Agent divineAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
        Log.info("占い先 : " + divineAgent);
        return divineAgent;
    }

    @Override
    public Agent guard() {
        Agent guardAgent;
        // ◯-◯進行をチェック
        // 占い師1ならその占い師を，2以上なら霊能者を護衛（霊能者が二人の時は適当に選ぶ），霊能者が不在なら適当に選ぶ
        List<Agent> comingoutSeerAgentList = boardSurface.comingoutRoleAgentList(org.aiwolf.common.data.Role.SEER);
        List<Agent> comingoutMediumAgentList = boardSurface.comingoutRoleAgentList(org.aiwolf.common.data.Role.MEDIUM);

        int numberOfSeer = comingoutSeerAgentList.size();
        int numberOfMedium = comingoutMediumAgentList.size();

        if (numberOfSeer == 1) {
            guardAgent = comingoutSeerAgentList.get(0);    // 占い師護衛
            Log.info("護衛先 : " + guardAgent);
            return guardAgent;
        } else if (numberOfSeer > 1 && numberOfMedium == 1) {
            guardAgent = comingoutMediumAgentList.get(0);  // 霊能者護衛
            Log.info("護衛先 : " + guardAgent);
            return guardAgent;
        }
        guardAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));   // 適当なプレイヤーを返す
        Log.info("護衛先 : " + guardAgent);
        return guardAgent;
    }

    @Override
    public void finish() {
        if (FlagManagement.getInstance().isFinish()) {  // finishが2回目に呼び出されるとき，処理をしない
            return;
        }
        FlagManagement.getInstance().setFinish(true);
        Log.debug("finish実行");
        // TODO メモリ，フィールドの初期化


        // 役職finish()時の処理
        roleState.finish(gameInfo, boardSurface);

        // 参加プレイヤのリザルト出力
        Map<Agent, org.aiwolf.common.data.Role> agentMap = gameInfo.getRoleMap();
        for (Agent agent:
                agentMap.keySet()){
            Log.info(agent + " Role : " + agentMap.get(agent));
        }
        // ----- ログ出力停止 -----
        Log.endLog();
    }

    /**
     * 役職セット
     */
    private void roleSet() {
        org.aiwolf.common.data.Role role = gameInfo.getRole();
        Log.info("自分の役職 : " + role);
        switch (role) {
            case SEER:
                roleState = new Seer(gameInfo, boardSurface);
                break;
            case MEDIUM:
                roleState = new Medium(gameInfo, boardSurface);
                break;
            case BODYGUARD:
                roleState = new Bodyguard(gameInfo, boardSurface);
                break;
            case POSSESSED:
                roleState = new Possessed(gameInfo, boardSurface);
                break;
            case WEREWOLF:
                roleState = new Werewolf(gameInfo, boardSurface);
                break;
            default:
                roleState = new Villager(gameInfo, boardSurface);
        }
    }


}
