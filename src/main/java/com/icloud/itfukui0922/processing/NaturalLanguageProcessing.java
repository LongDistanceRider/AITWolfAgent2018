package com.icloud.itfukui0922.processing;

import org.aiwolf.common.data.Talk;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;


/**
 * 自然言語処理部門
 * 別スレッドで処理が行われることに留意しプログラミングをすること
 *
 * フィルタリング：フィルタ情報にある単語が文中に含まれていない場合，雑談文として処理をしない
 */
public class NaturalLanguageProcessing implements Callable<Boolean> {

    /* フィルタ情報 */
    private static List<String> filterList = new ArrayList<>();
    /* Talk型 */
    private volatile Talk talk;


    static {
        // フィルタ情報の読み込み
        try {
            File csv = new File("lib/filterInformation.txt");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(csv));
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                filterList.add(readLine);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("フィルタ情報読み込みでエラー" + e);
        } catch (IOException e) {
            System.err.println("フィルタ情報読み込みでエラー" + e);
        }
    }

    /**
     * コンストラクタ
     * @param talk
     */
    public NaturalLanguageProcessing(Talk talk) {
        this.talk = talk;
    }

    /**
     * 別スレッド実行呼び出しCallメソッド
     * @return
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        String text = talk.getText();
        // フィルタリング
        for (String filter :
                filterList) {
            if (text.indexOf(filter) == -1) {
                return false;
            }
        }

        // 複数文の分割
        String[] stringArray = text.split("[!?.。！？]");  // 引数に分割するキーワードを入れる
        List<String> one_sentence_List = new ArrayList<>(Arrays.asList(stringArray));   // 1文ずつリストへ　memo:一度に色々発言する人は少ないから大抵1つしか入らないと思う

        // Mecab
//        Tagger tagger = new Tagger();
//        Node node = tagger.parseToNode(text);
//        // 単語と特徴量
//        Map<String, String> mecabMap = new LinkedHashMap<>();
//        for (; node != null; node.getNext()) {
//            // TODO: BOS/EOSが混ざるんじゃないかなって懸念．テスト必要
//            mecabMap.put(node.getSurface(), node.getFeature()); // keyには単語，valueには特徴量が入る memo:key=太郎	value=名詞,固有名詞,人名,名,*,*,太郎,タロウ,タロー
//        }


        return null;
    }
}
