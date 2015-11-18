import javax.print.Doc;
import java.io.*;
import java.util.*;

/**
 * Created by min on 11/15/15.
 */
public class Clustering {
    public class WordInfo {
        Integer time;
        String word;
    }
    public class Document {
        String name;
        ArrayList<WordInfo> words;
    }
    public static ArrayList<ArrayList<String>> documents = new ArrayList<>();
    public static ArrayList<String> stopWords = new ArrayList<>();
    public static ArrayList<Document> file = new ArrayList<>();
    public static Clustering cluster = new Clustering();

    public static void readStopWords(String stopwordsFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(stopwordsFile));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                } else {
                    ArrayList<String> tempSplit = new ArrayList<>(Arrays.asList(line.split("\\s+")));
                    for (String temp: tempSplit) {
                        stopWords.add(temp);
                    }
                }
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            stopwordsFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + stopwordsFile + "'");
        }

    }

    public static void readDocuments(String documentsFile){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(documentsFile));
            String line;
            boolean preBlankLine = true;
            ArrayList<String> tempDoc = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() != 0) {
                    tempDoc.add(line);
                    preBlankLine = false;
                } else {
                    if (!preBlankLine) {
                        documents.add(new ArrayList<>(tempDoc));
                        tempDoc.clear();
                    }
                    preBlankLine = true;
                }
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            documentsFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + documentsFile + "'");
        }

    }

    public static Document regExtrRule (ArrayList<String> spilt, Document document) {
        for (String tempString : spilt) {
            tempString = tempString.toLowerCase();
            WordInfo tempwordInfo = cluster.new WordInfo();
            boolean duplicateWord = false;
            if ((tempString.length() >= 3) && (!stopWords.contains(tempString))) {
                if (document.words != null) {
                    for (int i = 0; i < document.words.size(); i++) {
                        if (document.words.get(i).word.equals(tempString)) {
                            document.words.get(i).time ++;
                            duplicateWord = true;
                        }
                    }
                    if (!duplicateWord) {
                        tempwordInfo.time = 1;
                        tempwordInfo.word = tempString;
                        document.words.add(tempwordInfo);
                    }
                } else {
                    document.words = new ArrayList<>();
                    tempwordInfo.time = 1;
                    tempwordInfo.word = tempString;
                    document.words.add(tempwordInfo);
                }
            }
        }
        return document;
    }

    public static void regExtract (ArrayList<ArrayList<String>> documents){
        for (ArrayList<String> temp : documents ) {
            Document tempDoc = cluster.new Document();
            tempDoc.name = temp.get(0);
            for (int i = 1; i < temp.size(); i++) {
                ArrayList<String> tempSpilt = new ArrayList<>(Arrays.asList(temp.get(i).split("[\\p{Punct}\\s]+")));
                tempDoc = regExtrRule(tempSpilt, tempDoc);
            }
            file.add(tempDoc);
        }
    }


    public static void main (String[] args) throws IOException {
        String documentsFile = args[0];
        String stopwordsFile = args[1];
        readStopWords(stopwordsFile);
        readDocuments(documentsFile);
        regExtract(documents);
    }
}
