package textclustering;



import java.util.ArrayList;
import java.util.HashMap;


public class DuplicateWord {
    private ArrayList<String> docNames = new ArrayList<>();
    private double totalWeight;
    private HashMap<String, WordInfo> wordWeight;

    public DuplicateWord () {
        wordWeight = new HashMap<>();
    }

    public void setDocNames (String names) {
        docNames.add(names);
    }

    public void setTotalWeight (double weight) {
        totalWeight = weight;
    }

    public void setWordWeight (String word, WordInfo wordinfo) {
        wordWeight.put(word, wordinfo);
    }

    public ArrayList<String> getDocNames () {
        return docNames;
    }

    public double getTotalWeight (){
        return totalWeight;
    }

    public ArrayList<WordInfo> getDupWords () {
        return new ArrayList<>(wordWeight.values());
    }
}
