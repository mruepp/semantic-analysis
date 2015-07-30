package com.pianobakery.complsa;

import java.io.File;

/**
 * Created by michael on 27.07.15.
 */
public class DocSearchFile {
    String similarity;
    File file;
    File corpDir;

    public DocSearchFile(String similarity, File file, File corpDir) {
        this.similarity = similarity;
        this.file = file;
        this.corpDir = corpDir;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {

        String absPath = file.getAbsolutePath();

        String relative = new File(corpDir.toString()).toURI().relativize(new File(absPath).toURI()).getPath();
        return relative;
    }


    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }



}