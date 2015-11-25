package textclustering;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DuplicateWord {
    private ArrayList<String> docNames = new ArrayList<>();
    private double totalWeight;
    private HashMap<String, Double> wordWeight;

    public DuplicateWord () {
        wordWeight = new HashMap<>();
    }

    public void setDocNames (String names) {
        docNames.add(names);
    }

    public void setTotalWeight (double weight) {
        totalWeight = weight;
    }

    public void setWordWeight (String word, Double weight) {
        wordWeight.put(word, weight);
    }

    public ArrayList<String> getDocNames () {
        return docNames;
    }

    public double getTotalWeight (){
        return totalWeight;
    }

    public ArrayList<String> getDupWords () {
        return new ArrayList<>(wordWeight.keySet());
    }
}
