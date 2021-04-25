package org.lyfy.beyond.common.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryResp<T> {

    /**
     * 总数
     */
    private Long totalQty = 0L;

    /**
     * 数据集合
     */
    private List<T> itemList = new ArrayList<>();

    @Deprecated
    public static final PageQueryResp EMPTY_PAGE = new PageQueryResp<>(0L, new ArrayList<>());

    public static PageQueryResp EMPTY() {
        return new PageQueryResp<>(0L, new ArrayList<>());
    }
}
