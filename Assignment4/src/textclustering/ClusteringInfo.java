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

    @Override
    public String toString () {
        List<WordInfo> words = new ArrayList<>(dupWords);
        String resultName = words.get(0).toString();
        for (int i = 1; i < words.size(); i++) {
            resultName += ", ";
            resultName += words.get(i).toString();
        }

        resultName += ": ";
        resultName += docs.get(0);
        for (int i = 1; i < docs.size(); i++) {
            resultName += ", ";
            resultName += docs.get(i);
        }
        return resultName;
    }
}
