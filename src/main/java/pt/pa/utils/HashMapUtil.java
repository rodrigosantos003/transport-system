package pt.pa.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to sort HashMaps
 * @author Rodrigo Santos - 202100722,
 * João Fernandes - 202100718,
 * Rúben Dâmaso - 202100723
 *
 * [PL2 - Prof. André Sanguinetti]
 */
public class HashMapUtil {
    /**
     * Sorts a HashMap by values descending
     * @param map HashMap
     * @return Map sorted by values descending
     * @param <K> Key type
     * @param <V> Value type
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.<K, V>comparingByValue().reversed());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
