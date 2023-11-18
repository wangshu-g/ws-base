package com.ws.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtil {

    public static void copyFolderToFolder(String folder1, String folder2, boolean coverExistFile) throws IOException {
        new File(folder2).mkdirs();
        File fileList = new File(folder1);
        String[] fileNameArr = fileList.list();
        File temp;
        if (Objects.isNull(fileNameArr)) {
            return;
        }
        for (String fileName : fileNameArr) {
            if (folder1.endsWith(File.separator)) {
                temp = new File(StringUtil.concat(folder1, fileName));
            } else {
                temp = new File(StringUtil.concat(folder1, File.separator, fileName));
            }
            if (temp.isFile()) {
                File file = new File(StringUtil.concat(folder2, File.separator, temp.getName()));
                if (!file.exists() || coverExistFile) {
                    Files.copy(Path.of(temp.getAbsolutePath()), new FileOutputStream(file));
                }
            } else if (temp.isDirectory()) {
                copyFolderToFolder(StringUtil.concat(folder1, File.separator, fileName), StringUtil.concat(folder2, File.separator, fileName), coverExistFile);
            }
        }
    }

}
