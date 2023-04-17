package com.example.utils;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class JsonDataGenerator {
    private static int globalId = 0;
    private static double globalSortIndex = 0;

    public static List<Element> createNestedStructure(String titlePrefix, String type, String content_type, String contentPrefix, int depth, int maxDepth) {
        List<Element> resultList = new ArrayList<>();
        if (depth > maxDepth) {
            return resultList;
        }

        Random random = new Random();
        int childCount = random.nextInt(3) + 1; // 随机生成1到3个子元素

        List<Element> contents = new ArrayList<>();
        String sectionTitle = titlePrefix + " " + depth;
        Element section = new Element("", sectionTitle, type, content_type, sectionTitle, BigDecimal.ZERO, contents);

        for (int i = 1; i <= childCount; i++) {
            if (depth < maxDepth) {
                resultList.addAll(createNestedStructure(titlePrefix, type, content_type, contentPrefix, depth + 1, maxDepth));
            } else {
                int next = random.nextInt(2);
                if (next < 1) {
                    String sectionTitle2 = "Section Inisde " + depth + "." + i;
                    Element section2 = new Element("", sectionTitle2, "section", content_type, sectionTitle2, BigDecimal.ZERO, new ArrayList<>());
                    contents.add(section2);
                } else {
                    String paragraphTitle = "Paragraph " + depth + "." + i;
                    Element paragraph = new Element("", paragraphTitle, "paragraph", content_type, paragraphTitle, BigDecimal.ZERO, null);
                    contents.add(paragraph);
                }
            }
        }
        resultList.add(section);

        List<Element> temp = getNodes(resultList);
        assignSortIndex(temp);
        assignId(resultList);
        assignLevel(resultList);
        return resultList;
    }

    public static Comparator<Element> elementComparator = new Comparator<Element>() {
        @Override
        public int compare(Element o1, Element o2) {
            return o1.sortIndex.compareTo(o2.sortIndex);
        }
    };

    public static void collectAllElements(Element element, List<Element> result) {
        result.add(element);
        if (element.contents != null) {
            Collections.sort(element.contents, elementComparator);
            for (Element child : element.contents) {
                collectAllElements(child, result);
            }
        }
    }

    public static void assignId(List<Element> elements) {
        if (elements == null || elements.size() == 0) {
            return;
        }
        for (int i = 0; i < elements.size(); i++) {
            Element child = elements.get(i);
            dfsId(child, i + 1 + "");
        }
    }

    public static void dfsId(Element element, String id) {
        element.id = id;
        if (element.contents != null) {
            for (int i = 0; i < element.contents.size(); i++) {
                Element child = element.contents.get(i);
                if ("section".equals(child.type)) {
                    dfsId(child, id + "." + (i + 1));
                }
            }
        }
    }

    public static void assignLevel(List<Element> elements) {
        if (elements == null || elements.size() == 0) {
            return;
        }
        for (int i = 0; i < elements.size(); i++) {
            Element child = elements.get(i);
            dfsLevel(child, i + 1 + "");
        }
    }

    public static void dfsLevel(Element element, String level) {
        element.level = level;
        if (element.contents != null) {
            int sectionCount = 0;
            for (int i = 0; i < element.contents.size(); i++) {
                Element child = element.contents.get(i);
                if ("section".equals(child.type)) {
                    dfsLevel(child, level + "." + (sectionCount + 1));
                    sectionCount++;
                }
            }
        }
    }

    public static List<Element> getNodes(List<Element> elements) {
        HashSet<Element> allElements = new HashSet<>();
        for (Element element : elements) {
            collectAllNodes(element, allElements);
        }
        return allElements.stream().sorted(elementComparator).collect(Collectors.toList());
    }

    public static void collectAllNodes(Element element, Set<Element> result) {
        if (result == null) {
            result = new HashSet<>();
        }
        result.add(element);
        if (element.contents != null) {
            for (Element child : element.contents) {
                collectAllNodes(child, result);
            }
        }
    }

    public static void assignSortIndex(List<Element> elements) {
        double globalSortIndex = 1;
        for (Element element : elements) {
            element.sortIndex = BigDecimal.valueOf(++globalSortIndex);
        }
    }

    public static List<Element> jsonData = createNestedStructure("Chapter", "section", "text", "Chapter", 1, 3);
    public static List<Element> allElements = getNodes(jsonData);

    public static void main(String[] args) {
        // 使用适当的参数调用函数以生成 JSON 数据
        globalId = 0;
        globalSortIndex = 0;
        List<Element> jsonData = createNestedStructure("Chapter", "section", "text", "Chapter", 1, 3);
        List<Element> allElements = getNodes(jsonData);

        // 打印结果
        // 注意：以下仅为简单演示如何打印 title 和 id，请按需求完善其他字段的打印方式
        for (Element element : allElements) {
            System.out.println("Title: " + element.title + ", ID: " + element.id + ", Sort Index: " + element.sortIndex);
        }

        System.out.println("===============");
        System.out.println(new Gson().toJson(jsonData));
    }
}
