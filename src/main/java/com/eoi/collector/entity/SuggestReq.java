package com.eoi.collector.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author lemon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestReq {
    @ApiModelProperty(value = "opentsdb的地址", example = "http://localhost:4242")
    @NotNull
    private String apiUrl;
    @ApiModelProperty(value = "类型", example = "metrics")
    private String type;
    @ApiModelProperty(value = "过滤的条件", example = "tcollector.")
    private String q;
}
