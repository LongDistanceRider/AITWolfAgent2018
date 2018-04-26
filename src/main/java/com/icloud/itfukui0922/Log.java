package com.icloud.itfukui0922;


import java.io.*;
import java.util.*;

/**
 * ログ出力クラス
 * LogCategoryはファイル出力時，カテゴリーごとにファイルを分ける役割
 */
public class Log {

    /* コンソール出力レベル */
    private static LogLevel consoleLevel = LogLevel.INFO;   // default:INFO以上の情報を出力
    /* ファイル出力レベル */
    private static LogLevel writeLevel = LogLevel.INFO;     // default:INFO以上の情報をファイル出力
    /* ログ出力先パス */
    private static String logFilePath = "./log/";
    /* 開始時間 */
    private static long startTimeInMillis = 0;
    /* ファイル出力用ログ保管 */
    private static Map<LogLevel, String> generalLogHashMap = new LinkedHashMap<>();
    private static Map<LogLevel, String> naturalLogHashMap = new LinkedHashMap<>();
    private static Map<LogLevel, String> reinforLogHashMap = new LinkedHashMap<>();

    static {
        // ログ名のために日付を取得
        Date startDate = new Date();
        logFilePath += startDate.toString();
    }

    /**
     * コンソール出力レベルのセット
     * @param consoleLevel
     */
    public static void setConsoleLevel(LogLevel consoleLevel) {
        Log.consoleLevel = consoleLevel;
    }

    /**
     * ファイル出力レベルのセット
     * @param writeLevel
     */
    public static void setWriteLevel(LogLevel writeLevel) {
        Log.writeLevel = writeLevel;
    }

    /*
     * ログ出力メソッド
     */
    public static void fatal(String message) {
        submit(LogLevel.FATAL, LogCategory.GENERAL, message);
    }
    public static void error(String message) {
        submit(LogLevel.ERROR, LogCategory.GENERAL, message);
    }
    public static void warn(String message) {
        submit(LogLevel.WARN, LogCategory.GENERAL, message);
    }
    public static void info(String message) {
        submit(LogLevel.INFO, LogCategory.GENERAL, message);
    }
    public static void debug(String message) {
        submit(LogLevel.DEBUG, LogCategory.GENERAL, message);
    }
    public static void trace(String message) {
        submit(LogLevel.TRACE, LogCategory.GENERAL, message);
    }
    public static void submit(LogLevel logLevel, LogCategory logCategory, String message) {
        // 呼び出し元クラスとメソッドをくっつける
        String inMessage = "[" + getClassName() + ":" + getMethodName() + "] " + message;
        // コンソール出力
        if (consoleLevel.ordinal() <= logLevel.ordinal()) { // コンソール出力レベル以上のものだけコンソール出力
            String consoleMessage = logCategory + " : " + logLevel + "\t" + inMessage;
            System.out.println(consoleMessage);
        }
        // ----- ファイル出力用保管処理 -----
        if (writeLevel.ordinal() <= logLevel.ordinal()) {   // ファイル出力レベル以上のものだけをコンソール出力
            // logListに入れるマップ作成
            Map<LogLevel, String> submitMap = new HashMap<>();
            submitMap.put(logLevel, inMessage);
            switch (logCategory) {
                case GENERAL:
                    generalLogHashMap.put(logLevel, inMessage);
                    break;
                case NATURAL:
                    naturalLogHashMap.put(logLevel, inMessage);
                    break;
                case REINFORCEMENT:
                    naturalLogHashMap.put(logLevel, inMessage);
                    break;
            }
        }
    }

    /**
     * ログ出力開始
     */
    public static void init() {
        // ヘッダ出力
        System.out.println(reHeader(LogCategory.GENERAL));
    }

    /**
     * ログ出力停止
     */
    public static void endLog() {
        // フッタ出力
        System.out.println(reFooter());
        // ファイル書き出し

        // GENERALの書き出し
        writeLogFile(LogCategory.GENERAL, generalLogHashMap);
        // NATURALの書き出し
        writeLogFile(LogCategory.NATURAL, naturalLogHashMap);
        // Reinforcementの書き出し
        writeLogFile(LogCategory.REINFORCEMENT, reinforLogHashMap);
    }

    /**
     * ログファイルの書き出し
     *
     * @param logCategory カテゴリ別にファイル出力
     * @param logHashMap 出力するログ保管マップ
     */
    private static void writeLogFile(LogCategory logCategory, Map<LogLevel, String> logHashMap) {
        String fileName = logFilePath + logCategory.name().toLowerCase() + ".log";   // ファイル名
        try {
            File file = new File(fileName);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));    // ストリーム

            // ヘッダーの書き出し
            List<String> header = new ArrayList<>(reHeader(logCategory));
            for (String string :
                    header) {
                printWriter.println(string);
            }
            // ログ本体書き出し
            for (Map.Entry<LogLevel, String> map :
                    logHashMap.entrySet()) {
                printWriter.println(map.getKey() + " : " + map.getValue());     // 書き出し
            }
            // フッタの書き出し
            List<String> footer = new ArrayList<>(reFooter());
            for (String string :
                    footer) {
                printWriter.println(string);
            }

            printWriter.close();    // クローズ
        } catch(IOException e) {
            Log.error("ログファイル出力でIOException発生");
        }
    }

    /**
     * 呼び出し元クラス名を取得
     * @return クラス名
     */
    private static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

    /**
     * 呼び出し元メソッド名を取得
     * @return メソッド名
     */
    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /**
     * ヘッダに書き込む情報を返す
     * @return
     */
    private static List<String> reHeader(LogCategory logCategory) {
        List<String> writeList = new ArrayList<>();
        // HEAD
        writeList.add("==================================================");
        // 開始時間
        startTimeInMillis = Calendar.getInstance().getTimeInMillis();
        writeList.add("開始時刻 : " + startTimeInMillis);
        // 処理名称
        writeList.add("カテゴリー : " + logCategory);
        // FOOTER
        writeList.add("==================================================");
        return writeList;
    }

    /**
     * フッタに書き込む情報
     * @return
     */
    private static List<String> reFooter() {
        List<String> writeList = new ArrayList<>();
        // HEAD
        writeList.add("==================================================");
        // 終了時間
        long endTimeInMillis = Calendar.getInstance().getTimeInMillis();
        writeList.add("終了時刻 : " + endTimeInMillis);
        // 所要時間
        long timeSpend = endTimeInMillis - startTimeInMillis;
        writeList.add("所要時間 : " + timeSpend);
        // FOOTER
        writeList.add("==================================================");
        return writeList;
    }
}
