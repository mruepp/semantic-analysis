package com.pianobakery.complsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.*;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;


/**
 * Created by michael on 16.05.15.
 */
public class Parser {
    //private File[] files;
    private File wDir;
    private String xHtmlString;
    private String fileLanguage;
    private String fileTitle;
    private String fileAuthor;
    private Metadata fileMetadata;
    private String[] pArray;
    private Map fileMetadic;

    public Map getFileMetadic() {
        return fileMetadic;
    }

    public String getFileLanguage() {
        return fileLanguage;
    }

    public String getxHtmlString() {
        return xHtmlString;
    }


    public Metadata getFileMetadata() {
        return fileMetadata;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public String getFileAuthor() {
        return fileAuthor;
    }


    public String[] getpArray() {
        return pArray;
    }

    public File getwDir() {
        return wDir;
    }

    public void setwDir(File wDir) {
        this.wDir = wDir;
        System.out.println("Parser set WDir : " +  this.getwDir());
    }

    /*public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
        System.out.println("Parser files Array ist: " + Arrays.toString(files));
    }
    */





    public Boolean parseDocToXhtml(File infile,File wDir) throws IOException, SAXException, TikaException {
        //this.files = files;
        this.wDir = wDir;


        //ContentHandler handler = new ToXMLContentHandler();
        ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());
        //FileInputStream stream = null;
        TikaInputStream stream = TikaInputStream.get(infile);


        //stream = new FileInputStream(infile);
        //System.out.println("Stream ist: " + stream.toString());

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();

        try {

            parser.parse(stream, handler, metadata);
            //System.out.println("Handler is: " + handler.toString());
            xHtmlString = handler.toString();
            fileMetadata = metadata;
            //System.out.println("Metadata is: " + metadata.toString());

            pArray = this.genParagraphsArray(xHtmlString);
            fileLanguage = this.identifyLanguage(xHtmlString);
            fileTitle = this.identifyTitle(fileMetadata);
            fileAuthor = this.identifyAuthor(fileMetadata);
            fileMetadic = availableMetaDataFieldstoDic(fileMetadata);

            return saveDocToWorkingDirFolder(pArray, infile);
            //return handler.toString();
        }finally {
            stream.close();

        }
    }



    public String identifyAuthor(Metadata metadata) {
        String author = metadata.get("Author");
        //String title = StringUtils.substringBetween(text, "<title>", "</title>");
        System.out.println("Author:" + author); // good
        return author;
    }

    public String identifyTitle(Metadata metadata) {
        String title = metadata.get("title");
        //String title = StringUtils.substringBetween(text, "<title>", "</title>");
        System.out.println("title:" + title); // good
        return title;
    }

    public String[] genParagraphsArray(String text){

        String[] pString = StringUtils.substringsBetween(text, "<p>", "</p>");


        for (String p : pString) {


            //System.out.println("Paragraph value:" + p); // good

        }
        return pString;

    }


    private Map availableMetaDataFieldstoDic(Metadata metadata) {

        Map metadic = new HashMap();

        for(int i = 0; i <metadata.names().length; i++) {
            String name = metadata.names()[i];
            System.out.println(name + " : " + metadata.get(name));

            metadic.put(name, metadata.get(name));

        }
        return metadic;
    }


    public String identifyLanguage(String text) {
        LanguageIdentifier identifier = new LanguageIdentifier(text);
        System.out.println("Language:" + identifier.getLanguage()); // good
        return identifier.getLanguage();
    }

    public Boolean saveDocToWorkingDirFolder(String[] paragraph, File infile) {

        if (paragraph != null && infile != null) {

            File newDir = new File(wDir + File.separator + FilenameUtils.removeExtension(infile.getName()));
            System.out.println("Folder: " + newDir);

            if (!newDir.exists()) {

                System.out.println("Creating directory: " + newDir);
                boolean result = false;

                try {
                    newDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    JOptionPane.showMessageDialog(null, "No permission");
                    return Boolean.FALSE;
                }
                if (result) {
                    System.out.println("DIR created");
                }
            }


            int counter = 1;
            for (String p : paragraph) {


                try {

                    File fileName = new File(newDir + File.separator + Integer.toString(counter) + " " + (StringUtils.substring(p, 0, 30)).replaceAll("[^a-zA-Z0-9.-]", " ") + ".txt");

                    FileUtils.writeStringToFile(fileName, p);

                    //System.out.println("File created");

                    counter++;

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error happened");
                    return Boolean.FALSE;
                }





            }
            File metaFileName = new File(newDir + File.separator + "0 Metadata.txt");
            for (int i = 0; i < fileMetadata.names().length; i++) {
                String name = fileMetadata.names()[i];

                try {

                    System.out.println(name + " : " + fileMetadata.get(name));
                    FileUtils.writeStringToFile(metaFileName, ((name + " : " + fileMetadata.get(name)) + System.getProperty("line.separator")), true);

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error happened");
                    return Boolean.FALSE;
                }


            }
        }
        return Boolean.TRUE;

    }
}




