package org.suryansh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.suryansh.Dto.BarChart;
import org.suryansh.Dto.PieChart;
import org.suryansh.Dto.Statistics;
import org.suryansh.entity.Product;
import org.suryansh.repository.ProductRepo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String API_URL = "https://s3.amazonaws.com/roxiler.com/product_transaction.json";
    private final ProductRepo productRepo;
    public String initializeProducts() {
        try {
            productRepo.saveAll(fetchTransactions());
            return "Products Initialized";
        }catch (Exception e){
            e.printStackTrace();
            return "Error initializing products";
        }
    }
    public List<Product> listTransactions(int month, int page, int size, String search) {
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Pageable pageable = PageRequest.of(page - 1, size);

        if (StringUtils.hasText(search)) {
            Page<Product> searchResults = productRepo.findByDateOfSaleBetweenAndTitleContainingOrDescriptionContainingOrPriceContaining(
                    startDate, endDate, search, search, search, pageable);
            return searchResults.getContent();
        } else {
            Page<Product> transactions = productRepo.findByDateOfSaleBetween(startDate, endDate, pageable);
            return transactions.getContent();
        }
    }
    public Statistics getStatisticsForMonth(int month) {
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Page<Product> transactions = productRepo.findByDateOfSaleBetween(startDate, endDate,Pageable.unpaged());

        double totalSaleAmount = transactions.stream()
                .filter(Product::isSold)
                .mapToDouble(Product::getPrice)
                .sum();
        long totalSoldItems = transactions.stream()
                .filter(Product::isSold)
                .count();
        long totalNotSoldItems = transactions.stream()
                .filter(t -> !t.isSold())
                .count();

        Statistics stats = new Statistics();
        stats.setTotalSaleAmount(totalSaleAmount);
        stats.setTotalSoldItems(totalSoldItems);
        stats.setTotalNotSoldItems(totalNotSoldItems);
        return stats;
    }

    public PieChart getPieChartForMonth(int month) {
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Page<Product> transactions = productRepo.findByDateOfSaleBetween(startDate, endDate,Pageable.unpaged());


        Map<String, Long> categoryCounts = transactions.stream()
                .collect(Collectors.groupingBy(
                        Product::getTitle,
                        Collectors.counting()
                ));

        PieChart pieChart = new PieChart();
        pieChart.setCategoryCounts(categoryCounts);
        return pieChart;
    }

    private List<Product> fetchTransactions() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(API_URL, String.class);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<>() {});
    }

    public BarChart getBarChartForMonth(int month) {
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Page<Product> transactions = productRepo.findByDateOfSaleBetween(startDate, endDate,Pageable.unpaged());

        Map<String, Long> priceRanges = new HashMap<>();
        priceRanges.put("0-100", transactions.stream().filter(t -> t.getPrice() >= 0 && t.getPrice() <= 100).count());
        priceRanges.put("101-200", transactions.stream().filter(t -> t.getPrice() > 100 && t.getPrice() <= 200).count());
        priceRanges.put("201-300", transactions.stream().filter(t -> t.getPrice() > 200 && t.getPrice() <= 300).count());
        priceRanges.put("301-400", transactions.stream().filter(t -> t.getPrice() > 300 && t.getPrice() <= 400).count());
        priceRanges.put("401-500", transactions.stream().filter(t -> t.getPrice() > 400 && t.getPrice() <= 500).count());
        priceRanges.put("501-600", transactions.stream().filter(t -> t.getPrice() > 500 && t.getPrice() <= 600).count());
        priceRanges.put("601-700", transactions.stream().filter(t -> t.getPrice() > 600 && t.getPrice() <= 700).count());
        priceRanges.put("701-800", transactions.stream().filter(t -> t.getPrice() > 700 && t.getPrice() <= 800).count());
        priceRanges.put("801-900", transactions.stream().filter(t -> t.getPrice() > 800 && t.getPrice() <= 900).count());
        priceRanges.put("901-above", transactions.stream().filter(t -> t.getPrice() > 900).count());

        BarChart barChart = new BarChart();
        barChart.setPriceRanges(priceRanges);
        return barChart;
    }
}
