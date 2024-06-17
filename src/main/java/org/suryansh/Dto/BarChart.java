package org.suryansh.Dto;

import lombok.Data;

import java.util.Map;

@Data
public class BarChart {
    private Map<String, Long> priceRanges;
}
