package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.config.ConfigSitesList;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexing.IndexingService;
import searchengine.services.statictics.StatisticsService;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private final StatisticsService statisticsService;

    @Autowired
    private IndexingService indexingService;

    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity startIndexing() {

        indexingService.startIndexing();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity stopIndexing() {
        indexingService.stopIndexing();
        return ResponseEntity.noContent().build();
    }
}
