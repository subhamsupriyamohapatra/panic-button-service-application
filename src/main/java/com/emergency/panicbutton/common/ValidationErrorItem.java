package com.emergency.panicbutton.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * FastAPI-compatible validation error item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"loc", "msg", "type", "input", "ctx"})
public class ValidationErrorItem {

    private List<Object> loc;
    private String msg;
    private String type;
    private Object input;
    private Map<String, Object> ctx;
}
