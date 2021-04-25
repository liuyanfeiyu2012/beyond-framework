package org.lyfy.beyond.es.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author sujiani 2020/7/22 18:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AggsRespDto {

    private Map<String,Object> arrgCols;

    private long count;

}
