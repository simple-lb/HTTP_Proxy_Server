package http.proxy;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 代理服务器主函数 监听并提供代理
 */
public class Main {
    // 代理端口
    private static int PORT = 8888;

    public static void main(String[] args) throws Exception {
        // 欢迎套接字
        ServerSocket welcomeSocket = new ServerSocket(PORT);
        // 创建缓存线程池, 可无限大
        ExecutorService executor = Executors.newCachedThreadPool();

        // 监听
        System.out.println("代理服务器正在运行, 监听端口 "+PORT);

        // 获取数据库中的数据
        Filter.getData();
        // 定时清理缓存
        new Cleaner().start();

        // 不断监听
        while (true) {
            Socket proxySocket = welcomeSocket.accept();
            // 创建代理进程
            Proxy proxy = new Proxy(proxySocket);
            executor.execute(proxy);
        }
    }
}
