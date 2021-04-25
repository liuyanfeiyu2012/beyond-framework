package org.lyfy.beyond.ratelimiter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RateLimitEntity {

    private String key;

    private int limitNum;

    private int seconds;
}
