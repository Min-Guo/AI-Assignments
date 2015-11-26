package textclustering;

import java.io.*;
import java.util.*;

public class Clustering {

    public static HashMap<String, WordInfo> checkWords = new HashMap<>();
    public static ArrayList<ClusteringInfo> docsCluster = new ArrayList<>();
    public static ArrayList<DuplicateWord> docWordList = new ArrayList<>();
    public static ArrayList<String> discardWords = new ArrayList<>();
    public static ArrayList<String> stopWords = new ArrayList<>();


    public static void readStopWords(String stopwordsFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(stopwordsFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                } else {
                    ArrayList<String> tempSplit = new ArrayList<>(Arrays.asList(line.split("\\s+")));
                    for (String temp : tempSplit) {
                        stopWords.add(temp);
                    }
                }
            }

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            stopwordsFile + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + stopwordsFile + "'");
        }

    }

    public static List<String> regularizeAndExtract(List<String> tempSplit) {
        List<String> words = new ArrayList<>();
        for (String word: tempSplit) {
            if (word.length() >= 3 && !stopWords.contains(word.toLowerCase())) {
                words.add(word);
            }
        }
        return words;
    }

    public static List<List<List<String>>> readDocuments(String inputFile) {
        List<List<List<String>>> result = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line;
            boolean preBlankLine = true;
            List<List<String>> tempDoc = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() != 0) {
                    List<String> tempSpilt = new ArrayList<>(Arrays.asList(line.split("[\\p{Punct}\\s]+")));
                    tempDoc.add(regularizeAndExtract(tempSpilt));
                    preBlankLine = false;
                } else {
                    if (!preBlankLine) {
                        result.add(new ArrayList<>(tempDoc));
                        tempDoc.clear();
                    }
                    preBlankLine = true;
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            inputFile + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + inputFile + "'");
        }
        return result;
    }

    public static void calcWordsFequency(List<List<List<String>>> originalWords) {
        for (int i = 0; i < originalWords.size(); i++) {
            List<List<String>> tempDoc = originalWords.get(i);
            for (List<String> tempSentence : tempDoc) {
                for (String tempWord : tempSentence) {
                    WordInfo tempInfo = new WordInfo("",tempWord);
                    tempInfo.addIndex(i);
                    checkWords.put(tempWord, tempInfo);
                }
            }
        }
    }

    public static void wordTodiscard(HashMap<String, WordInfo> checkWords, List<List<List<String>>> originalWords) {
        for (Map.Entry<String, WordInfo> entry : checkWords.entrySet()) {
            WordInfo tempInfo = entry.getValue();
            if (tempInfo.sizeIndex() > (originalWords.size() / 2)) {
                discardWords.add(entry.getKey());
            }
        }
    }

    public static void discard(ArrayList<String> discardWords, List<List<List<String>>> originalWords) {
        for (String tempWord : discardWords) {
            for (List<List<String>> tempdoc : originalWords) {
                for (List<String> tempSentence : tempdoc) {
                    for (Iterator<String> iterator = tempSentence.iterator(); iterator.hasNext(); ) {
                        String word = iterator.next();
                        if (word.equals(tempWord)) {
                            iterator.remove();
                        }

                    }
                }
            }
        }
    }

    public static void stemmerInput(List<List<List<String>>> documents) throws IOException {
        FileWriter writer = new FileWriter("stemmerIn.txt");
        for (List<List<String>> tempDoc : documents) {
            for (List<String> tempSentence : tempDoc) {
                for (String tempWord : tempSentence) {
                    writer.write(tempWord + " ");
                }
                writer.write("\n");
            }
            writer.write("\n\n\n");
        }
        writer.close();
    }

    public static boolean prevDocContainWord(List<Document> precDocWords, String word) {
        for (Document doc : precDocWords) {
            if (doc.checkDuplicateKey(word)) {
                return true;
            }
        }
        return false;
    }

    public static double updateWordFreq(List<Document> docWords, String word) {
        double freq = 0.0;
        for (Document doc : docWords) {
            if (doc.checkDuplicateKey(word)) {
                doc.getWordInfo(word).increaseFreq();
                freq = doc.getWordInfo(word).getFreq();
            }
        }
        return freq;
    }

    public static List<Document> calcStemedWordFreq(List<List<List<String>>> stremmerWords, List<List<List<String>>> originalWords) {
        List<Document> result = new ArrayList<>();
        for (int i = 0; i < stremmerWords.size(); i++) {
            Document doc = new Document();
            String tempName = "";
            for (int j = 0; j < originalWords.get(i).get(0).size(); j++) {
                tempName = tempName + originalWords.get(i).get(0).get(j) + " ";
            }
            doc.setName(tempName.substring(0, tempName.length()-1));
            for (int j = 0; j < stremmerWords.get(i).size(); j++) {
                for (int k = 0; k < stremmerWords.get(i).get(j).size(); k++) {
                    if (!doc.checkDuplicateKey(stremmerWords.get(i).get(j).get(k))) {
                        WordInfo tempInfo = new WordInfo(stremmerWords.get(i).get(j).get(k), originalWords.get(i).get(j).get(k));
                        if (prevDocContainWord(result, stremmerWords.get(i).get(j).get(k))) {
                            tempInfo.setFreq(updateWordFreq(result, stremmerWords.get(i).get(j).get(k)));
                            doc.putWordInfo(stremmerWords.get(i).get(j).get(k), tempInfo);
                        } else {
                            doc.putWordInfo(stremmerWords.get(i).get(j).get(k), tempInfo);
                        }
                    }
                }
            }
            result.add(doc);
        }
        return result;
    }

    public static void calcWordWeight(List<Document> documentWords) {
        for (Document tempDoc : documentWords) {
            tempDoc.calcWeight(documentWords.size());
        }
    }

    public static void findSameWord(List<Document> documentWords) {
        for (int i = 0; i < documentWords.size() - 1; i++) {
            for (int j = i + 1; j < documentWords.size(); j++) {
                DuplicateWord tempDW = new DuplicateWord();
                double tempTW = 0.0;
                tempDW.setDocNames(documentWords.get(i).getName());
                tempDW.setDocNames(documentWords.get(j).getName());
                for (Map.Entry<String, WordInfo> tempWords : documentWords.get(i).getWords().entrySet()) {
                    if (documentWords.get(j).getWords().containsKey(tempWords.getKey())) {
                        String word = tempWords.getKey();
                        WordInfo wordInfo = tempWords.getValue();
                        tempDW.setWordWeight(word, wordInfo);
                        tempTW += wordInfo.getWeight();
                    }
                }


                tempDW.setTotalWeight(tempTW);
                docWordList.add(tempDW);
            }
        }
    }

    public static int checkDuplicate(String doc, ArrayList<ClusteringInfo> docsList) {
        for (int i = 0; i < docsList.size(); i++) {
            if (docsList.get(i).getDocs().contains(doc)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean MergeComponent(ClusteringInfo docs, ArrayList<ClusteringInfo> docsList) {
        if ((checkDuplicate(docs.getDocs().get(0), docsList) == -1) && (checkDuplicate(docs.getDocs().get(1), docsList) == -1)) {
            return false;
        } else {
            if ((checkDuplicate(docs.getDocs().get(0), docsList) != -1) && (checkDuplicate(docs.getDocs().get(1), docsList) != -1)) {
                int i = checkDuplicate(docs.getDocs().get(0), docsList);
                int j = checkDuplicate(docs.getDocs().get(1), docsList);
                if (i != j) {
                    docsList.get(i).getDocs().addAll(docsList.get(j).getDocs());
                    docsList.get(i).getDupWords().addAll(docsList.get(j).getDupWords());
                    docsList.remove(j);
                }

            } else if ((checkDuplicate(docs.getDocs().get(0), docsList) != -1)) {
                int i = checkDuplicate(docs.getDocs().get(0), docsList);
                docsList.get(i).getDocs().add(docs.getDocs().get(1));
                docsList.get(i).getDupWords().addAll(docs.getDupWords());
            } else {
                int i = checkDuplicate(docs.getDocs().get(1), docsList);
                docsList.get(i).getDocs().add(docs.getDocs().get(0));
                docsList.get(i).getDupWords().addAll(docs.getDupWords());
            }
        }
        return true;
    }

    public static void connectedGraph(ClusteringInfo docs, ArrayList<ClusteringInfo> docsList) {
        if (docsList.isEmpty()) {
            docsList.add(docs);
        } else {
            if (!MergeComponent(docs, docsList)) {
                docsList.add(docs);
            }
        }
    }

    public static void clusteringDocs(ArrayList<DuplicateWord> docWordList, String parameterN) throws IOException{
        for (DuplicateWord docWord : docWordList) {
            if (docWord.getTotalWeight() > Double.parseDouble(parameterN)) {
                ClusteringInfo tempCI = new ClusteringInfo();
                tempCI.setDocs(docWord.getDocNames());
                tempCI.setDupWords(docWord.getDupWords());
                connectedGraph(tempCI, docsCluster);
            }
        }
        FileWriter writer = new FileWriter("Final_Output.txt");
        for (ClusteringInfo cluster: docsCluster) {
            writer.write(cluster.toString());
            writer.write(System.getProperty( "line.separator" ));
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        String documentsFile = args[0];
        String stopwordsFile = args[1];
        String parameterN = args[2];
        Stemmer stemmer = new Stemmer();
        readStopWords(stopwordsFile);
        List<List<List<String>>> originalWords = readDocuments(documentsFile);
        calcWordsFequency(originalWords);
        wordTodiscard(checkWords, originalWords);
        discard(discardWords, originalWords);
        stemmerInput(originalWords);
        stemmer.wordOutput("stemmerIn.txt");
        List<List<List<String>>> stemmerWords = readDocuments("stemmerOut.txt");
        List<Document> words = calcStemedWordFreq(stemmerWords, originalWords);
        calcWordWeight(words);
        findSameWord(words);
        clusteringDocs(docWordList, parameterN);
    }
}
