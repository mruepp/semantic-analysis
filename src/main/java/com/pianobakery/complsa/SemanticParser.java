package com.pianobakery.complsa;

import ch.akuhn.edu.mit.tedlab.DMat;
import ch.akuhn.edu.mit.tedlab.SMat;
import ch.akuhn.edu.mit.tedlab.SVDRec;
import ch.akuhn.edu.mit.tedlab.Svdlib;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.opengis.metadata.identification.Progress;
import pitt.search.lucene.FilePositionDoc;
import pitt.search.lucene.PorterAnalyzer;
import pitt.search.semanticvectors.*;
import pitt.search.semanticvectors.utils.VerbatimLogger;
import pitt.search.semanticvectors.vectors.RealVector;
import pitt.search.semanticvectors.vectors.Vector;
import sun.reflect.misc.FieldUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.apache.log4j.Logger.*;

/**
 * Created by michael on 10.07.15.
 */
public class SemanticParser {

    private File theCorpDir = null;
    private File lucIndexDir = null;
    private File wDir = null;

    private int posIndexRadius;
    private String termweight;
    private int amTraining;
    private static ProgressBar theBar;

    public void setAmTraining(int amTraining) {
        this.amTraining = amTraining;
    }

    private static String lucIndexParentDirName = "luceneIndex";
    private static String luceneIndexFilesFolder = "luceneIndexFiles";

    public static String getLucIndexParentDirName() {
        return lucIndexParentDirName;
    }

    public static String getLuceneIndexFilesFolder() {
        return luceneIndexFilesFolder;
    }

    final static Logger logger = getLogger(MainGui.class);


    public SemanticParser(File wDir, File aCorpDir, int aPosIndexRadius, ProgressBar aBar) {
        this.amTraining = 2;
        this.theBar = aBar;
        this.theCorpDir = aCorpDir;
        File indexDir = new File(wDir + File.separator + lucIndexParentDirName + File.separator + aCorpDir.getName());
        this.lucIndexDir = indexDir;
        this.wDir = wDir;

        this.posIndexRadius = aPosIndexRadius;

        if (!indexDir.exists()) {
            boolean result = false;

            try {
                FileUtils.forceMkdir(indexDir);
                result = true;
            } catch (SecurityException se) {
                JOptionPane.showMessageDialog(null, "No permission or File Exists");

                //return Boolean.FALSE;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (result) {
                logger.debug("Lucene Index Dir created");

            }

        }



    }


