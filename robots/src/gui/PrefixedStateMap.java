package robots.src.gui;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class PrefixedStateMap extends AbstractMap<String, String> {
    private final Map<String, String> original;
    private final String prefix;

    public PrefixedStateMap(Map<String, String> original, String prefix) {
        this.original = original;
        this.prefix = prefix;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return new AbstractSet<Entry<String, String>>() {
            @Override
            public Iterator<Entry<String, String>> iterator() {
                return original.entrySet().stream()
                        .filter(e -> e.getKey().startsWith(prefix))
                        .map(e -> (Entry<String, String>)new AbstractMap.SimpleEntry<>(
                                e.getKey().substring(prefix.length()),
                                e.getValue()))
                        .iterator();
            }

            @Override
            public int size() {
                return (int) original.keySet().stream()
                        .filter(k -> k.startsWith(prefix))
                        .count();
            }
        };
    }

    @Override
    public String put(String key, String value) {
        return original.put(prefix + key, value);
    }
}

