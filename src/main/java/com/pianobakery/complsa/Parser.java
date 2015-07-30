package com.pianobakery.complsa;

import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
    private File theInfile;
    private String plainText;
    private String xhtmlText;
    private Metadata theMetadata;
    private String[] theMetadataNames;
    private HashMap<String, File> sentModels;

    final static Logger logger = Logger.getLogger(MainGui.class);




    //TODO Add Method to parse Document String into List of Sentences and/or Pages with certain amount of Sentences


    public Parser(File theInfile) {
        this.theInfile = theInfile;
        this.plainText = null;
        this.xhtmlText = null;
        this.theMetadata = null;
        this.sentModels = null;

    }


    public File getTheInfile() {
        return theInfile;
    }

    public String getPlainText() {
        return plainText;
    }

    public String getXhtmlText() {
        return xhtmlText;
    }

    public Metadata getTheMetadata() {
        return theMetadata;
    }

    public void parseDocToPlainText() throws IOException, SAXException, TikaException {

        if (theInfile == null) {
            return;
        }

        BodyContentHandler handler = new BodyContentHandler(writeLimit);
        TikaInputStream stream = TikaInputStream.get(theInfile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();

        try {
            parser.parse(stream, handler, metadata);
            plainText = handler.toString();
            //return handler.toString();
        } finally {
            stream.close();
        }


    }



    public void parseDocToXhtml() throws IOException, SAXException, TikaException {

        if (theInfile == null){
            return;
        }

        ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());
        TikaInputStream stream = TikaInputStream.get(theInfile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();

        try {
            parser.parse(stream, handler, metadata);
            //logger.debug
            xhtmlText = handler.toString();
            //return handler.toString();
        }finally {
            stream.close();
        }


    }



    public void saveMetadataToFolder(File aFolder) {
        theMetadataNames = theMetadata.names();

        BufferedWriter writer = null;
        File fileName = new File(aFolder + File.separator + ".metadata.txt");

        try {
            writer = new BufferedWriter(new FileWriter(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (theMetadataNames != null) {

            for (String aName : theMetadataNames) {

                try {
                    if (aName != null) {
                        writer.write(aName + " " + theMetadata.get(aName));
                        writer.newLine();
                        writer.flush();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }



    public void getMetaData() throws IOException, SAXException, TikaException {

        if (theInfile == null){
            return;
        }

        BodyContentHandler handler = new BodyContentHandler(writeLimit);
        TikaInputStream stream = TikaInputStream.get(theInfile);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();



        try {
            parser.parse(stream, handler, metadata);

            String[] metadataNames = metadata.names();
            for(String name : metadataNames) {
                logger.debug(name + ": " + metadata.get(name));
            }
            theMetadata = metadata;


        }finally{
            stream.close();
        }

    }

    public String identifyAuthor() {

        if (theMetadata == null) {
            return null;
        }
        String author = theMetadata.get("Author");
        logger.debug("Author:" + author); // good
        return author;
    }

    public String identifyTitle() {

        if (theMetadata == null) {
            return null;
        }
        String title = theMetadata.get("title");
        logger.debug("Title:" + title); // good
        return title;
    }

    public String identifyLanguage() {
        if (plainText == null) {
            return null;
        }
        LanguageIdentifier identifier = new LanguageIdentifier(plainText);
        logger.debug("Language:" + identifier.getLanguage()); // good
        return identifier.getLanguage();
    }




    public Boolean saveDocToWorkingDirFolder(File corpDir,boolean chunk, int sentences, HashMap<String, File> theSentModels) throws FileNotFoundException {
        this.sentModels = theSentModels;

        if (theInfile == null) {
            return false;
        }

        if (corpDir != null && theInfile != null) {
            String newTitle;
            File newDocDir;
            String lang;
            //Random rand = new Random();

            try {
                parseDocToPlainText();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (TikaException e) {
                e.printStackTrace();
            }


            try {
                getMetaData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (TikaException e) {
                e.printStackTrace();
            }

            lang = identifyLanguage();
            File docDir;
            //String title  = new String(FilenameUtils.removeExtension(theInfile.getName()) + "-" + Integer.toString(rand.nextInt(100000)) + "-" + lang);
            String parentDir = theInfile.getAbsoluteFile().getParentFile().getName();
            logger.debug("Parent: " + parentDir.toString());
            logger.debug("CorpDir: " + corpDir.toString());

            String title = new String(FilenameUtils.removeExtension(theInfile.getName()) + "-" + lang);
            logger.debug("Title: " + title);

            if (parentDir.equals(corpDir.getName())) {
                docDir = new File(corpDir + File.separator + title);
                logger.debug("Docdir same: " + docDir);
            } else {
                docDir = new File(corpDir + File.separator + parentDir + File.separator + title);
            }


            logger.debug("Document Directory: " + docDir);
            newDocDir = docDir;

            if (!newDocDir.exists()) {

                logger.debug("Creating directory: " + newDocDir);
                boolean result = false;

                try {
                    FileUtils.forceMkdir(newDocDir);

                    result = true;
                } catch (SecurityException | IOException e) {
                    JOptionPane.showMessageDialog(null, "No permission");
                    //return Boolean.FALSE;
                }
                if (result) {
                    logger.debug("DIR created");
                }

            }

            if (theMetadata != null) {
                saveMetadataToFolder(newDocDir);

            }


            //TODO Get the infile and split it to X amount of txt files

            if (!chunk) {


                BufferedWriter writer = null;
                File fileName = new File(newDocDir + File.separator + title + ".txt");
                //If not chunking, save whole document to subfolder

                if (newDocDir.exists() && !fileName.exists()) {

                        try {
                            writer = new BufferedWriter(new FileWriter(fileName));
                            writer.write(plainText);
                            logger.debug("Creating File now");
                        } catch (SecurityException | IOException e) {
                            JOptionPane.showMessageDialog(null, "No permission");
                            return false;
                            //return Boolean.FALSE;
                        } finally {
                            try {
                                if (writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "No permission");
                                return false;
                            }

                        }

                } else {
                    logger.debug("File Exists or DocDir doesnt");
                }


            } else {





                if (newDocDir.exists()) {

                    logger.debug("Amount of Sentences: " + sentences);

                    logger.debug("Parser is chunker: " + chunk);
                    //String[] sent = getStanfordSentenceArray();
                    String[] sent = getSentenceArray();

                    if (sent == null) {
                        logger.debug("No Language Model");
                        return false;
                    }

                    int arrayLength = sent.length;


                    for (int i = 0; i < arrayLength; i+=sentences) {


                        int length = i+sentences;
                        logger.debug("From " + i + " to Length: " + length);
                        logger.debug("#####################################");
                        String[] subset = Arrays.copyOfRange(sent, i, length);
                        BufferedWriter writer = null;
                        File fileName = new File(newDocDir + File.separator + i + "-" + length + "-" + title + ".txt");
                        try {
                            writer = new BufferedWriter(new FileWriter(fileName));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        for (String aSentence : subset) {
                            logger.debug("The Sentence: " + aSentence);
                            try {
                                if (aSentence != null) {
                                    writer.write(aSentence);
                                    writer.newLine();
                                    writer.flush();

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                        if (writer != null) {
                            try {
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        logger.debug("#####################################");
                        //Do something
                    }
                    return true;
                }



            }

        }
        return true;
    }


    public String[] getStanfordSentenceArray() throws FileNotFoundException {
        if (theInfile == null) {
            return null;
        }

//TODO Implement the Stanford Parser to Split Sentences
        //http://nlp.stanford.edu/software/corenlp.shtml
        //http://stackoverflow.com/questions/9492707/how-can-i-split-a-text-into-sentences-using-the-stanford-parser


        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit");
        props.put("ssplit.newlineIsSentenceBreak", "two");


        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);



        // read some text in the text variable
        //String text = ... // Add your text here!

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(plainText);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> sentString = new ArrayList<String>();


        for(CoreMap sentence: sentences) {
            logger.debug("##################################");
            logger.debug("Stanford Sentence: " + sentence.toString());
            String line = sentence.toString().replaceAll("\\r\\n|\\r|\\n", "");
            String lineb = line.replaceAll("\\s+", " ").trim();
            sentString.add(lineb);

            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            /*for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                logger.debug("Stanford word: " + word);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                logger.debug("Stanford pos: " + pos);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                logger.debug("Stanford NER: " + ne);
            }*/

        }

        return sentString.toArray(new String[sentString.size()]);



    }



    public String[]  getSentenceArray() throws FileNotFoundException {
        if (theInfile == null) {
            return null;
        }

        String lang = identifyLanguage();
        File langModel = sentModels.get(lang);

        if (langModel == null) {

            return null;
        }

        logger.debug("The Sent Model: " + langModel.toString());
        InputStream modelIn = new FileInputStream(langModel);
        List<String> sentList = new ArrayList<String>();

        try {
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sdetector = new SentenceDetectorME(model);
            String[] sentences = sdetector.sentDetect(plainText);

            for (String sent : sentences) {
                String line = sent.toString().replaceAll("\\r\\n|\\r|\\n", " ");
                String lineb = line.replaceAll("\\s+", " ").trim();
                String linec = lineb.replaceAll("- ", "");
                sentList.add(linec);

            }

            return sentList.toArray(new String[sentList.size()]);

        } catch (IOException e) {
            e.printStackTrace();


        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                }
            }



        }

        return null;
    }




}




