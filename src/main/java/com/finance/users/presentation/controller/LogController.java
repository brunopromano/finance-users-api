package com.finance.users.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/logs")
@Tag(name = "Logs", description = "Application log access")
public class LogController {

    private static final DateTimeFormatter LOG_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter PARAM_FORMAT   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path logFilePath;

    public LogController(@Value("${logging.file.path:logs/requests.log}") String logFilePath) {
        this.logFilePath = Paths.get(logFilePath).normalize().toAbsolutePath();
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get application log content",
               description = "Returns the last N lines. Optionally filter by date/time range using 'from' and 'to' (format: yyyy-MM-dd HH:mm:ss).")
    @ApiResponse(responseCode = "200", description = "Log content returned")
    @ApiResponse(responseCode = "400", description = "Invalid date/time format")
    @ApiResponse(responseCode = "404", description = "Log file not found")
    public ResponseEntity<String> getLogs(
            @Parameter(description = "Number of lines to return (ignored when 'from'/'to' are set)", example = "100")
            @RequestParam(name = "tail", defaultValue = "100") int tail,
            @Parameter(description = "Start of filter range (yyyy-MM-dd HH:mm:ss)", example = "2026-04-01 08:00:00")
            @RequestParam(name = "from", required = false) String from,
            @Parameter(description = "End of filter range (yyyy-MM-dd HH:mm:ss)", example = "2026-04-01 09:00:00")
            @RequestParam(name = "to", required = false) String to) throws IOException {

        if (!Files.exists(logFilePath)) {
            return ResponseEntity.notFound().build();
        }

        List<String> allLines = Files.readAllLines(logFilePath);

        if (from != null || to != null) {
            try {
                LocalDateTime fromDt = from != null ? LocalDateTime.parse(from, PARAM_FORMAT) : LocalDateTime.MIN;
                LocalDateTime toDt   = to   != null ? LocalDateTime.parse(to,   PARAM_FORMAT) : LocalDateTime.MAX;

                String content = allLines.stream()
                        .filter(line -> {
                            if (line.length() < 23) return false;
                            try {
                                LocalDateTime ts = LocalDateTime.parse(line.substring(0, 23), LOG_TIMESTAMP);
                                return !ts.isBefore(fromDt) && !ts.isAfter(toDt);
                            } catch (DateTimeParseException e) {
                                return false;
                            }
                        })
                        .collect(Collectors.joining("\n"));

                return ResponseEntity.ok(content);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Invalid date format. Use: yyyy-MM-dd HH:mm:ss");
            }
        }

        int lines = Math.min(Math.max(tail, 1), 10_000);
        int fromIndex = Math.max(0, allLines.size() - lines);
        String content = String.join("\n", allLines.subList(fromIndex, allLines.size()));

        return ResponseEntity.ok(content);
    }
}
