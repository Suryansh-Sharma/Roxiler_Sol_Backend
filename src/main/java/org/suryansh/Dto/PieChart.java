package org.suryansh.Dto;

import lombok.Data;

import java.util.Map;
@Data
public class PieChart {
    private Map<String, Long> categoryCounts;
}
