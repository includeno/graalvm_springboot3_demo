package com.example.utils;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PandocUtils {
    public static String getRandomFileName() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static final String formulaTemplate = "\\documentclass{article}\n\\usepackage{amsmath}\n" +
            "\\begin{document}\n" +
            "\\begin{equation}"+
            "%s \\tag{1-1} \n" +
            "\\end{equation}"+
            "\\end{document}";

    public static List<Object> latexToWord(String formula,Boolean randomFileName) {
        String inputString = String.format(formulaTemplate, formula);

        File wordDocxFile = stringToWordFile(inputString, randomFileName);
        if (wordDocxFile == null) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        // First, create/save a docx containing a formula.
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(wordDocxFile);
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            result = mdp.getContent();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        } finally {
//            try {
//                Files.delete(wordDocxFile.toPath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            return result;
        }
    }

    public static List<Object> readFromWordFile(File wordDocxFile) {
        List<Object> result = new ArrayList<>();
        // First, create/save a docx containing a formula.
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(wordDocxFile);
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            result = mdp.getContent();
            return result;
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        } finally {
            return result;
        }
    }

    /**
     * 将LaTeX字符串转换为Word文档
     *
     * @param latexString LaTeX字符串
     * @param filePath Word文档路径
     * @return Word文档
     */
    public static File stringToWordFile(String latexString, String filePath){
        File tempFile = null;
        try {
            // 创建临时文件并将LaTeX字符串写入临时文件
            tempFile = File.createTempFile(getRandomFileName() + "_latex", ".tex");
            try (FileWriter fileWriter = new FileWriter(tempFile)) {
                fileWriter.write(latexString);
            }
            // 要生成的Word文档路径
            String outputFilePath = filePath;
            // 构造Pandoc命令
            String pandocCommand = "pandoc " + tempFile.getAbsolutePath() + " -s -o " + outputFilePath;
            // 使用Runtime.getRuntime().exec()方法执行Pandoc命令
            Process process = Runtime.getRuntime().exec(pandocCommand);
            // 等待Pandoc命令执行完成
            process.waitFor();
            // 删除临时文件
            Files.delete(tempFile.toPath());
            return new File(outputFilePath);
        } catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public static File stringToWordFile(String latexString, Boolean randomFileName,String baseFileName){
        if(randomFileName){
            return stringToWordFile(latexString, getRandomFileName() + "_" + baseFileName);
        }else{
            return stringToWordFile(latexString, baseFileName);
        }
    }


    public static File stringToWordFile(String latexString, Boolean randomFileName) {
        return stringToWordFile(latexString,randomFileName,"output2.docx");
    }


    public static void main(String[] args) throws IOException {
        File tempFile = null;
        try {
            // LaTeX字符串内容
            String latexString = "\\documentclass{article}\n" +
                    "\\begin{document}\n" +
                    "Hello, this is a LaTeX document.\n" +
                    "\\end{document}";

            // 创建临时文件并将LaTeX字符串写入临时文件
            tempFile = File.createTempFile("latex", ".tex");
            try (FileWriter fileWriter = new FileWriter(tempFile)) {
                fileWriter.write(latexString);
            }

            // 要生成的Word文档路径
            String outputFilePath = "output.docx";
            // 构造Pandoc命令
            String pandocCommand = "pandoc " + tempFile.getAbsolutePath() + " -s -o " + outputFilePath;
            // 使用Runtime.getRuntime().exec()方法执行Pandoc命令
            Process process = Runtime.getRuntime().exec(pandocCommand);
            // 等待Pandoc命令执行完毕
            process.waitFor();
            // 检查命令执行结果
            if (process.exitValue() == 0) {
                System.out.println("转换成功！");
            } else {
                System.out.println("转换失败！");
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件
            Files.delete(tempFile.toPath());
        }
    }
}
