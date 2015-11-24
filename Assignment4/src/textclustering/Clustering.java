package textclustering;
import com.sun.javadoc.Doc;

import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.util.*;

public class Clustering {

    public static HashMap<String, WordInfo> checkWords = new HashMap<>();
    public static ArrayList<ArrayList<ArrayList<String>>> stemmerDocuments = new ArrayList<>();
    public static ArrayList<ArrayList<ArrayList<String>>> documents = new ArrayList<>();
    public static ArrayList<Document> documentWords = new ArrayList<>();
    public static ArrayList<String> discardWords = new ArrayList<>();
    public static ArrayList<String> stopWords = new ArrayList<>();


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

    public static void readDocuments(String inputFile, ArrayList<ArrayList<ArrayList<String>>> outputArray){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
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
                        outputArray.add(new ArrayList<>(tempDoc));
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
                            inputFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + inputFile + "'");
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

    public static boolean prevDocContainWord (ArrayList<Document> precDocWords, String word) {
        for (Document doc : precDocWords) {
            if (doc.checkDuplicateKey(word)) {
                return true;
            }
        }
        return false;
    }

    public static double updateWordFreq (ArrayList<Document> docWords, String word) {
        double freq = 0.0;
        for (Document doc : docWords) {
            if (doc.checkDuplicateKey(word)){
                doc.getWordInfo(word).increaseFreq();
                freq = doc.getWordInfo(word).getFreq();
            }
        }
        return freq;
    }

    public static void calcStemedWordFreq (ArrayList<ArrayList<ArrayList<String>>> documents) {
        for (ArrayList<ArrayList<String>> tempDoc : documents) {
            Document doc = new Document();
                String tempName = "";
                for (int j = 0; j < tempDoc.get(0).size(); j ++) {
                    tempName = tempName + tempDoc.get(0).get(j) + " ";
                }
                doc.setName(tempName);
                for (ArrayList<String> tempSentence : tempDoc) {
                    for (String tempWord : tempSentence) {
                        if (!doc.checkDuplicateKey(tempWord)) {
                            WordInfo tempInfo = new WordInfo();
                            if (prevDocContainWord(documentWords, tempWord)){
                                tempInfo.setFreq(updateWordFreq(documentWords, tempWord));
                                doc.putWordInfo(tempWord, tempInfo);
                            } else {
                                doc.putWordInfo(tempWord, tempInfo);
                            }
                        }
                    }
                }
            documentWords.add(doc);
        }
    }

    public static void calcWordWeight (ArrayList<Document> documentWords) {
        for (Document tempDoc : documentWords) {
            tempDoc.calcWeight();
        }
    }

    public static void findSameWord(ArrayList<Document> documentWords) {
        for (int i = 0; i < documentWords.size() - 1; i++) {
            for (int j = i + 1; j < documentWords.size(); j++) {
                for (Map.Entry<String, WordInfo> tempDoc : documentWords.get(i).getWords().entrySet()) {
                    if (documentWords.get(j).getWords().containsKey(tempDoc.getKey())) {
                        System.out.println("Doc" + i + " "+ "Doc" + j + " " + "Same word: " + tempDoc.getKey());
                    }
                }

            }
        }
    }

    public static void main (String[] args) throws IOException {
        String documentsFile = args[0];
        String stopwordsFile = args[1];
        Stemmer stemmer = new Stemmer();
        readStopWords(stopwordsFile);
        readDocuments(documentsFile, documents);
        calcWordsFequency(documents);
        wordTodiscard(checkWords);
        discard(discardWords);
        stemmerInput(documents);
        stemmer.wordOutput("stemmerIn.txt");
        readDocuments("stemmerOut.txt", stemmerDocuments);
        calcStemedWordFreq(stemmerDocuments);
        calcWordWeight(documentWords);
        findSameWord(documentWords);
        System.out.println("Hello");
    }
}
