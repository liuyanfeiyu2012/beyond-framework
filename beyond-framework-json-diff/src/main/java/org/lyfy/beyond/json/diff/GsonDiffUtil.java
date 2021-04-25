package org.lyfy.beyond.json.diff;


import com.google.gson.*;
import org.lyfy.beyond.json.diff.util.CompareUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: 谢星星
 * @Date: 2019/7/15 14:02
 * @Description:
 */
public class GsonDiffUtil implements Constants {

    public static String diff2String(Object originObject, Object currentObject) {
        return convertMapToJson(diff2Map(originObject, currentObject));
    }

    public static String diff2String(String originJsonStr, String currentJsonStr) {
        return convertMapToJson(diff2Map(originJsonStr, currentJsonStr));
    }

    public static Map<String, Object> diff2Map(Object originObject, Object currentObject) {
        Gson gson = new Gson();
        String originJsonStr = gson.toJson(originObject);
        String currentJsonStr = gson.toJson(currentObject);
        return diff2Map(originJsonStr, currentJsonStr);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> diff2Map(String originJsonStr, String currentJsonStr) {
        Map<String, Object> originMap = new LinkedHashMap<>();
        Map<String, Object> currentMap = new LinkedHashMap<>();

        JsonObject originJson = new JsonParser().parse(originJsonStr).getAsJsonObject();
        JsonObject currentJson = new JsonParser().parse(currentJsonStr).getAsJsonObject();
        convertJsonToMap(originJson, "", originMap);
        convertJsonToMap(currentJson, "", currentMap);

        return doCompare(originMap, currentMap);
    }

    /**
     * 将json数据转换为map存储用于比较
     */
    private static void convertJsonToMap(Object json, String root, Map<String, Object> resultMap) {
        if (json instanceof JsonObject) {
            JsonObject jsonObject = ((JsonObject) json);
            Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().getKey();
                Object value = jsonObject.get(key);
                String newRoot = "".equals(root) ? key + "" : root + DOT_SEPARATOR + key;
                if (value instanceof JsonObject || value instanceof JsonArray) {
                    convertJsonToMap(value, newRoot, resultMap);
                } else {
                    resultMap.put(newRoot, value);
                }
            }
        } else if (json instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) json;
            for (int i = 0; i < jsonArray.size(); i++) {
                Object value = jsonArray.get(i);
                String newRoot = "".equals(root) ? "[" + i + "]" : root + "[" + i + "]";
                if (value instanceof JsonObject || value instanceof JsonArray) {
                    convertJsonToMap(value, newRoot, resultMap);
                } else {
                    resultMap.put(newRoot, value);
                }
            }
        }
    }

    /**
     * 比较两个map，返回不同数据
     */
    private static Map<String, Object> doCompare(Map<String, Object> originMap, Map<String, Object> currentMap) {
        // 遍历currentMap，将currentMap的不同数据装进originMap，同时删除originMap中与currentMap相同的数据
        CompareUtil.compareCurrentMapWithOriginMap(originMap, currentMap);
        // 将originMap中有，而currentMap中没有的数据装入originMap中
        CompareUtil.compareOriginMapWithCurrentMap(originMap);
        return originMap;
    }

    private static String convertMapToJson(Map<String, Object> map) {
        return new Gson().toJson(map);
    }
}
