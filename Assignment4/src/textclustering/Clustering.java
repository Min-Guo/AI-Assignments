package textclustering;
import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.util.*;

public class Clustering {

    public class Document {
        String docName;
        HashMap<String, WordInfo> words;
    }
    public static HashMap<String, WordInfo> checkWords = new HashMap<>();
    public static ArrayList<ArrayList<ArrayList<String>>> documents = new ArrayList<>();
    public static ArrayList<String> originalWords = new ArrayList<>();
    public static ArrayList<WordInfo> originalWordInfo = new ArrayList<>();
    public static ArrayList<String> discardWords = new ArrayList<>();
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

    public static ArrayList<String> regularizeAndExtract (ArrayList<String> tempSplit) {
        for (Iterator<String> iterator = tempSplit.iterator(); iterator.hasNext();) {
            String word = iterator.next();
            if (word.length() < 3) {
                iterator.remove();
            }
            if (stopWords.contains(word)) {
                iterator.remove();
            }
        }
        for (int i = 0; i < tempSplit.size(); i++) {
            tempSplit.set(i, tempSplit.get(i).toLowerCase());
        }

        return tempSplit;
    }

    public static void readDocuments(String documentsFile){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(documentsFile));
            String line;
            boolean preBlankLine = true;
            ArrayList<ArrayList<String >> tempDoc = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() != 0) {
                    ArrayList<String> tempSpilt = new ArrayList<>(Arrays.asList(line.split("[\\p{Punct}\\s]+")));
                    tempDoc.add(regularizeAndExtract(tempSpilt));
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

    public static void calcWordsFequency (ArrayList<ArrayList<ArrayList<String>>> documnets) {
        for (int i = 0; i < documnets.size() ; i++) {
            ArrayList<ArrayList<String>> tempDoc = documnets.get(i);
            for (ArrayList<String> tempSentence : tempDoc) {
                for (String tempWord : tempSentence) {
                        WordInfo tempInfo = new WordInfo();
                        tempInfo.addIndex(i);
                        checkWords.put(tempWord, tempInfo);
                }
            }
        }
    }

    public static void wordTodiscard (HashMap<String, WordInfo> checkWords) {
      for (Map.Entry<String, WordInfo> entry : checkWords.entrySet()) {
          WordInfo tempInfo = new WordInfo();
          tempInfo = entry.getValue();
          if (tempInfo.sizeIndex() > (documents.size() / 2)) {
              discardWords.add(entry.getKey());
          }
      }
    }

    public static void discard (ArrayList<String> discardWords) {
        for (String tempWord : discardWords) {
            for (ArrayList<ArrayList<String>> tempdoc :documents) {
                for (ArrayList<String> tempSentence : tempdoc) {
                    for (Iterator<String> iterator = tempSentence.iterator(); iterator.hasNext();) {
                        String word = iterator.next();
                        if (word.equals(tempWord)) {
                            iterator.remove();
                        }

                    }
                }
            }
        }
    }

    public static void stemmerInput (ArrayList<ArrayList<ArrayList<String>>> documents) throws IOException {
        FileWriter writer = new FileWriter ("stemmerIn.txt");
        for (ArrayList<ArrayList<String>> tempDoc: documents) {
            for (ArrayList<String> tempSentence : tempDoc) {
                for (String tempWord : tempSentence) {
                    writer.write(tempWord + " ");
                }
                writer.write("\n");
            }
            writer.write("\n\n\n");
        }
        writer.close();
    }

    public static void main (String[] args) throws IOException {
        String documentsFile = args[0];
        String stopwordsFile = args[1];
        Stemmer stemmer = new Stemmer();
        readStopWords(stopwordsFile);
        readDocuments(documentsFile);
        calcWordsFequency(documents);
        wordTodiscard(checkWords);
        discard(discardWords);
        stemmerInput(documents);
        stemmer.wordOutput("stemmerIn.txt");
        /*regExtract(documents);*/
    }
}
