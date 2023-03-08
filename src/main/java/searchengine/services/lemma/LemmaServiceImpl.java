package searchengine.services.lemma;

import com.sun.istack.NotNull;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Only serves for russian words for now
 */
@Service
public class LemmaServiceImpl implements LemmaService {

    private LuceneMorphology russianMorphology;
    private final String[] excludedWords = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};
    private final static String SPLIT_REGEX = "[\\s.,\\-:;]+"; // may add more

    /**
     * Method might be extended.
     * Declare new LuceneMorphology field and then add initialization in this method for usage
     */
    private void initMorphology() {
        try {
            russianMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            // TODO: оповестить об ошибке
        }

    }

    @Override
    public Map<String, Integer> getLemmaToAmountMap(String sourceText) {
        return getLemmaToAmountMap(sourceText, false);
    }

    /**
     * @param includeUnique if true, then word that contains any not cyrillic symbol,
     *                      word saves as unique into result map
     * @return map of lemmas with amount of it
     */
    @Override
    public Map<String, Integer> getLemmaToAmountMap(String sourceText, boolean includeUnique) {
        initMorphology();
        String[] words = sourceText.trim().split(SPLIT_REGEX);
        Map<String, Integer> lemmaToCountMap = new HashMap<>();
        for (String word : words) {
            String lemma = null;
            List<String> wordMorphInfo = getMorphInfo(word);
            if (wordMorphInfo != null) {
                if (isExcludedLemma(wordMorphInfo)) {
                    continue;
                }
                lemma = russianMorphology.getNormalForms(word).get(0);
            } else if (includeUnique) {
                lemma = word;
            }
            if (lemma != null) {
                Integer lemmaCount = lemmaToCountMap.get(lemma);
                if (lemmaCount == null) {
                    lemmaCount = 0;
                }
                lemmaToCountMap.put(lemma, ++lemmaCount); // increment
            }
        }
        return lemmaToCountMap;
    }

    /**
     * if returns null then is a sign of exception,
     * that means word unsupported by current realization and might be unique
     */
    private List<String> getMorphInfo(String word) {
        //TODO: написать regex для проверки содержания символов в слове,
        // чтобы исключить слова полностью из цифр или сторонних символов
        List<String> morphInfo = new ArrayList<>();
        if (true) { // FIXME: if match regex
            try {
                morphInfo = russianMorphology.getMorphInfo(word);
            } catch (Exception e) {
                return null;
            }
        }
        return morphInfo;
    }

    private boolean isExcludedLemma(@NotNull List<String> wordMorphInfo) {
        if (wordMorphInfo.isEmpty()) {
            return true;
        }
        String lemma = wordMorphInfo.get(0);
        for (String excludedWord : excludedWords) {
            if (excludedWord.contains(lemma)) {
                return true;
            }
        }
        return false;
    }
}
