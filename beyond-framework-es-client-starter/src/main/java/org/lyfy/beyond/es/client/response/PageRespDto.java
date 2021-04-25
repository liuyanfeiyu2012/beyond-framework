package org.lyfy.beyond.es.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sujiani 2020/4/27 17:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageRespDto<T> {

    /**
     * 总数量
     */
    private long totalCount;

    /**
     * 结果集合
     */
    private List<T> resultList;
}
