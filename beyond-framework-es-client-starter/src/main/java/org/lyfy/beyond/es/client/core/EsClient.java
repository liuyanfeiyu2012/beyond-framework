package org.lyfy.beyond.es.client.core;

import org.lyfy.beyond.es.client.response.AggsRespDto;
import org.lyfy.beyond.es.client.response.PageRespDto;
import org.lyfy.beyond.es.client.util.DateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.IdFieldMapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 谢星星
 * @date 2019/12/18 11:00
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsClient {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EsClient.class);

    private static final int DEEP_SEARCH_INTERVAL = 5000;

    public static final int MAX_PAGE_SIZE = 2000;

    private static Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

    private RestHighLevelClient client;

    public boolean indexExists(String index) {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        request.local(false);
        request.humanReadable(true);

        boolean exists = false;
        try {
            exists = client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return exists;
    }

    public <T> List<T> search(SearchRequest rq, Class<T> classOfT) {
        SearchResponse rp = null;
        try {
            rp = client.search(rq, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (responseInvalid(rp)) {
            return Collections.emptyList();
        }

        return Arrays.stream(rp.getHits().getHits()).map(b -> GSON.fromJson(b.getSourceAsString(), classOfT)).collect(Collectors.toList());
    }

    public <T> List<T> search(String index,
                              String indexType,
                              int from,
                              int size,
                              BoolQueryBuilder boolQueryBuilder,
                              Map<String, Boolean> sortFieldsToAsc,
                              Class<T> classOfT) {

        return search(index, indexType, from, size, boolQueryBuilder, null, sortFieldsToAsc, null, null, classOfT);
    }

    public <T> List<T> search(String index,
                              String indexType,
                              int from,
                              int size,
                              BoolQueryBuilder boolQueryBuilder,
                              CollapseBuilder collapseBuilder,
                              Map<String, Boolean> sortFieldsToAsc,
                              String[] includeFields,
                              String[] excludeFields,
                              Class<T> classOfT) {
        return searchIndex(index, indexType, from, size, boolQueryBuilder, collapseBuilder, sortFieldsToAsc, includeFields, excludeFields, classOfT);
    }

    public <T> List<T> searchIndex(String index,
                                   String indexType,
                                   int from,
                                   int size,
                                   BoolQueryBuilder boolQueryBuilder,
                                   CollapseBuilder collapseBuilder,
                                   Map<String, Boolean> sortFieldsToAsc,
                                   String[] includeFields,
                                   String[] excludeFields,
                                   Class<T> classOfT) {
        SearchResponse rp = getSearchIndexResponse(index, indexType, from, size, boolQueryBuilder, collapseBuilder, sortFieldsToAsc, includeFields, excludeFields);
        if (responseInvalid(rp)) {
            return Collections.emptyList();
        }

        return Arrays.stream(rp.getHits().getHits()).map(hit -> GSON.fromJson(hit.getSourceAsString(), classOfT)).collect(Collectors.toList());
    }

    public <T> PageRespDto<T> pageRespSearch(SearchRequest rq,
                                             Class<T> classOfT) {
        SearchResponse rp = null;
        try {
            rp = client.search(rq, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getPageRespDto(rp, classOfT);
    }

    public <T> PageRespDto<T> pageRespSearch(String index,
                                             String indexType,
                                             int from,
                                             int size,
                                             BoolQueryBuilder boolQueryBuilder,
                                             Map<String, Boolean> sortFieldsToAsc,
                                             Class<T> classOfT) {
        return pageRespSearch(index, indexType, from, size, boolQueryBuilder, null, sortFieldsToAsc, null, null, classOfT);
    }

    public <T> PageRespDto<T> pageRespSearch(String index,
                                             String indexType,
                                             int from,
                                             int size,
                                             BoolQueryBuilder boolQueryBuilder,
                                             CollapseBuilder collapseBuilder,
                                             Map<String, Boolean> sortFieldsToAsc,
                                             String[] includeFields,
                                             String[] excludeFields,
                                             Class<T> classOfT) {
        SearchResponse rp = getSearchIndexResponse(index, indexType, from, size, boolQueryBuilder, collapseBuilder, sortFieldsToAsc, includeFields, excludeFields);
        return getPageRespDto(rp, classOfT);
    }

    public <T> PageRespDto<T> getPageRespDto(SearchResponse rp, Class<T> classOfT) {
        if (responseInvalid(rp)) {
            return PageRespDto.<T>builder().resultList(Collections.emptyList()).totalCount(0).build();
        }

        return PageRespDto.<T>builder()
                .resultList(Arrays.stream(rp.getHits().getHits()).map(b -> GSON.fromJson(b.getSourceAsString(), classOfT)).collect(Collectors.toList()))
                .totalCount(rp.getHits().getTotalHits().value)
                .build();
    }

    public boolean documentDelete(String index, String indexType, String documentId) {
        DeleteRequest request = new DeleteRequest(index, indexType, documentId);
        DeleteResponse deleteResponse = null;
        try {
            deleteResponse = client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            return true;
        }
        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            return true;
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                throw new RuntimeException(failure.reason());
            }
        }
        return false;
    }

    public void documentDelete(String index, String indexType, QueryBuilder query) {
        DeleteByQueryRequest request = new DeleteByQueryRequest();
        request.indices(index);
        request.types(indexType);
        request.setQuery(query);
        try {
            BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void documentUpdate(String index, String indexType, QueryBuilder query, Script script) {
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest();
        updateByQueryRequest.indices(index);
        updateByQueryRequest.setDocTypes(indexType);
        updateByQueryRequest.setQuery(query);
        updateByQueryRequest.setScript(script);
        try {
            BulkByScrollResponse bulkByScrollResponse = client.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean documentCreate(String index, String indexType,
                                  String documentId, String jsonStr) {
        IndexRequest request = new IndexRequest(index, indexType, documentId);

        request.source(jsonStr, XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            return true;
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            return true;
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                throw new RuntimeException(failure.reason());
            }
        }
        return false;
    }

    public String documentCreate(String index, String indexType, String json) {
        IndexRequest request = new IndexRequest(index, indexType);
        request.source(json, XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String id = indexResponse.getId();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED
                || indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            return id;
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            return id;
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo
                    .getFailures()) {
                throw new RuntimeException(failure.reason());
            }
        }
        return null;
    }

    public void save(String index, String indexType, String id, String jsonString) {
        UpdateRequest updateRequest = new UpdateRequest(index, indexType, id);
        updateRequest.doc(jsonString, XContentType.JSON);
        updateRequest.upsert(jsonString, XContentType.JSON);
        try {
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long count(String index, String indexType, QueryBuilder query) {
        return count(index, indexType, query, null);
    }

    public long count(String index, String indexType, QueryBuilder query, CollapseBuilder collapseBuilder) {
        CountRequest countRequest = new CountRequest();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        if (collapseBuilder != null) {
            sourceBuilder.collapse(collapseBuilder);
        }
        countRequest.indices(index);
        countRequest.types(indexType);
        countRequest.source(sourceBuilder);
        try {
            return client.count(countRequest, RequestOptions.DEFAULT).getCount();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BulkResponse bulk(BulkRequest bulkRequest) {
        try {
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("bulkSave exception:{}", e);
            throw new RuntimeException(e);
        }
    }

    private SearchResponse getSearchIndexResponse(String index,
                                                  String indexType,
                                                  int from,
                                                  int size,
                                                  BoolQueryBuilder boolQueryBuilder,
                                                  CollapseBuilder collapseBuilder,
                                                  Map<String, Boolean> sortFieldsToAsc,
                                                  String[] includeFields,
                                                  String[] excludeFields) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //分页
            from = from <= -1 ? 0 : from;
            size = size >= MAX_PAGE_SIZE ? MAX_PAGE_SIZE : size;
            size = size <= 0 ? 20 : size;
            sourceBuilder.from(from);
            sourceBuilder.size(size);

            //排序
            if (sortFieldsToAsc != null && !sortFieldsToAsc.isEmpty()) {
                sortFieldsToAsc.forEach((k, v) -> sourceBuilder.sort(new FieldSortBuilder(k).order(v ? SortOrder.ASC : SortOrder.DESC)));
            }

            //返回和排除列
            if (!CollectionUtils.isEmpty(includeFields) || !CollectionUtils.isEmpty(excludeFields)) {
                sourceBuilder.fetchSource(includeFields, excludeFields);
            }
            sourceBuilder.query(boolQueryBuilder);

            if (collapseBuilder != null) {
                sourceBuilder.collapse(collapseBuilder);
            }

            SearchRequest rq = new SearchRequest();
            //索引
            rq.indices(index);
            rq.types(indexType);
            //各种组合条件
            rq.source(sourceBuilder);

            //请求
            LOGGER.debug("Search Query, dsl:{}", rq.source().toString());
            return client.search(rq, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 深度查询返回sortValues
     *
     * @param index            esIndex
     * @param indexType        esIndexType
     * @param size             查询深度
     * @param boolQueryBuilder 　查询条件
     * @param sortFieldsToAsc  　排序
     * @param sortValues       　当前sortValues
     * @return 下次传入的sortValues, null－没有匹配的记录
     */
    public Object[] getSortValues(String index,
                                  String indexType,
                                  int size,
                                  BoolQueryBuilder boolQueryBuilder,
                                  LinkedHashMap<String, Boolean> sortFieldsToAsc,
                                  Object[] sortValues) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //_source
            sourceBuilder.fetchSource(SearchSourceBuilder.SORT_FIELD.getPreferredName(), null);
            //query criteria
            sourceBuilder.query(boolQueryBuilder);
            //return size
            sourceBuilder.size(size);
            //排序sort
            sortFieldsToAsc.forEach((k, v) -> sourceBuilder.sort(new FieldSortBuilder(k).order(v ? SortOrder.ASC : SortOrder.DESC)));
            if (!ObjectUtils.isEmpty(sortValues)) {
                sourceBuilder.searchAfter(sortValues);
            }
            SearchRequest rq = new SearchRequest();
            //索引
            rq.indices(index);
            rq.types(indexType);
            //各种组合条件
            rq.source(sourceBuilder);
            SearchResponse sr = client.search(rq, RequestOptions.DEFAULT);
            if (sr.getHits().getHits().length == 0) {
                return null;
            }
            return sr.getHits().getHits()[sr.getHits().getHits().length - 1].getSortValues();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 使用searchAfter进行深度查询
     *
     * @param index            　esIndex
     * @param indexType        　esIndexType
     * @param size             　查询深度
     * @param boolQueryBuilder 　查询条件
     * @param collapseBuilder  　折叠显示
     * @param sortFieldsToAsc  　排序
     * @param includeFields    　显示字段
     * @param excludeFields    　不显示字段
     * @param sortValues       　当前sortValues
     * @return 查询结果
     */
    public SearchResponse getSearchAfterResponse(String index,
                                                 String indexType,
                                                 int size,
                                                 BoolQueryBuilder boolQueryBuilder,
                                                 CollapseBuilder collapseBuilder,
                                                 LinkedHashMap<String, Boolean> sortFieldsToAsc,
                                                 String[] includeFields,
                                                 String[] excludeFields,
                                                 Object[] sortValues) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //返回和排除列_source
            if (!CollectionUtils.isEmpty(includeFields) || !CollectionUtils.isEmpty(excludeFields)) {
                sourceBuilder.fetchSource(includeFields, excludeFields);
            }
            //query criteria
            sourceBuilder.query(boolQueryBuilder);
            //return size
            sourceBuilder.size(size);
            sortFieldsToAsc.forEach((k, v) -> sourceBuilder.sort(new FieldSortBuilder(k).order(v ? SortOrder.ASC : SortOrder.DESC)));
            if (!ObjectUtils.isEmpty(sortValues)) {
                sourceBuilder.searchAfter(sortValues);
            }
            //折叠的字段
            if (collapseBuilder != null) {
                sourceBuilder.collapse(collapseBuilder);
            }

            SearchRequest rq = new SearchRequest();
            //索引
            rq.indices(index);
            rq.types(indexType);
            //各种组合条件
            rq.source(sourceBuilder);
            //请求
            LOGGER.debug("Search Query, dsl:{}", rq.source().toString());
            return client.search(rq, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 使用searchAfter进行深度查询(递归）
     *
     * @param index            　esIndex
     * @param indexType        　esIndexType
     * @param initPoint        　查询起始行数
     * @param size             　查询深度
     * @param boolQueryBuilder 　查询条件
     * @param collapseBuilder  　折叠显示
     * @param sortFieldsToAsc  　排序
     * @param includeFields    　显示字段
     * @param excludeFields    　不显示字段
     * @param sortValues       　当前sortValues，初次为null
     * @return 查询结果
     */
    private SearchResponse getSearchAfterResponse(String index,
                                                  String indexType,
                                                  long initPoint,
                                                  int size,
                                                  BoolQueryBuilder boolQueryBuilder,
                                                  CollapseBuilder collapseBuilder,
                                                  LinkedHashMap<String, Boolean> sortFieldsToAsc,
                                                  String[] includeFields,
                                                  String[] excludeFields,
                                                  Object[] sortValues) {
        //避免数据拥有同样的sortValues而导致过滤后的数据缺失
        if (!sortFieldsToAsc.containsKey(IdFieldMapper.NAME)) {
            sortFieldsToAsc.put(IdFieldMapper.NAME, true);
        }
        if (initPoint == 0) {
            //查询返回对象
            return getSearchAfterResponse(index, indexType, size,
                    boolQueryBuilder, collapseBuilder, sortFieldsToAsc,
                    includeFields, excludeFields, sortValues);
        } else {
            // 滚动获取sortValues
            Object[] nextSortValues = getSortValues(index, indexType,
                    new Long(Math.min(initPoint, (long) DEEP_SEARCH_INTERVAL)).intValue(),
                    boolQueryBuilder, sortFieldsToAsc, sortValues);
            if (ObjectUtils.isEmpty(nextSortValues)) {
                initPoint = 0L;
            } else {
                sortValues = nextSortValues;
                initPoint = initPoint - Math.min(initPoint, (long) DEEP_SEARCH_INTERVAL);
            }
            return getSearchAfterResponse(index, indexType, initPoint, size, boolQueryBuilder,
                    collapseBuilder, sortFieldsToAsc, includeFields, excludeFields, sortValues);
        }
    }

    public <T> PageRespDto<T> pageRespBySearchAfter(String index,
                                                    String indexType,
                                                    int from,
                                                    int size,
                                                    BoolQueryBuilder boolQueryBuilder,
                                                    LinkedHashMap<String, Boolean> sortFieldsToAsc,
                                                    Class<T> classOfT) {
        return pageRespBySearchAfter(index, indexType, from, size,
                boolQueryBuilder, null, sortFieldsToAsc,
                null, null, classOfT);
    }

    public <T> PageRespDto<T> pageRespBySearchAfter(String index,
                                                    String indexType,
                                                    int from,
                                                    int size,
                                                    BoolQueryBuilder boolQueryBuilder,
                                                    LinkedHashMap<String, Boolean> sortFieldsToAsc,
                                                    String[] includeFields,
                                                    String[] excludeFields,
                                                    Class<T> classOfT) {
        return pageRespBySearchAfter(index, indexType, from, size,
                boolQueryBuilder, null, sortFieldsToAsc,
                includeFields, excludeFields, classOfT);
    }

    public <T> PageRespDto<T> pageRespBySearchAfter(String index,
                                                    String indexType,
                                                    int from,
                                                    int size,
                                                    BoolQueryBuilder boolQueryBuilder,
                                                    CollapseBuilder collapseBuilder,
                                                    LinkedHashMap<String, Boolean> sortFieldsToAsc,
                                                    String[] includeFields,
                                                    String[] excludeFields,
                                                    Class<T> classOfT) {
        if (ObjectUtils.isEmpty(sortFieldsToAsc)) {
            throw new RuntimeException("use es search after must afferent sortFieldsToAsc");
        }
        long initPoint = (long) Math.max(0, from);
        size = (Math.max(0, size) == 0 ? 20 : Math.min(MAX_PAGE_SIZE, size));
        SearchResponse rp = getSearchAfterResponse(index, indexType, initPoint, size, boolQueryBuilder, collapseBuilder, sortFieldsToAsc, includeFields, excludeFields, null);
        return getPageRespDto(rp, classOfT);
    }

    public List<AggsRespDto> getSimpAggregation(String index, String indexType,
                                                BoolQueryBuilder boolQueryBuilder,
                                                AggregationBuilder aggregationBuilder) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.aggregation(aggregationBuilder);
            sourceBuilder.size(0);
            SearchRequest rq = new SearchRequest();
            //索引
            rq.indices(index);
            rq.types(indexType);
            //各种组合条件
            rq.source(sourceBuilder);
            //请求
            LOGGER.debug("Search Query, dsl:{}", rq.source().toString());
            SearchResponse sr = client.search(rq, RequestOptions.DEFAULT);
            return getAggsCount(sr.getAggregations().getAsMap(), null);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<AggsRespDto> getAggsCount(Map<String, Aggregation> aggregationsMap, List<AggsRespDto> resp) {
        List<AggsRespDto> finalResp = new ArrayList<>();
        if (!ObjectUtils.isEmpty(aggregationsMap)) {
            aggregationsMap.forEach((key, aggregation) -> {
                ((ParsedTerms) aggregation).getBuckets().forEach(bucket -> {
                    if (bucket.getAggregations().getAsMap().size() > 0) {
                        getAggsCount(bucket.getAggregations().getAsMap(), finalResp)
                                .forEach(aggsRespDto -> {
                                    aggsRespDto.getArrgCols().put(key, bucket.getKey());
                                    finalResp.add(aggsRespDto);
                                });
                    } else {
                        Map<String, Object> bottom = new HashMap<>();
                        bottom.put(key, bucket.getKey());
                        finalResp.add(AggsRespDto.builder().arrgCols(bottom).count(bucket.getDocCount()).build());
                    }
                });
            });
        }
        return finalResp;
    }

    private boolean responseInvalid(SearchResponse sp) {
        return sp.status() != RestStatus.OK || sp.getHits() == null || sp.getHits().getTotalHits() == null || sp.getHits().getTotalHits().value <= 0;
    }
}