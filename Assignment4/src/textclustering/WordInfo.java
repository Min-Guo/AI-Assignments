package textclustering;
import java.util.HashSet;
import java.util.Set;

public class WordInfo {
    private double freq;
    private double weight;
    private Set<Integer> docIndex;

    public WordInfo () {
        docIndex = new HashSet<>();
        freq = 1;
    }

    public void addIndex (Integer index) {
        docIndex.add(index);
    }

    public int sizeIndex () {
        return docIndex.size();
    }

    public void setFreq (double freuency) {
        freq = freuency;
    }

    public double getFreq () {
        return freq;
    }

    public void increaseFreq() {
        freq++;
    }

    public void setWeight (double wordWeight) {
        weight = wordWeight;
    }

    public double getWeight () {
        return weight;
    }
}
