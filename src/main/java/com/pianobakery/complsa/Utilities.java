package com.pianobakery.complsa;

import java.io.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 29.07.15.
 */
public class Utilities {

    public Utilities() {
    }

    public static String removeQuoteFromString(String aString) {

        if (!aString.isEmpty()) {
            String withoutQuotes = aString.replace("\"", "");
            return withoutQuotes;
        }
        return null;

    }

    public static String readFileToString(File aFile) {


        if (!aFile.exists()) {
            return null;
        }

        String theContent;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(aFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            theContent = sb.toString();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //logger.debug("Metadata: " + metadata);

        return theContent;

    }

    public static List<String> getWords(String text) {
        List<String> words = new ArrayList<String>();
        BreakIterator breakIterator = BreakIterator.getWordInstance();
        breakIterator.setText(text);
        int lastIndex = breakIterator.first();
        while (BreakIterator.DONE != lastIndex) {
            int firstIndex = lastIndex;
            lastIndex = breakIterator.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                words.add(text.substring(firstIndex, lastIndex));
            }
        }

        return words;
    }

}
