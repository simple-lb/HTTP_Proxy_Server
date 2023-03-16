package http.proxy;

import java.io.*;

/**
 * 解析并保存客户的请求头
 */
public class Headers {

    // POST或GET
    private String method;
    // 请求的URL
    private String url;
    // 目标主机
    private String host;
    // 端口默认为80(http),https为443
    private int port = 80;
    // 保存整个头部
    private String headers;
    private int totalByteRead;
    private byte[] response= new byte[65535];

    public Headers() {
        this.headers = new String();
    }

    /**
     * 接收并解析来自客户的请求报文
     */
    public void recieve(InputStream inFromClient) throws IOException {
        byte[] buff = new byte[65535];
        // 设置一个小延迟等待数据传输
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (inFromClient.available() > 0) {
            int byteRead = inFromClient.read(buff);
            for (int i = 0; i < byteRead && this.totalByteRead + i < 100000; i++) {
                this.response[i + this.totalByteRead] = buff[i];
            }
            this.totalByteRead += byteRead;
            // 再次设置一个小延迟确保数据传输完整
            if (inFromClient.available() <= 0)
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }

        headers = new String(this.response).substring(0,totalByteRead);
        String[] st = headers.split("\r\n");
        this.resolveMethod(st[0]);
        for (int i = 1 ;i<st.length;i++)
            if (st[i].startsWith("Host"))
            {
                this.resolveHost(st[i]);
                break;
            }
        if ("\r\n".equals(this.headers.substring(this.headers.length() - 2)))
            this.headers = this.headers.substring(0, this.headers.length() - 2);
    }

    /**
     * 解析首部行
     *
     * @param line Method URL Version
     */
    public void resolveMethod(String line) {
        String[] temp = line.split(" ");
        this.method = temp[0];
        if (!"CONNECT".equals(temp[0]))
            this.url = temp[1];
        else
            this.url = temp[1].split(":443")[0];
    }

    /**
     * 解析主机和端口
     *
     * @param line Host: host:port
     */
    public void resolveHost(String line) {
        String[] temp = line.split(" ");
        // 获得 host:port
        String[] hostAndPort = temp[1].split(":");
        host = hostAndPort[0];
        // 解析端口, 没有则使用默认端口
        if (hostAndPort.length > 1) {
            port = Integer.valueOf(hostAndPort[1]);
        }
    }

    // 插入if modified since
    public void insertIfModifiedSince(String date) {
        String ifModifiedSince = date.replace
          ("Last-Modified", "If-Modified-Since") + "\r\n";
        this.headers += ifModifiedSince;
    }

    public String getHeaders() {
        // 加上一个\r\n保证格式正确
        headers += "\r\n";
        return this.headers;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String url) {
        this.host = url;
    }

    public int getPort() {
        return this.port;
    }

    public String getMethod() {
        return this.method;
    }

    public String getURL() {
        return this.url;
    }
}
