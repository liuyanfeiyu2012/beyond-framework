package org.lyfy.beyond.common.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryReq {

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
