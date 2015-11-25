package textclustering;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClusteringInfo {
    private ArrayList<String> docs;
    private Set<String> dupWords;

    public ClusteringInfo () {
        docs = new ArrayList<>();
    }

    public void setDocs (ArrayList<String> names) {
       docs = new ArrayList<>(names);
    }

    public void setDupWords (ArrayList<String> words) {
        dupWords = new HashSet<>(words);
    }

    public ArrayList<String> getDocs() {
        return docs;
    }

    public Set<String> getDupWords() {
        return dupWords;
    }
}
