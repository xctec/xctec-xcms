package com.df4j.xctec.xcms.common.jpa.tree;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * 树路径工具。path 用 '/' 分隔的祖先 id 序列；codePath 用 '/' 分隔的编码序列。
 */
public final class TreePath {

    private TreePath() {
    }

    public static final String SEP = "/";

    public static List<Long> parseIds(String path) {
        if (path == null || path.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(path.split(SEP))
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();
    }

    public static List<String> parseCodes(String codePath) {
        if (codePath == null || codePath.isBlank()) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(codePath.split(SEP))
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /** 根据父 path 与自身 id 计算子 path（不含自身） */
    public static String childPath(String parentPath, Long selfId) {
        StringJoiner sj = new StringJoiner(SEP);
        if (parentPath != null && !parentPath.isBlank()) {
            Arrays.stream(parentPath.split(SEP)).filter(s -> !s.isEmpty()).forEach(sj::add);
        }
        sj.add(String.valueOf(selfId));
        return sj.toString();
    }

    /** 根据父 codePath 与自身 code 计算子 codePath（含自身） */
    public static String childCodePath(String parentCodePath, String selfCode) {
        StringJoiner sj = new StringJoiner(SEP);
        if (parentCodePath != null && !parentCodePath.isBlank()) {
            Arrays.stream(parentCodePath.split(SEP)).filter(s -> !s.isEmpty()).forEach(sj::add);
        }
        sj.add(selfCode);
        return sj.toString();
    }

    /** 判断 childPath 是否为 ancestorPath 的后代 */
    public static boolean isDescendant(String ancestorPath, String childPath) {
        if (ancestorPath == null || childPath == null) {
            return false;
        }
        List<Long> anc = parseIds(ancestorPath);
        List<Long> child = parseIds(childPath);
        if (child.size() <= anc.size()) {
            return false;
        }
        for (int i = 0; i < anc.size(); i++) {
            if (!anc.get(i).equals(child.get(i))) {
                return false;
            }
        }
        return true;
    }
}
