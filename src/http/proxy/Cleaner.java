package http.proxy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class Cleaner extends Thread {
    @Override
    public void run() {
        try {
            while (true) {
                long now = new Date().getTime();
                // 遍历Cache文件夹下的所有文件
                String path = "Cache";
                // 获取其file对象
                File file = new File(path);
                File[] fs = file.listFiles();
                for (File f : fs) {
                    Path testPath = Paths.get(f.getPath());
                    BasicFileAttributeView basicView = Files.getFileAttributeView(testPath, BasicFileAttributeView.class);
                    BasicFileAttributes basicFileAttributes = basicView.readAttributes();
                    if ((now - basicFileAttributes.creationTime().toMillis()) / 60000 > 1)
                        f.delete();
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        }
    }
}