    public boolean createLuceneIndexCorp() {
        boolean update;
        Directory dir;

        if (theCorpDir == null && lucIndexDir == null) {
            return false;
        }

        if (lucIndexDir.exists()) {
            try {
                dir = FSDirectory.open(lucIndexDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            try {
                update = DirectoryReader.indexExists(dir);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            logger.warn("Can not create Index Lucene Dir not existing");
            return false;
        }

        try {

            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (!update) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
                logger.info("Lucene Index does not exist, creating");
            } else {
                // Add new documents to an existing index:
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                logger.info("Lucene Index exists, updating");
            }

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            iwc.setRAMBufferSizeMB(1024.0);

            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocs(writer, theCorpDir.toPath());

            // NOTE: if you want to maximize search performance,
            // you can optionally call forceMerge here.  This can be
            // a terribly costly operation, so generally it's only
            // worth it when your index is relatively static (ie
            // you're done adding documents to it):
            //
            writer.forceMerge(1);

            writer.close();
            logger.debug("Writer closed");
            return true;

        } catch (IOException e) {

            logger.debug(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
        }
        return false;
    }

    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {



                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    if (!file.toFile().isHidden()) {
                        try {
                            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());

                        } catch (IOException ignore) {

                            // don't index files that can't be read.
                        }
                        if (theBar.getButtonCancel()) return FileVisitResult.TERMINATE;
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }

    }

    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {



        try (InputStream stream = Files.newInputStream(file)) {

        // make a new, empty document

            Document doc = new Document();


            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            // Add the last modified date of the file a field named "modified".
            // Use a LongField that is indexed (i.e. efficiently filterable with
            // NumericRangeFilter).  This indexes to milli-second resolution, which
            // is often too fine.  You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you require.
            // For example the long value 2011021714 would mean
            // February 17, 2011, 2-3 PM.
            doc.add(new LongField("modified", lastModified, Field.Store.NO));


            //create new FieldType to store term positions (TextField is not sufficiently configurable)
            FieldType ft = new FieldType();
            ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            ft.setTokenized(true);
            ft.setStoreTermVectors(true);
            ft.setStoreTermVectorPositions(true);
            Field contentsField = new Field("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), ft);
            doc.add(contentsField);

            logger.debug("Lucene Fieldtype: " + ft.toString());
            logger.debug("Lucene ContentFields: " + contentsField.toString());
            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will fail.
            //doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {

                // New index, so we just add the document (no old document can be there):
                logger.debug("adding " + file);
                writer.addDocument(doc);

                if (theBar.getButtonCancel()) return;
            } else {

            // Existing index (an old copy of this document may have been indexed) so
            // we use updateDocument instead to replace the old one matching the exact
            // path, if present:
            logger.debug("updating " + file);
            writer.updateDocument(new Term("path", file.toString()), doc);
                if (theBar.getButtonCancel()) return;
            }
        }
    }


    public boolean buildSemanticIndex(int type, String termweight) {
        this.termweight = termweight;
        if (theBar.getButtonCancel()) return false;

        switch (type) {

            case 0:

                buildStandIndex();
                return true;

            case 1:
                buildLSAIndex();
                return true;

            case 2:
                buildPosIndex();
                return true;

            default:
                logger.debug("Nothing selected");
                return false;


        }

    }

    public void buildStandIndex() {
        logger.debug("Calling buildIndex");

        logger.debug("Termweight is: " + termweight);
        //File termtermvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "StandardTermTermIndex");
        File termvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "Standard-term-" + termweight);
        File docvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "Standard-doc-" + termweight);


        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("-luceneindexpath");
        arguments.add(lucIndexDir.toString());
        //arguments.add("-vectortype");
        //arguments.add("-dimension");
        //arguments.add("-seedlength");
        //arguments.add("-minfrequency");
        //arguments.add("-maxnonalphabetchars");
        //arguments.add("-docindexing");
        arguments.add("-termweight");
        arguments.add(termweight);
        arguments.add("-docindexing");
        arguments.add("incremental");
        arguments.add("-trainingcycles");
        arguments.add(Integer.toString(amTraining));
        arguments.add("-termvectorsfile");
        arguments.add(termvectorfile.toString());
        //arguments.add("-termtermvectorsfile");
        //arguments.add(termtermvectorfile.toString());
        arguments.add("-docvectorsfile");
        arguments.add(docvectorfile.toString());


        String[] args = new String[arguments.size()];
        args = arguments.toArray(args);



        FlagConfig flagConfig;
        try {
            flagConfig = FlagConfig.getFlagConfig(args);
        } catch (IllegalArgumentException e) {
            //System.err.println(usageMessage);
            throw e;
        }


        logger.info("Seedlength: " + flagConfig.seedlength() + "\n"
                + ", Dimension: " + flagConfig.dimension() + "\n"
                + ", Vector type: " + flagConfig.vectortype() + "\n"
                + ", Minimum frequency: " + flagConfig.minfrequency() + "\n"
                + ", Maximum frequency: " + flagConfig.maxfrequency() + "\n"
                + ", Number non-alphabet characters: " + flagConfig.maxnonalphabetchars() + "\n"
                + ", Windowradius: " + flagConfig.windowradius() + "\n"
                + ", Termvectorfile: " + flagConfig.termvectorsfile() + "\n"
                + ", Docindexing: " + flagConfig.docindexing() + "\n"
                + ", Postermvectorfile: " + flagConfig.termtermvectorsfile() + "\n"
                + ", Docvectorsfile: " + flagConfig.docvectorsfile() + "\n"
                + ", Termweigh: " + flagConfig.termweight() + "\n"
                + ", Contents fields are: " + Arrays.toString(flagConfig.contentsfields()) + "\n");


        try {
            BuildIndex.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File learnedTermFile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "Standard-term-" + termweight + amTraining + ".bin");
        File fullTermvectorfile = new File(termvectorfile + ".bin");
        logger.debug("The termvectorfile: " + termvectorfile.toString());
        logger.debug("The learnedTermFile: " + learnedTermFile.toString());


        //This will break if the Buildindex will take input of suffix (.bin) without adding .bin - e.g. behave like lsa or positional index function
        if (learnedTermFile.exists()) {
            try {
                FileUtils.forceDelete(fullTermvectorfile);
                FileUtils.moveFile(learnedTermFile, fullTermvectorfile);
                logger.debug("Remove and rename of " + learnedTermFile + " to " + fullTermvectorfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


/*
        String termFile = flagConfig.termvectorsfile();
        String docFile = flagConfig.docvectorsfile();
        LuceneUtils luceneUtils = null;

        try {
            luceneUtils = new LuceneUtils(flagConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TermVectorsFromLucene termVectorIndexer;
            if (!flagConfig.initialtermvectors().isEmpty()) {
                // If Flags.initialtermvectors="random" create elemental (random index)
                // term vectors. Recommended to iterate at least once (i.e. -trainingcycles = 2) to
                // obtain semantic term vectors.
                // Otherwise attempt to load pre-existing semantic term vectors.
                logger.info("Creating elemental term vectors ... ");
                termVectorIndexer = TermVectorsFromLucene.createTermBasedRRIVectors(flagConfig);
            } else {
                VectorStore initialDocVectors = null;
                if (!flagConfig.initialdocumentvectors().isEmpty()) {
                    logger.info(String.format(
                            "Loading initial document vectors from file: '%s'.\n", flagConfig.initialdocumentvectors()));
                    initialDocVectors = VectorStoreRAM.readFromFile(flagConfig, flagConfig.initialdocumentvectors());
                    logger.info(String.format(
                            "Loaded %d document vectors to use as elemental vectors.\n", initialDocVectors.getNumVectors()));
                }
                logger.info("Creating term vectors as superpositions of elemental document vectors ... \n");
                termVectorIndexer = TermVectorsFromLucene.createTermVectorsFromLucene(flagConfig, initialDocVectors);
            }

            // Create doc vectors and write vectors to disk.
            switch (flagConfig.docindexing()) {
                case INCREMENTAL:
                    VectorStoreWriter.writeVectors(termFile, flagConfig, termVectorIndexer.getSemanticTermVectors());
                    IncrementalDocVectors.createIncrementalDocVectors(
                            termVectorIndexer.getSemanticTermVectors(), flagConfig, luceneUtils);
                    IncrementalTermVectors itermVectors;

                    for (int i = 1; i < flagConfig.trainingcycles(); ++i) {
                        itermVectors = new IncrementalTermVectors(flagConfig, luceneUtils);

                        VectorStoreWriter.writeVectors(
                                VectorStoreUtils.getStoreFileName(
                                        flagConfig.termvectorsfile() + flagConfig.trainingcycles(), flagConfig),
                                flagConfig, itermVectors);

                        IncrementalDocVectors.createIncrementalDocVectors(itermVectors, flagConfig, luceneUtils);
                        if (theBar.getButtonCancel()) break;
                    }
                    break;
                case INMEMORY:
                    DocVectors docVectors = new DocVectors(termVectorIndexer.getSemanticTermVectors(), flagConfig, luceneUtils);
                    for (int i = 1; i < flagConfig.trainingcycles(); ++i) {
                        logger.info("\nRetraining with learned document vectors ...");
                        termVectorIndexer = TermVectorsFromLucene.createTermVectorsFromLucene(flagConfig, docVectors);
                        docVectors = new DocVectors(termVectorIndexer.getSemanticTermVectors(), flagConfig, luceneUtils);
                        if (theBar.getButtonCancel()) break;
                    }
                    // At end of training, convert document vectors from ID keys to pathname keys.
                    VectorStore writeableDocVectors = docVectors.makeWriteableVectorStore();

                    logger.info("Writing term vectors to " + termFile + "\n");
                    VectorStoreWriter.writeVectors(termFile, flagConfig, termVectorIndexer.getSemanticTermVectors());
                    logger.info("Writing doc vectors to " + docFile + "\n");
                    VectorStoreWriter.writeVectors(docFile, flagConfig, writeableDocVectors);
                    break;
                case NONE:
                    // Write term vectors to disk even if there are no docvectors to output.
                    logger.info("Writing term vectors to " + termFile + "\n");
                    VectorStoreWriter.writeVectors(termFile, flagConfig, termVectorIndexer.getSemanticTermVectors());
                    break;
                default:
                    throw new IllegalStateException(
                            "No procedure defined for -docindexing " + flagConfig.docindexing());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        */

    }

    public void buildLSAIndex(){
        logger.debug("Calling buildLSAIndex");
        logger.debug("Termweight is: " + termweight);

        //File termtermvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "LSATermTermIndex-" + termweight + ".bin");
        File termvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "LSA-term-" + termweight + ".bin");
        File docvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "LSA-doc-" + termweight + ".bin");

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("-luceneindexpath");
        arguments.add(lucIndexDir.toString());
        //arguments.add("-vectortype");
        //arguments.add("-dimension");
        //arguments.add("-seedlength");
        //arguments.add("-minfrequency");
        //arguments.add("-maxnonalphabetchars");
        arguments.add("-termweight");
        arguments.add(termweight);
        arguments.add("-docindexing");
        arguments.add("incremental");
        arguments.add("-trainingcycles");
        arguments.add(Integer.toString(amTraining));
        //arguments.add("-termtermvectorsfile");
        //arguments.add(termtermvectorfile.toString());
        arguments.add("-termvectorsfile");
        arguments.add(termvectorfile.toString());
        arguments.add("-docvectorsfile");
        arguments.add(docvectorfile.toString());

        if (!termvectorfile.exists() || !docvectorfile.exists()) {
            try {
                FileUtils.touch(termvectorfile);
                FileUtils.touch(docvectorfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        String[] args = new String[arguments.size()];
        args = arguments.toArray(args);

        FlagConfig flagConfig;
        try {
            flagConfig = FlagConfig.getFlagConfig(args);
        } catch (IllegalArgumentException e) {
            //System.err.println(usageMessage);
            throw e;
        }


        logger.info("Seedlength: " + flagConfig.seedlength() + "\n"
                + ", Dimension: " + flagConfig.dimension() + "\n"
                + ", Vector type: " + flagConfig.vectortype() + "\n"
                + ", Minimum frequency: " + flagConfig.minfrequency() + "\n"
                + ", Maximum frequency: " + flagConfig.maxfrequency() + "\n"
                + ", Number non-alphabet characters: " + flagConfig.maxnonalphabetchars() + "\n"
                + ", Windowradius: " + flagConfig.windowradius() + "\n"
                + ", Termvectorfile: " + flagConfig.termvectorsfile() + "\n"
                + ", Docindexing: " + flagConfig.docindexing() + "\n"
                + ", Postermvectorfile: " + flagConfig.termtermvectorsfile() + "\n"
                + ", Docvectorsfile: " + flagConfig.docvectorsfile() + "\n"
                + ", Termweigh: " + flagConfig.termweight() + "\n"
                + ", Contents fields are: " + Arrays.toString(flagConfig.contentsfields()) + "\n");

        //String termFile = flagConfig.termvectorsfile();
        //String docFile = flagConfig.docvectorsfile();
        //LuceneUtils luceneUtils = new LuceneUtils(flagConfig);


        try {
            LSA.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*
        LSA lsaIndexer = new LSA(flagConfig.luceneindexpath(), flagConfig);
        SMat A = lsaIndexer.smatFromIndex();
        Svdlib svd = new Svdlib();

        logger.info("Starting SVD using algorithm LAS2 ...\n");

        SVDRec svdR = svd.svdLAS2A(A, flagConfig.dimension());
        DMat vT = svdR.Vt;
        DMat uT = svdR.Ut;
        lsaIndexer.writeOutput(vT, uT);


        // Open file and write headers.
        FSDirectory fsDirectory = FSDirectory.open(FileSystems.getDefault().getPath("."));
        IndexOutput outputStream = fsDirectory.createOutput(
                VectorStoreUtils.getStoreFileName(flagConfig.termvectorsfile(), flagConfig),
                IOContext.DEFAULT);

        // Write header giving number of dimensions for all vectors and make sure type is real.
        outputStream.writeString(VectorStoreWriter.generateHeaderString(flagConfig));
        int cnt;
        // Write out term vectors
        for (cnt = 0; cnt < vT.cols; cnt++) {
            outputStream.writeString(this.termList[cnt]);
            Vector termVector;

            float[] tmp = new float[flagConfig.dimension()];
            for (int i = 0; i < flagConfig.dimension(); i++)
                tmp[i] = (float) vT.value[i][cnt];
            termVector = new RealVector(tmp);
            termVector.normalize();

            termVector.writeToLuceneStream(outputStream);
        }
        outputStream.close();
        VerbatimLogger.info(
                "Wrote " + cnt + " term vectors incrementally to file " + flagConfig.termvectorsfile() + ".\n");

        // Write document vectors.
        // Open file and write headers.
        outputStream = fsDirectory.createOutput(
                VectorStoreUtils.getStoreFileName(flagConfig.docvectorsfile(), flagConfig), IOContext.DEFAULT);

        // Write header giving number of dimensions for all vectors and make sure type is real.
        outputStream.writeString(VectorStoreWriter.generateHeaderString(flagConfig));

        // Write out document vectors
        for (cnt = 0; cnt < uT.cols; cnt++) {
            String thePath = luceneUtils.getDoc(cnt).get(flagConfig.docidfield());
            outputStream.writeString(thePath);
            float[] tmp = new float[flagConfig.dimension()];

            for (int i = 0; i < flagConfig.dimension(); i++)
                tmp[i] = (float) uT.value[i][cnt];
            RealVector docVector = new RealVector(tmp);
            docVector.normalize();

            docVector.writeToLuceneStream(outputStream);
        }
        outputStream.close();
        VerbatimLogger.info("Wrote " + cnt + " document vectors incrementally to file "
                + flagConfig.docvectorsfile() + ". Done.\n");


*/



    }

    public void buildPosIndex(){
        logger.debug("Calling buildPosIndex");
        logger.debug("Termweight is: " + termweight);
        logger.debug("Radius is: " + posIndexRadius);


        logger.debug("Calling buildLSAIndex");
        logger.debug("Termweight is: " + termweight);

        File termtermvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "Positional-term-" + termweight + ".bin");
        //File termvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "PosTermIndex");
        File docvectorfile = new File(wDir + File.separator + luceneIndexFilesFolder + File.separator + theCorpDir.getName() + File.separator + "Positional-doc-" + termweight + ".bin");

        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add("-luceneindexpath");
        arguments.add(lucIndexDir.toString());
        //arguments.add("-vectortype");
        //arguments.add("-dimension");
        //arguments.add("-seedlength");
        //arguments.add("-minfrequency");
        //arguments.add("-maxnonalphabetchars");
        //arguments.add("-docindexing");
        arguments.add("-termweight");
        arguments.add(termweight);
        //Writes docs one at a time for GC
        arguments.add("-docindexing");
        arguments.add("incremental");

        //arguments.add("-trainingcycles");
        //arguments.add(Integer.toString(amTraining));
        arguments.add("-termtermvectorsfile");
        arguments.add(termtermvectorfile.toString());
        //arguments.add("-termvectorsfile");
        //arguments.add(termvectorfile.toString());
        arguments.add("-docvectorsfile");
        arguments.add(docvectorfile.toString());
        arguments.add("-windowradius");
        arguments.add(Integer.toString(posIndexRadius));



        if (!termtermvectorfile.exists() || !docvectorfile.exists()) {
            try {
                FileUtils.touch(termtermvectorfile);
                FileUtils.touch(docvectorfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        String[] args = new String[arguments.size()];
        args = arguments.toArray(args);

        FlagConfig flagConfig;
        try {
            flagConfig = FlagConfig.getFlagConfig(args);
        } catch (IllegalArgumentException e) {
            //System.err.println(usageMessage);
            throw e;
        }

        logger.info("Seedlength: " + flagConfig.seedlength() + "\n"
                + ", Dimension: " + flagConfig.dimension() + "\n"
                + ", Vector type: " + flagConfig.vectortype() + "\n"
                + ", Minimum frequency: " + flagConfig.minfrequency() + "\n"
                + ", Maximum frequency: " + flagConfig.maxfrequency() + "\n"
                + ", Number non-alphabet characters: " + flagConfig.maxnonalphabetchars() + "\n"
                + ", Windowradius: " + flagConfig.windowradius() + "\n"
                + ", Docindexing: " + flagConfig.docindexing() + "\n"
                + ", Termvectorfile: " + flagConfig.termvectorsfile() + "\n"
                + ", Postermvectorfile: " + flagConfig.termtermvectorsfile() + "\n"
                + ", Docvectorsfile: " + flagConfig.docvectorsfile() + "\n"
                + ", Termweigh: " + flagConfig.termweight() + "\n"
                + ", Contents fields are: " + Arrays.toString(flagConfig.contentsfields()) + "\n");

        BuildPositionalIndex posIndexer = null;
        posIndexer.main(args);






    }









    //TODO Method to Build Index
    //TODO Method to Search selected Index for Term/Paragraphs (Textfield) and return Terms
    //TODO Method to search selected Index for Term/Paragraphs (Textfield) and return List of Docs



}
