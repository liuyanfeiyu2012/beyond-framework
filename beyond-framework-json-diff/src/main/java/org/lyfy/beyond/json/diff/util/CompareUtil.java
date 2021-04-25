package org.lyfy.beyond.json.diff.util;

import org.lyfy.beyond.json.diff.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description
 *
 * @author jiangyangchun
 * @date 2021-04-20
 */
public class CompareUtil {

    /**
     * 统一originMap存在，而currentMap中不存在的数据放入originMap中
     */
    public static void compareOriginMapWithCurrentMap(Map<String, Object> originMap) {
        for (Map.Entry<String, Object> item : originMap.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            if (!(value instanceof Map)) {
                Map<String, Object> differenceMap = new LinkedHashMap<>();
                differenceMap.put(Constants.ORIGIN_VALUE, value);
                differenceMap.put(Constants.CURRENT_VALUE, "");
                originMap.put(key, differenceMap);
            }
        }
    }

    /**
     * 将当前Map与原始Map比较，并将数据统一存在原始Map中
     */
    public static void compareCurrentMapWithOriginMap(Map<String, Object> originMap, Map<String, Object> currentMap) {
        for (Map.Entry<String, Object> item : currentMap.entrySet()) {
            String key = item.getKey();
            Object newValue = item.getValue();
            Map<String, Object> differenceMap = new LinkedHashMap<>();
            if (originMap.containsKey(key)) {
                Object oldValue = originMap.get(key);
                if (newValue.equals(oldValue)) {
                    originMap.remove(key);
                    continue;
                } else {
                    differenceMap.put(Constants.ORIGIN_VALUE, oldValue);
                    differenceMap.put(Constants.CURRENT_VALUE, newValue);
                    originMap.put(key, differenceMap);
                }
            } else {
                differenceMap.put(Constants.ORIGIN_VALUE, "");
                differenceMap.put(Constants.CURRENT_VALUE, newValue);
                originMap.put(key, differenceMap);
            }
        }
    }
}
