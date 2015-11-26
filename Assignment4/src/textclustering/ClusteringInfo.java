package textclustering;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusteringInfo {
    private List<String> docs;
    private Set<WordInfo> dupWords;

    public ClusteringInfo () {
        docs = new ArrayList<>();
    }

    public void setDocs (List<String> names) {
       docs = new ArrayList<>(names);
    }

    public void setDupWords (List<WordInfo> words) {
        dupWords = new HashSet<>(words);
    }

    public List<String> getDocs() {
        return docs;
    }

    public Set<WordInfo> getDupWords() {
        return dupWords;
    }
}
