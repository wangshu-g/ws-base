package com.ws;

import com.ws.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class IClassLoader extends ClassLoader {

    private String classFilePath = null;

    public IClassLoader(String classFilePath) {
        this.classFilePath = classFilePath;
    }

    @Override
    protected Class<?> findClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ignored) {
        }
        if (Objects.nonNull(clazz)) {
            return clazz;
        }
        try {
            clazz = getParent().loadClass(className);
        } catch (ClassNotFoundException ignored) {
        }
        if (Objects.nonNull(clazz)) {
            return clazz;
        }
        byte[] classBytes = null;
        try {
            String classUri = StringUtil.concat(classFilePath, className.replaceAll("\\.", "/"), ".class");
            classBytes = Files.readAllBytes(Path.of(classUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clazz = defineClass(className, classBytes, 0, classBytes.length);
        return clazz;
    }

}
