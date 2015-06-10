package com.pianobakery.complsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.example.ContentHandlerExample;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.*;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;


/**
 * Created by michael on 16.05.15.
 */
public class Parser {







    public String parseDocToPlainText(File infile) throws IOException, SAXException, TikaException {

        BodyContentHandler handler = new BodyContentHandler();
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
            logger.debug
            return handler.toString();
        }finally {
            stream.close();

        }
    }

    public List<String> parseToPlainTextChunks(File infile, int maxchunks) throws IOException, SAXException, TikaException {
        final List<String> chunks = new ArrayList<String>();
        chunks.add("");
        ContentHandlerDecorator handler = new ContentHandlerDecorator() {
            @Override
            public void characters(char[] ch, int start, int length) {
                String lastChunk = chunks.get(chunks.size()-1);
                String thisStr = new String(ch, start, length);

                if (lastChunk.length()+length > maxchunks) {
                    chunks.add(thisStr);
                } else {
                    chunks.set(chunks.size()-1, lastChunk+thisStr);
                }
            }
        };

        TikaInputStream stream = TikaInputStream.get(infile);
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try {
            parser.parse(stream, handler, metadata);
            return chunks;
        } finally {
            stream.close();
        }
    }




    public Metadata getMetaData(File infile) {

        BodyContentHandler handler = new BodyContentHandler();
        TikaInputStream stream = TikaInputStream.get(infile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();


        try {
            parser.parse(stream, handler, metadata);

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
        System.out.println("title:" + title); // good
        return title;
    }

    public String identifyLanguage(String text) {
        LanguageIdentifier identifier = new LanguageIdentifier(text);
        System.out.println("Language:" + identifier.getLanguage()); // good
        return identifier.getLanguage();
    }




    /*public Boolean saveDocToWorkingDirFolder(String[] paragraph, File infile) {

        if (paragraph != null && infile != null) {

            File newDir = new File(wDir + File.separator + "Corporae" + File.separator + FilenameUtils.removeExtension(infile.getName()));
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

    }*/
}




