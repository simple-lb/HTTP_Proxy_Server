package http.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 流通道处理线程
 */
public class HttpsChannel extends Thread {
    private final CountDownLatch countDownLatch;
    private final DataInputStream in;
    private final DataOutputStream out;

    public HttpsChannel(DataInputStream in, DataOutputStream out, CountDownLatch countDownLatch) {
        this.in = in;
        this.out = out;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        int len;
        byte buf[] = new byte[10240];
        try {
            while ((len = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, len);
                out.flush();
            }
        } catch (Exception e) {
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            countDownLatch.countDown();
        }
    }
}