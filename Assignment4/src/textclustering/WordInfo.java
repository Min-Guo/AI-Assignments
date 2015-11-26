package textclustering;

import java.util.*;

public class WordInfo {
    private double freq;
    private double weight;
    private Set<Integer> docIndex;
    private String stremmWord;
    private String originalWord;

    public WordInfo(String stremmWord, String originalWord) {
        docIndex = new HashSet<>();
        freq = 1;
        this.stremmWord = stremmWord;
        this.originalWord = originalWord;
    }

    public void addIndex(Integer index) {
        docIndex.add(index);
    }

    public int sizeIndex() {
        return docIndex.size();
    }

    public void setFreq(double freuency) {
        freq = freuency;
    }

    public double getFreq() {
        return freq;
    }

    public void increaseFreq() {
        freq++;
    }

    public void setWeight(double wordWeight) {
        weight = wordWeight;
    }

    public double getWeight() {
        return weight;
    }

    public String stemmWord() {
        return stremmWord;
    }
@Override
    public boolean equals(Object obj) {
    if (obj == null) {
        return false;
    }
    if (this.getClass() != obj.getClass()) {
        return false;
    }
    if (!this.stremmWord.equals(((WordInfo) obj).stemmWord())) {
        return false;
    }
    return true;
    }
}
