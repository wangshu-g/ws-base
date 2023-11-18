package com.ws.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StringUtil {

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "null".equals(str) || "".equals(String.valueOf(str).replaceAll(" ", "")));
    }

    public static boolean isNotEqual(String str1, String str2) {
        return !isEqual(str1, str2);
    }

    public static boolean isEqual(String str1, String str2) {
        String s1 = str1 == null ? "" : str1;
        String s2 = str2 == null ? "" : str2;
        return s1.equals(s2);
    }

    public static boolean isEqualIgnoreCase(String str1, String str2) {
        String s1 = str1 == null ? "" : str1;
        String s2 = str2 == null ? "" : str2;
        return s1.equalsIgnoreCase(s2);
    }

    public static @NotNull String concat(String @NotNull ... str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : str) {
            stringBuilder.append(item);
        }
        return stringBuilder.toString();
    }

    public static @NotNull String getAssignString(int @NotNull [] assign, @NotNull String str) {
        StringBuilder result = new StringBuilder();
        for (int item : assign) {
            result.append(str.charAt(item));
        }
        return result.toString();
    }

    public static @Nullable List<String> enumToListStr(@NotNull Class<? extends Enum> enumType) {
        if (enumType.isEnum()) {
            Enum[] enums = enumType.getEnumConstants();
            return Arrays.stream(enums).map(Enum::name).collect(Collectors.toList());
        }
        return null;
    }

}
