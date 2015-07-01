package com.pianobakery.complsa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.*;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.swing.*;


/**
 * Created by michael on 16.05.15.
 */
public class Parser {
    private final int writeLimit = -1;


    //TODO Add Method to parse Document String into List of Sentences and/or Pages with certain amount of Sentences


    public String parseDocToPlainText(File infile) throws IOException, SAXException, TikaException {

        BodyContentHandler handler = new BodyContentHandler(writeLimit);
        TikaInputStream stream = TikaInputStream.get(infile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();

        try {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        } finally {
            stream.close();
        }


    }

    public String parseDocToXhtml(File infile) throws IOException, SAXException, TikaException {

        ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());

        TikaInputStream stream = TikaInputStream.get(infile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();

        try {
            parser.parse(stream, handler, metadata);
            //logger.debug
            return handler.toString();
        }finally {
            stream.close();

        }
    }






    public Metadata getMetaData(File infile) throws IOException, SAXException, TikaException {

        BodyContentHandler handler = new BodyContentHandler(writeLimit);
        TikaInputStream stream = TikaInputStream.get(infile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();


        try {
            parser.parse(stream, handler, metadata);

            String[] metadataNames = metadata.names();
            for(String name : metadataNames) {
                System.out.println(name + ": " + metadata.get(name));
            }

            return metadata;
            }finally{
                stream.close();
        }
    }

    public String identifyAuthor(Metadata metadata) {
        String author = metadata.get("Author");
        System.out.println("Author:" + author); // good
        return author;
    }

    public String identifyTitle(Metadata metadata) {


        String title = metadata.get("title");
        System.out.println("Title:" + title); // good
        return title;
    }





    public String identifyLanguage(String text) {
        LanguageIdentifier identifier = new LanguageIdentifier(text);
        System.out.println("Language:" + identifier.getLanguage()); // good
        return identifier.getLanguage();
    }




    public Boolean saveDocToWorkingDirFolder(File corpDir, File infile) {

        if (corpDir != null && infile != null) {
            String newTitle;
            File newDocDir = null;

                Random rand = new Random();
                //Metadata metadata = this.getMetaData(infile);
                String title  = new String(FilenameUtils.removeExtension(infile.getName()) + "-" + Integer.toString(rand.nextInt(100000)));
                newTitle = title;
                System.out.println("Title: " + newTitle);

                File docDir = new File(corpDir + File.separator + newTitle);
                System.out.println("Document Directory: " + docDir);
                newDocDir = docDir;

            if (!newDocDir.exists()) {

                System.out.println("Creating directory: " + newDocDir);
                boolean result = false;

                try {
                    newDocDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    JOptionPane.showMessageDialog(null, "No permission");
                    //return Boolean.FALSE;
                }
                if (result) {
                    System.out.println("DIR created");
                }

            } else {
                System.out.println("Folder exists, second try new random");
                String secondtitle  = new String(FilenameUtils.removeExtension(infile.getName()) + "-" + Integer.toString(rand.nextInt(100000)));
                newTitle = secondtitle;
                File seconddocDir = new File(corpDir + File.separator + newTitle);
                System.out.println("Creating directory: " + seconddocDir);
                boolean result = false;

                try {
                    seconddocDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    JOptionPane.showMessageDialog(null, "No permission");
                    //return Boolean.FALSE;
                }
                if (result) {
                    System.out.println("DIR created 2nd try");
                }

            }

            /*

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


            }*/
        }
        return true;

    }
}




