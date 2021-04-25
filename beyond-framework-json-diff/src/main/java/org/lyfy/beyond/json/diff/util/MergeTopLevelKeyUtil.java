package org.lyfy.beyond.json.diff.util;

import org.lyfy.beyond.json.diff.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author: 谢星星
 * @Date: 2019/7/15 20:00
 * @Description:
 */
public class MergeTopLevelKeyUtil implements Constants {

    public static final List<String> merge(Map<String, Object> objectMap) {
        HashSet<String> topLevelKeySet = new HashSet<>();
        objectMap.keySet().forEach(key -> {

            if (key.contains(DOT_SEPARATOR)) {
                String topKey = key.split("\\.")[0];
                topLevelKeySet.add(topKey);
            } else {
                topLevelKeySet.add(key);
            }
        });

        return new ArrayList<>(topLevelKeySet);
    }
}
