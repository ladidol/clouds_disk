package com.feng.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ladidol
 * @date: 2022/9/29 20:18
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    private String type;
    @JsonProperty("name")
    private String fileName;
    private String link;
}
