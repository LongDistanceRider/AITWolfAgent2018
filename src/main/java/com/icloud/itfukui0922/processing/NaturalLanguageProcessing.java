package com.icloud.itfukui0922.processing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.log.LogCategory;
import com.icloud.itfukui0922.log.LogLevel;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.mychaelstyle.nlp.KNP;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 自然言語処理部門
 * 自然言語処理を施し，自然言語文からプロトコル文へ変換する．
 * プロトコル部門で処理できないものについても，このクラスで処理をする．
 *
 * フィルタリング：フィルタ情報にある単語が文中に含まれていない場合，雑談文として処理をしない
 *
 * @author fukurou
 * @version 1.0
 */
public class NaturalLanguageProcessing {

    /* フィルタ情報 */
    private static List<String> filterList = new ArrayList<>();

    static {
        // フィルタ情報の読み込み
        BufferedReader bufferedReader = null;
        try {
            File csv = new File("lib/filterInformation.txt");

            bufferedReader = new BufferedReader(new FileReader(csv));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                filterList.add(readLine);
            }
            Log.trace("フィルタ情報が読み込まれました．");
        } catch (FileNotFoundException e){
            Log.fatal("フィルタ情報のファイルが存在しません．");
        } catch (IOException e){
            Log.fatal("フィルタ情報のファイル入力に失敗しました．");
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.error("フィルタ情報のファイルを閉じる際にIOExceptionが発生しました．");
            }
        }
    }

    /**
     * プロトコル文へ変換
     *
     * @param gameInfo ゲーム情報
     * @param boardSurface 盤面情報
     * @param talk 発言内容
     * @return プロトコル文
     */
    public static List<String> convertPro(GameInfo gameInfo, BoardSurface boardSurface, Talk talk) {
        Log.submit(LogLevel.INFO, LogCategory.NATURAL, "NL発言内容 : " + talk.getAgent() + " > " + talk.getText());   // 処理前発言
        List<String> NLStringList = new ArrayList<>();

        String text = talk.getText();
        // フィルタリング
        for (String filter :
                filterList) {
            if (text.contains(filter)) {
                // 複数文の分割
                String[] stringArray = text.split("[!?.。！？]");  // 引数に分割するキーワードを入れる
                List<String> oneSentenceList = new ArrayList<>(Arrays.asList(stringArray));   // 1文ずつリストへ　memo:一度に色々発言する人は少ないから大抵1つしか入らないと思う

                // ----- 1文ずつ処理 -----
                for (String oneSentence :
                        oneSentenceList) {
                    // KNP
                    KNP knp = new KNP();
                    ObjectNode objectNode = null;
                    try {
                        objectNode = knp.parse(oneSentence);
                    } catch (IOException | InterruptedException e) {
                        Log.submit(LogLevel.ERROR, LogCategory.NATURAL, "knpパース時にエラー発生: " + e);
                        continue;
                    }
                    // submitを取得（私は，僕は，などの言葉）
//                    Agent target = getSubmit(oneSentence);
                    // targetを取得
                    Agent target = getTarget(gameInfo, objectNode);
                    // Roleがあるか（あるならcomingoutとする）
                    Role role = getRole(oneSentence);
                    // Speciesがあるか（あるなら結果を話している（占い師COしていたらdivined 霊能COならiden）
                    Species species = getSpecies(oneSentence);
                    // 投票発言しているか
                    boolean isProvideVote = isProvideVote(oneSentence);

                    // --- 話題判定 ---
                    if (target == null) {
                        continue;   // targetがないなら，どの話題にも当てはまらないため，コンティニュー
                    }
                    if (role != null) { // roleが発見されたらcomingout
                        NLStringList.add("COMINGOUT " + target + " " + role);
                    }
                    if (species != null) { // speciesが発見された上に占い師COしていたら結果報告
                        if (boardSurface.getPlayerInformation(target).getSelfCO().equals(Role.SEER)) {
                            NLStringList.add("DIVINED " + target + " " + species);
                        }
                    }
                    if (isProvideVote) {    // 投票発言
                        NLStringList.add("VOTE " + target);
                    }

                }
            }
            // ログ出力
            for (String NLString :
                    NLStringList) {
                Log.submit(LogLevel.INFO, LogCategory.NATURAL, "PRO発言内容 : " + talk.getAgent() + " > " + NLString);   // 処理後発言
            }
        }


//        Log.submit(LogLevel.DEBUG, LogCategory.NATURAL, objectNode.toString());
        // Mecab
//        Tagger tagger = new Tagger();
//        Node node = tagger.parseToNode(text);
//        // 単語と特徴量
//        Map<String, String> mecabMap = new LinkedHashMap<>();
//        for (; node != null; node.getNext()) {
//            mecabMap.put(node.getSurface(), node.getFeature()); // keyには単語，valueには特徴量が入る memo:key=太郎	value=名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー
//        }


        return NLStringList;
    }


    /**
     * 文中に投票を示す単語がある場合はtrueない場合はfalse
     * @param oneSentence
     * @return
     */
    private static boolean isProvideVote(String oneSentence) {
        boolean isProvideVote = false;
        if (oneSentence.contains("投票")) {
            isProvideVote = true;
        }
        Log.submit(LogLevel.DEBUG, LogCategory.NATURAL, "投票発言か: " + isProvideVote);
        return isProvideVote;
    }

    /**
     * 文中のSpeciesを取得
     * 文中にSpeciesを示す単語があった場合にSpeciesを返す
     * @param oneSentence
     * @return
     */
    private static Species getSpecies (String oneSentence) {
        Species species = null;
        if (oneSentence.contains("白") || oneSentence.contains("人間")) {
            species = Species.HUMAN;
        }
        if (oneSentence.contains("黒") || oneSentence.contains("人狼")) {
            species = Species.WEREWOLF;
        }
        Log.submit(LogLevel.DEBUG, LogCategory.NATURAL, "Species取得: " + species);
        return species;
    }

    /**
     * 文中のRoleを取得
     * 文中に役職を示す単語があった場合にroleを返す
     * @param oneSentence 解析文
     * @return
     */
    private static Role getRole (String oneSentence) {
        Role role = null;
        if (oneSentence.contains("占い師")) {
            role = Role.SEER;
        }
        if (oneSentence.contains("霊能者")) {
            role = Role.MEDIUM;
        }
        if (oneSentence.contains("狩人")) {
            role = Role.BODYGUARD;
        }
        if (oneSentence.contains("裏切り者") || oneSentence.contains("狂人")) {
            role = Role.POSSESSED;
        }
        if (oneSentence.contains("人狼")) {
            role = Role.WEREWOLF;
        }
        if (oneSentence.contains("村人")) {
            role = Role.VILLAGER;
        }
        Log.submit(LogLevel.DEBUG, LogCategory.NATURAL, "role取得: " + role);
        return role;
    }
    /**
     * 文中のtargetを取得
     * 文中に”Agent”の形態素があった場合に，パターンマッチで数字を取り出して，Agent型を特定する
     * Agentは00番から99番までに対応とする
     * @param objectNode
     * @return
     */
    private static Agent getTarget (GameInfo gameInfo, ObjectNode objectNode) {
        List<String> morphemesList = morphemes(objectNode); // 形態素リストを取得
        for (String morphemes :
                morphemesList) {
            if (morphemes.contains("Agent")) {  // Agent の文字があるか
                Pattern pattern = Pattern.compile("[0-9]{2}");
                Matcher matcher = pattern.matcher(morphemes);
                if (matcher.find()) {   // 番号発見
                    List<Agent> agentList = gameInfo.getAgentList();    // Agentリスト参照
                    for (Agent agent :
                            agentList) {
                        if (Integer.parseInt(matcher.group()) == agent.getAgentIdx()) {
                            Log.submit(LogLevel.DEBUG, LogCategory.NATURAL, "target取得: " + agent);
                            return agent;   // target発見
                        } else {
                            Log.submit(LogLevel.WARN, LogCategory.NATURAL, "Agentリストにない番号を取得: " + matcher.group());
                        }
                    }
                } else {
                    Log.submit(LogLevel.WARN, LogCategory.NATURAL, "targetの番号取得に失敗");
                }
            }
        }
        return null;
    }
    /**
     * 形態素のリストを取得する
     * ex: 私は占い師です => 私　は　占い　師　です
     * @param objectNode KNP解析結果
     * @return 形態素のリスト
     */
    private static List<String> morphemes (ObjectNode objectNode) {
        List<String> morphemesList = new ArrayList<>();
        int clauseasNum = objectNode.get("clauseas").size();
        for (int i = 0; i < clauseasNum; i++) {
            int phrasesNum = objectNode.get("clauseas").get(i).get("phrases").size();
            for (int j = 0; j < phrasesNum; j++) {
                int morphemesNum = objectNode.get("clauseas").get(i).get("phrases").get(j).get("morphemes").size();
                for (int k = 0; k < morphemesNum; k++) {
                    String morphemeString = objectNode.get("clauseas").get(i).get("phrases").get(j).get("morphemes").get(k).get("signage").toString();
                    morphemesList.add(morphemeString.replace("\"", ""));    // 形態素の前後にダブルクォーテーションが入るため，これを削除
                }
            }
        }
        return morphemesList;
    }

    /**
     * 文節のリストを取得
     * @param objectNode
     * @return 文節リスト
     */
    private static List<String> phrases (ObjectNode objectNode) {
        List<String> list = new ArrayList<>();

        int clauseasNum = objectNode.get("clauseas").size();
        for (int i = 0; i < clauseasNum; i++) {
            String phraseString = objectNode.get("clauseas").get(i).get("clausea").toString();
            list.add(phraseString);
        }
        return list;
    }
}
