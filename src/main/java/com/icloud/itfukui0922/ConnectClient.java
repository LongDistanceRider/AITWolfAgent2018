package com.icloud.itfukui0922;

import com.icloud.itfukui0922.log.LogLevel;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.TcpipClient;

/**
 * このクラスはサーバにクライアントを接続するためのmainクラスです．
 * jarファイル作成時にはこのクラスをマニフェストファイルに記述してください．
 *
 * 利用可能オプション
 *  -h: ホスト名を指定（デフォルト値 = "localhost"）
 *  -p: ポート番号を指定 （デフォルト値 = 10000）
 *  -r: 希望役職
 *  -n: 自然言語処理をするか （デフォルト値 = false）ex: '-n true' or '-n false'
 */
public class ConnectClient {

    public static void main (String[] args) {
        String host = "localhost";  // ホスト名
        int port = 10000;   // ポート番号
        Role role = null;
        boolean isNLP = false;

        // コマンドライン引数のチェック
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    host = args[i + 1];
                    i++;
                    break;
                case "-p":
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                    break;
                case "-r":
                    role = Role.valueOf(args[i + 1]);
                    i++;
                    break;
                case "-n":
                    isNLP = Boolean.getBoolean(args[i + 1]);
                    i++;
                    break;
                case "--help":
                    System.out.println("-hでホスト指定，-pでポート指定");
                    return;
            }

        }

        TcpipClient client;
        if (role != null) {
            client = new TcpipClient(host, port, role);
        } else {
            client = new TcpipClient(host, port);
        }
        Player player = new AITWolf(LogLevel.FATAL, LogLevel.FATAL, isNLP);
        client.connect(player);
        client.setName(player.getName());
    }
}
