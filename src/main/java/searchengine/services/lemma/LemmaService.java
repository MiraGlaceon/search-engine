package searchengine.services.lemma;

import java.util.Map;

public interface LemmaService {
    Map<String, Integer> getLemmaToAmountMap(String sourceText);
    Map<String, Integer> getLemmaToAmountMap(String sourceText, boolean includeUnique);
}
