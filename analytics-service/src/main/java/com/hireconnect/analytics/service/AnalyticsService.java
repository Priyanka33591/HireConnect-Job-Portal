package com.hireconnect.analytics.service;

import com.hireconnect.analytics.api.dto.AdminAnalyticsResponse;
import com.hireconnect.analytics.api.dto.RecruiterAnalyticsResponse;
import com.hireconnect.analytics.service.dto.ApplicationDto;
import com.hireconnect.analytics.service.dto.JobDto;
import com.hireconnect.analytics.service.dto.InvoiceDto;
import com.hireconnect.analytics.service.dto.UserCredentialDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final DownstreamProperties props;

    public RecruiterAnalyticsResponse recruiterAnalytics(Long recruiterId, String bearerHeader) {
        List<JobDto> allJobs = jobClient(bearerHeader).get()
                .uri("/jobs")
                .retrieve()
                .body(new ParameterizedTypeReference<List<JobDto>>() {});
        if (allJobs == null) allJobs = List.of();

        List<JobDto> recruiterJobs = allJobs.stream().filter(j -> Objects.equals(j.getPostedBy(), recruiterId)).toList();
        Map<String, Long> byStatus = new HashMap<>();
        long totalApps = 0;

        for (JobDto j : recruiterJobs) {
            List<ApplicationDto> apps = applicationClient(bearerHeader).get()
                    .uri("/applications/job/{jobId}", j.getId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ApplicationDto>>() {});
            if (apps == null) apps = List.of();
            totalApps += apps.size();
            for (ApplicationDto a : apps) {
                String st = a.getStatus() == null ? "UNKNOWN" : a.getStatus();
                byStatus.merge(st, 1L, Long::sum);
            }
        }

        return RecruiterAnalyticsResponse.builder()
                .recruiterId(recruiterId)
                .jobsPosted(recruiterJobs.size())
                .totalApplications(totalApps)
                .applicationsByStatus(sortMap(byStatus))
                .build();
    }

    public AdminAnalyticsResponse adminAnalytics(String bearerHeader) {
        List<JobDto> allJobs = List.of();
        try {
            allJobs = jobClient(bearerHeader).get()
                .uri("/jobs")
                .retrieve()
                .body(new ParameterizedTypeReference<List<JobDto>>() {});
            if (allJobs == null) allJobs = List.of();
        } catch (Exception e) { /* Service down */ }

        List<ApplicationDto> allApps = List.of();
        try {
            allApps = applicationClient(bearerHeader).get()
                .uri("/applications")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ApplicationDto>>() {});
            if (allApps == null) allApps = List.of();
        } catch (Exception e) { /* Service down */ }

        List<InvoiceDto> allInvoices = List.of();
        try {
            allInvoices = subscriptionClient(bearerHeader).get()
                .uri("/invoices/admin/all")
                .retrieve()
                .body(new ParameterizedTypeReference<List<InvoiceDto>>() {});
            if (allInvoices == null) allInvoices = List.of();
        } catch (Exception e) { /* Service down */ }

        List<UserCredentialDto> allUsers = List.of();
        try {
            allUsers = authClient(bearerHeader).get()
                .uri("/auth/users")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserCredentialDto>>() {});
            if (allUsers == null) allUsers = List.of();
        } catch (Exception e) { /* Service down */ }

        Map<String, Long> byStatus = new HashMap<>();
        long hiredCount = 0;
        for (ApplicationDto a : allApps) {
            String st = a.getStatus() == null ? "UNKNOWN" : a.getStatus();
            byStatus.merge(st, 1L, Long::sum);
            if ("HIRED".equalsIgnoreCase(st)) hiredCount++;
        }

        double conversion = allApps.isEmpty() ? 0 : (double) hiredCount / allApps.size() * 100;
        double revenue = allInvoices.stream().mapToDouble(i -> i.getAmountCents() / 100.0).sum();

        // Growth: New users + jobs in last 7 days vs previous 7 days
        java.time.Instant now = java.time.Instant.now();
        java.time.Instant weekAgo = now.minus(7, java.time.temporal.ChronoUnit.DAYS);
        java.time.Instant twoWeeksAgo = now.minus(14, java.time.temporal.ChronoUnit.DAYS);

        long recent = allUsers.stream().filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(weekAgo)).count() +
                      allJobs.stream().filter(j -> j.getPostedAt() != null && j.getPostedAt().isAfter(weekAgo)).count();
        long previous = allUsers.stream().filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(twoWeeksAgo) && u.getCreatedAt().isBefore(weekAgo)).count() +
                        allJobs.stream().filter(j -> j.getPostedAt() != null && j.getPostedAt().isAfter(twoWeeksAgo) && j.getPostedAt().isBefore(weekAgo)).count();

        double growth = previous == 0 ? (recent > 0 ? 100 : 0) : (double) (recent - previous) / previous * 100;

        return AdminAnalyticsResponse.builder()
                .totalJobs(allJobs.size())
                .totalApplications(allApps.size())
                .applicationsByStatus(sortMap(byStatus))
                .conversionRate(Math.round(conversion * 10) / 10.0)
                .totalRevenue(revenue)
                .growthRate(Math.round(growth * 10) / 10.0)
                .build();
    }

    public List<com.hireconnect.analytics.api.dto.RecruiterStatDto> recruiterStats(String bearerHeader) {
        List<JobDto> allJobs = jobClient(bearerHeader).get()
                .uri("/jobs")
                .retrieve()
                .body(new ParameterizedTypeReference<List<JobDto>>() {});
        if (allJobs == null) allJobs = List.of();

        Map<Long, com.hireconnect.analytics.api.dto.RecruiterStatDto.RecruiterStatDtoBuilder> stats = new HashMap<>();
        for (JobDto j : allJobs) {
            Long rid = j.getPostedBy();
            if (rid == null) continue;
            stats.putIfAbsent(rid, com.hireconnect.analytics.api.dto.RecruiterStatDto.builder().recruiterId(rid).totalJobs(0).activeJobs(0));
            var builder = stats.get(rid);
            builder.totalJobs(builder.build().getTotalJobs() + 1);
            if ("OPEN".equalsIgnoreCase(j.getStatus())) {
                builder.activeJobs(builder.build().getActiveJobs() + 1);
            }
        }
        return stats.values().stream().map(com.hireconnect.analytics.api.dto.RecruiterStatDto.RecruiterStatDtoBuilder::build).toList();
    }

    public List<Map<String, Object>> checkSystemHealth(String bearerHeader) {
        List<Map<String, String>> serviceDefinitions = List.of(
            Map.of("name", "API Gateway", "eurekaName", "API-GATEWAY", "url", "http://localhost:8080"),
            Map.of("name", "Auth Service", "eurekaName", "AUTH-SERVICE", "url", "http://localhost:8081"),
            Map.of("name", "Profile Service", "eurekaName", "PROFILE-SERVICE", "url", "http://localhost:8082"),
            Map.of("name", "Job Service", "eurekaName", "JOB-SERVICE", "url", "http://localhost:8083"),
            Map.of("name", "Application Service", "eurekaName", "APPLICATION-SERVICE", "url", "http://localhost:8084"),
            Map.of("name", "Interview Service", "eurekaName", "INTERVIEW-SERVICE", "url", "http://localhost:8085"),
            Map.of("name", "Notification Service", "eurekaName", "NOTIFICATION-SERVICE", "url", "http://localhost:8086"),
            Map.of("name", "Subscription Service", "eurekaName", "SUBSCRIPTION-SERVICE", "url", "http://localhost:8087"),
            Map.of("name", "Analytics Service", "eurekaName", "ANALYTICS-SERVICE", "url", "http://localhost:8088")
        );

        Set<String> upServices = new HashSet<>();
        try {
            // Fetch from Eureka
            Map<String, Object> eurekaData = RestClient.builder()
                .baseUrl("http://localhost:8761/eureka/apps")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build().get().retrieve().body(new ParameterizedTypeReference<Map<String, Object>>() {});
            
            if (eurekaData != null && eurekaData.containsKey("applications")) {
                Map<String, Object> apps = (Map<String, Object>) eurekaData.get("applications");
                List<Map<String, Object>> appList = (List<Map<String, Object>>) apps.get("application");
                if (appList != null) {
                    for (var app : appList) {
                        String name = (String) app.get("name");
                        upServices.add(name.toUpperCase());
                    }
                }
            }
        } catch (Exception e) {
            // Eureka might be down
        }

        List<Map<String, Object>> out = new ArrayList<>();
        for (var s : serviceDefinitions) {
            boolean online = upServices.contains(s.get("eurekaName").toUpperCase());
            // Double check via root ping if not in eureka yet (eureka has delay)
            if (!online) {
                try {
                    int code = RestClient.builder().baseUrl(s.get("url")).build().get().retrieve().toBodilessEntity().getStatusCode().value();
                    online = true; 
                } catch (Exception e) {
                    online = false;
                }
            }
            out.add(Map.of("name", s.get("name"), "status", online ? "ONLINE" : "OFFLINE"));
        }

        // Special case: Eureka Server itself
        boolean eurekaOnline = false;
        try {
            RestClient.builder().baseUrl("http://localhost:8761").build().get().retrieve().toBodilessEntity();
            eurekaOnline = true;
        } catch (Exception e) { eurekaOnline = false; }
        out.add(Map.of("name", "Eureka Server", "status", eurekaOnline ? "ONLINE" : "OFFLINE"));

        return out;
    }

    public List<com.hireconnect.analytics.api.dto.CandidateStatDto> candidateStats(String bearerHeader) {
        List<ApplicationDto> allApps = applicationClient(bearerHeader).get()
                .uri("/applications")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ApplicationDto>>() {});
        if (allApps == null) allApps = List.of();

        Map<Long, Integer> counts = new HashMap<>();
        for (ApplicationDto a : allApps) {
            Long cid = a.getCandidateId();
            if (cid == null) continue;
            counts.merge(cid, 1, Integer::sum);
        }
        return counts.entrySet().stream()
                .map(e -> com.hireconnect.analytics.api.dto.CandidateStatDto.builder()
                        .candidateId(e.getKey())
                        .totalApplications(e.getValue())
                        .build())
                .toList();
    }

    private RestClient jobClient(String bearerHeader) {
        return RestClient.builder()
                .baseUrl(props.getJob().getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, bearerHeader)
                .build();
    }

    private RestClient applicationClient(String bearerHeader) {
        return RestClient.builder()
                .baseUrl(props.getApplication().getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, bearerHeader)
                .build();
    }

    private RestClient subscriptionClient(String bearerHeader) {
        return RestClient.builder()
                .baseUrl(props.getSubscription().getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, bearerHeader)
                .build();
    }

    private RestClient authClient(String bearerHeader) {
        return RestClient.builder()
                .baseUrl(props.getAuth().getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, bearerHeader)
                .build();
    }

    private Map<String, Long> sortMap(Map<String, Long> input) {
        return input.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
    }
}

