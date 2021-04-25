package org.lyfy.beyond.distributed.lock.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 谢星星
 * @Date: 2019/9/16 11:17
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = "beyond.distributed.lock", ignoreInvalidFields = true)
public class LockProperties {

    private boolean enable = Boolean.TRUE;
}
