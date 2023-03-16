package http.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 网站过滤：允许、不允许访问某些网站
 * 用户过滤：支持、不支持某些用户访问外部网站
 * 网站引导：将用户对某个网站的访问引导至另一个网站（钓鱼）
 */
public class Filter {

    // 不允许访问的网站
    private static List<String> disallowedWebsites = new ArrayList<>();
    // 不支持访问外部网络的用户
    private static List<String> disallowedUsers = new ArrayList<>();
    // 需要引导的网站
    private static List<String> phishingWebsites = new ArrayList<>();

    private static String forbidden = "HTTP/1.1 403 Forbidden\r\n" +
      "Server: nginx\r\n";

    public static void getData() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/website";
        String user = "root";
        String password = "123456";

        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        String sql1 = "select * from forbidden;";
        String sql2 = "select * from user;";
        String sql3 = "select * from phishing;";
        ResultSet rs1 = stmt.executeQuery(sql1);
        while (rs1.next()) {
            disallowedWebsites.add(rs1.getString(1));
        }
        ResultSet rs2 = stmt.executeQuery(sql2);
        while (rs2.next()) {
            disallowedUsers.add(rs2.getString(1));
        }
        ResultSet rs3 = stmt.executeQuery(sql3);
        while (rs3.next()) {
            phishingWebsites.add(rs3.getString(1));
        }
    }

    /**
     * 过滤网站
     */
    public static boolean isDisallowedWebsites(String url) {
        for (String disallowedSite : disallowedWebsites) {
            if (url.contains(disallowedSite)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 过滤用户
     *
     * @param user 用户
     */
    public static String isDisalloedUsers(String user) {
        for (String disallowedUser : disallowedUsers) {
            if (disallowedUser.equals(user)) {
                return forbidden;
            }
        }
        return null;
    }

    /**
     * 钓鱼
     * @param url 客户请求访问的网站
     */
    public static boolean leadToPhishingWebsite(String url) {
        for (String phishingWebsite : phishingWebsites) {
            if (url.contains(phishingWebsite)) {
                return true;
            }
        }
        return false;
    }

}
