package org.nutz.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 一个统计代码的工具
 * 
 * @author pangwu86(pangwu86@gmail.com)
 */
public class Code {

    private Code() {}

    /**
     * 代码分析结果。
     * 
     * @author pangwu86(pangwu86@gmail.com)
     */
    public static class CodeAnalysisResult {
        // 代码行
        protected long normalLines;
        // 注释行
        protected long commentLines;
        // 空行
        protected long whiteLines;
        // 导入行
        protected long importLines;

        public CodeAnalysisResult() {}

        public CodeAnalysisResult(    long normalLines,
                                    long commentLines,
                                    long whiteLines,
                                    long importLines) {
            this.normalLines = normalLines;
            this.commentLines = commentLines;
            this.whiteLines = whiteLines;
            this.importLines = importLines;
        }

        public long getNormalLines() {
            return normalLines;
        }

        public long getCommentLines() {
            return commentLines;
        }

        public long getWhiteLines() {
            return whiteLines;
        }

        public long getImportLines() {
            return importLines;
        }

        public long getTotalLines() {
            return normalLines + commentLines + whiteLines + importLines;
        }

    }

    /**
     * 代码统计结果。
     * 
     * @author pangwu86(pangwu86@gmail.com)
     */
    public static class CodeStatisticsResult extends CodeAnalysisResult {

        private File src;

        private int fileCount;

        public CodeStatisticsResult(File src) {
            this.src = src;
        }

        public void addCodeAnalysisResult(CodeAnalysisResult analysisResult) {
            this.normalLines += analysisResult.getNormalLines();
            this.commentLines += analysisResult.getCommentLines();
            this.whiteLines += analysisResult.getWhiteLines();
            this.importLines += analysisResult.getImportLines();
            fileCount++;
        }

        public File getSrc() {
            return src;
        }

        public int getFileCount() {
            return fileCount;
        }
    }

    /**
     * 代码分析配置信息。
     * 
     * @author pangwu86(pangwu86@gmail.com)
     */
    public static class CodeAnalysisConf {

        /** 包名行开头 */
        public String pakStart;

        /** 导入行开头 */
        public String impStart;

        /** 单行注解开头 */
        public String singleLineCommentStart;

        /** 多行注解开头 */
        public String multiLineCommentStart;

        /** 多行注解结尾 */
        public String multiLineCommentEnd;

        /** 空行 */
        public String emptyLinePattern;

    }

    /** 分析JAVA代码的配置项 */
    private static CodeAnalysisConf CODE_INFO_JAVA = new CodeAnalysisConf();

    static {
        CODE_INFO_JAVA.pakStart = "package ";
        CODE_INFO_JAVA.impStart = "import ";
        CODE_INFO_JAVA.singleLineCommentStart = "//";
        CODE_INFO_JAVA.multiLineCommentStart = "/*";
        CODE_INFO_JAVA.multiLineCommentEnd = "*/";
        CODE_INFO_JAVA.emptyLinePattern = "^[\\s&&[^\\n]]*$";
    }

    /**
     * 统计某个文件的信息。
     * 
     * @param file
     *            被分析的文件
     * @param conf
     *            代码分析配置项(为空的话，则按照JAVA代码来进行分析统计)
     * @return 分析结果
     */
    public static CodeAnalysisResult countingCode(File file, CodeAnalysisConf conf) {
        if (!Files.isFile(file)) {
            throw new RuntimeException("file is not a File, can't analysis it.");
        }
        if (null == conf) {
            conf = CODE_INFO_JAVA;
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
        boolean comment = false;
        long whiteLines = 0;
        long commentLines = 0;
        long normalLines = 0;
        long importLines = 0;
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(conf.multiLineCommentStart)
                    && !line.endsWith(conf.multiLineCommentEnd)) {
                    // 多行注释开始
                    commentLines++;
                    comment = true;
                } else if (true == comment) {
                    // 多行注释结束
                    commentLines++;
                    if (line.endsWith(conf.multiLineCommentEnd)) {
                        comment = false;
                    }
                } else if (line.matches(conf.emptyLinePattern)) {
                    // 空白行(多行注解内的空白行不算在内)
                    whiteLines++;
                } else if (line.startsWith(conf.singleLineCommentStart)
                            || (line.startsWith(conf.multiLineCommentStart) && line.endsWith(conf.multiLineCommentEnd))) {
                    // 单行注释
                    commentLines++;
                } else if (line.startsWith(conf.pakStart) || line.startsWith(conf.impStart)) {
                    // package与import
                    importLines++;
                } else {
                    // 代码行
                    normalLines++;
                }
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        if (br != null) {
            try {
                br.close();
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        }
        // 记录并返回统计结果
        return new CodeAnalysisResult(normalLines, commentLines, whiteLines, importLines);
    }

    /**
     * 统计某个目录下，以特定后缀名结尾的源码信息。
     * 
     * @param src
     *            源代码目录
     * @param suffix
     *            文件后缀（为空的话，则统计所有类型文件）
     * @param countSubFolder
     *            是否统计子文件夹(true的话，将递归统计所有子文件夹)
     * @param conf
     *            代码分析配置项(为空的话，则按照JAVA代码来进行分析统计)
     */
    public static CodeStatisticsResult countingCode(File src,
                                                    String suffix,
                                                    boolean countSubFolder,
                                                    CodeAnalysisConf conf) {
        if (!Files.isDirectory(src)) {
            throw new RuntimeException("src is not a File, can't analysis it.");
        }
        if (null == conf) {
            conf = CODE_INFO_JAVA;
        }
        CodeStatisticsResult statisticsResult = new CodeStatisticsResult(src);
        boolean useParticularType = !Strings.isBlank(suffix);
        folderAnalysis(src, useParticularType, suffix, countSubFolder, conf, statisticsResult);
        return statisticsResult;
    }

    private static void folderAnalysis(    File src,
                                        boolean useParticularType,
                                        String suffix,
                                        boolean countSubFolder,
                                        CodeAnalysisConf conf,
                                        CodeStatisticsResult statisticsResult) {
        for (File f : src.listFiles()) {
            if (countSubFolder && Files.isDirectory(f)) {
                folderAnalysis(f, useParticularType, suffix, countSubFolder, conf, statisticsResult);
            } else {
                if (useParticularType
                    && !suffix.equalsIgnoreCase(f.getName()
                                                    .substring(f.getName().lastIndexOf('.') + 1))) {
                    continue;
                }
                statisticsResult.addCodeAnalysisResult(countingCode(f, conf));
            }
        }
    }

}
