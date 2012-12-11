package org.nutz.lang;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.lang.Code.CodeAnalysisResult;
import org.nutz.lang.Code.CodeStatisticsResult;

public class CodeTest {

    private static String path;

    static {
        path = Files.findFile("org/nutz/lang/CodeAnalysisDemo1.ca").getParent();
    }

    @Test
    public void analysisFile() throws Exception {
        File file = new File(path + "/CodeAnalysisDemo1.ca");
        CodeAnalysisResult analysisResult = Code.countingCode(file, null);
        Assert.assertEquals(4, analysisResult.getImportLines());
        Assert.assertEquals(5, analysisResult.getCommentLines());
        Assert.assertEquals(9, analysisResult.getWhiteLines());
        Assert.assertEquals(14, analysisResult.getNormalLines());
    }

    @Test
    public void analysisFolder() throws Exception {
        File src = new File(path);
        CodeStatisticsResult statisticsResult = Code.countingCode(src, "ca", false, null);
        Assert.assertEquals(2, statisticsResult.getFileCount());
        Assert.assertEquals(8, statisticsResult.getImportLines());
        Assert.assertEquals(10, statisticsResult.getCommentLines());
        Assert.assertEquals(18, statisticsResult.getWhiteLines());
        Assert.assertEquals(28, statisticsResult.getNormalLines());
    }

    @Test
    public void analysisFolderAndSubFolder() throws Exception {
        File src = new File(path);
        CodeStatisticsResult statisticsResult = Code.countingCode(src, "ca", true, null);
        Assert.assertEquals(4, statisticsResult.getFileCount());
        Assert.assertEquals(16, statisticsResult.getImportLines());
        Assert.assertEquals(20, statisticsResult.getCommentLines());
        Assert.assertEquals(36, statisticsResult.getWhiteLines());
        Assert.assertEquals(56, statisticsResult.getNormalLines());

    }

}
