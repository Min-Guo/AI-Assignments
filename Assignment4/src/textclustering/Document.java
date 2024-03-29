package textclustering;


import java.util.*;
import java.lang.Math;

public class Document {
    private String docName;
    private WordInfo nameInfo;
    private HashMap<String, WordInfo> words;

    public Document () {
        words = new HashMap<> ();
    }

    public void setName (String name) {
        docName = name;
    }

    public String getName () {
        return docName;
    }

    public HashMap<String, WordInfo> getWords () {
        return words;
    }

    public boolean checkDuplicateKey (String key) {
        return words.containsKey(key);
    }

    public void putWordInfo (String word, WordInfo wordInfo) {
        words.put(word, wordInfo);
    }

    public WordInfo getWordInfo (String tempWord) {
        return words.get(tempWord);
    }

    /*public double getWordNumber (HashMap<String, WordInfo> words) {
        Collection<WordInfo> c = words.values();
        Iterator itr = c.iterator();
        while (itr.hasNext()) {
            WordInfo wordInfo = (WordInfo) itr.next();
            wordNumber = wordNumber + wordInfo.getFreq();
        }
        return wordNumber;
    }*/

    public void calcWeight (double textNumber) {
        /*double wordsNum = getWordNumber(words);*/
        for (Map.Entry<String, WordInfo> entry : words.entrySet()) {
            double tempWeight;
            WordInfo wordInfo = entry.getValue();
            tempWeight = - Math.log(wordInfo.getFreq() / textNumber)/ Math.log(2.0) ;
            wordInfo.setWeight(tempWeight);
        }
    }
}