package com.networkinsights.ai.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.networkinsights.ai.model.ChatMessage;
import com.networkinsights.ai.model.TeamMember;
import com.networkinsights.ai.service.TeamCoordinationService;

/**
 * Team Coordination Controller
 * Handles team coordination related API requests
 */
@RestController
@RequestMapping("/api/team-coordination")
@CrossOrigin(origins = "*")
public class TeamCoordinationController {

    private static final Logger logger = LoggerFactory.getLogger(TeamCoordinationController.class);

    @Autowired
    private TeamCoordinationService teamCoordinationService;

    /**
     * Process team coordination query
     * Example: "Who owns Europe network issues?"
     */
    @PostMapping("/query")
    public ResponseEntity<ChatMessage> processTeamCoordinationQuery(@RequestBody Map<String, String> request) {
        try {
            logger.info("Received request: {}", request);
            
            // Support both "query" and "message" field names for flexibility
            String query = request.get("message");
            if (query == null || query.trim().isEmpty()) {
                query = request.get("query");
            }
            
            String language = request.getOrDefault("language", "en");
            
            logger.info("Extracted query: '{}', language: '{}'", query, language);

            if (query == null || query.trim().isEmpty()) {
                logger.warn("Query is null or empty. Request was: {}", request);
                return ResponseEntity.badRequest().body(createErrorMessage("Query cannot be empty", language));
            }

            logger.info("Team Coordination query: {}", query);

            // Parse the query
            TeamCoordinationService.TeamCoordinationQuery parsedQuery = teamCoordinationService.parseQuery(query);
            logger.info("Parsed query: {}", parsedQuery);

            // Find team members
            List<TeamMember> teamMembers = teamCoordinationService.findTeamMembers(parsedQuery);
            logger.info("Found {} team members", teamMembers.size());

            // Generate response
            String response = teamCoordinationService.generateTeamCoordinationResponse(parsedQuery, teamMembers, language);
            logger.info("Generated response: {}", response);

            // Create chat message
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(java.util.UUID.randomUUID().toString());
            chatMessage.setContent(response);
            chatMessage.setSender("ai");
            chatMessage.setTimestamp(java.time.LocalDateTime.now());
            chatMessage.setLanguage(language);
            chatMessage.setOriginalContent("Team Coordination Query Result");

            return ResponseEntity.ok(chatMessage);

        } catch (Exception e) {
            logger.error("Team coordination query processing failed: {}", e.getMessage(), e);
            String language = request.getOrDefault("language", "en");
            return ResponseEntity.ok(createErrorMessage("Team coordination query processing failed. Please try again.", language));
        }
    }

    /**
     * Get all team members
     */
    @GetMapping("/members")
    public ResponseEntity<List<TeamMember>> getAllTeamMembers() {
        try {
            List<TeamMember> teamMembers = teamCoordinationService.getAllTeamMembers();
            return ResponseEntity.ok(teamMembers);
        } catch (Exception e) {
            logger.error("Failed to get team members: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Get team member by ID
     */
    @GetMapping("/members/{memberId}")
    public ResponseEntity<TeamMember> getTeamMemberById(@PathVariable String memberId) {
        try {
            return teamCoordinationService.getTeamMemberById(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Failed to get team member by ID {}: {}", memberId, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search team members by criteria
     */
    @PostMapping("/search")
    public ResponseEntity<List<TeamMember>> searchTeamMembers(@RequestBody Map<String, Object> searchCriteria) {
        try {
            // Create a mock query from search criteria
            TeamCoordinationService.TeamCoordinationQuery query = new TeamCoordinationService.TeamCoordinationQuery();
            
            if (searchCriteria.containsKey("region")) {
                query.setRegion((String) searchCriteria.get("region"));
            }
            if (searchCriteria.containsKey("issueType")) {
                query.setIssueType((String) searchCriteria.get("issueType"));
            }
            if (searchCriteria.containsKey("roleLevel")) {
                query.setRoleLevel((String) searchCriteria.get("roleLevel"));
            }

            List<TeamMember> teamMembers = teamCoordinationService.findTeamMembers(query);
            return ResponseEntity.ok(teamMembers);

        } catch (Exception e) {
            logger.error("Failed to search team members: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Get team coordination service status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getServiceStatus() {
        try {
            Map<String, Object> status = Map.of(
                "service", "Team Coordination",
                "status", "Available",
                "totalMembers", teamCoordinationService.getAllTeamMembers().size(),
                "features", List.of("Member Search", "Query Processing", "Contact Information", "Responsibility Mapping")
            );
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Failed to get service status: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("service", "Team Coordination", "status", "Error"));
        }
    }

    /**
     * Get example queries
     */
    @GetMapping("/examples")
    public ResponseEntity<Map<String, Object>> getExampleQueries() {
        try {
            Map<String, Object> examples = Map.of(
                "examples", List.of(
                    "Who owns Europe network issues?",
                    "Who is responsible for Asia logistics?",
                    "Who manages North America support?",
                    "Who handles security incidents?",
                    "Who leads the finance team?",
                    "Who coordinates legal matters?",
                    "Who owns marketing strategy?",
                    "Who manages infrastructure maintenance?"
                ),
                "categories", Map.of(
                    "ownership", "Find who owns specific issues or responsibilities",
                    "contact", "Get contact information for team members",
                    "team", "Find team members by role or department",
                    "coordination", "Connect with the right people globally"
                )
            );
            return ResponseEntity.ok(examples);
        } catch (Exception e) {
            logger.error("Failed to get example queries: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("error", "Failed to get examples"));
        }
    }

    /**
     * Create error message
     */
    private ChatMessage createErrorMessage(String message, String language) {
        ChatMessage errorMessage = new ChatMessage();
        errorMessage.setId(java.util.UUID.randomUUID().toString());
        errorMessage.setContent(message);
        errorMessage.setSender("ai");
        errorMessage.setTimestamp(java.time.LocalDateTime.now());
        errorMessage.setLanguage(language);
        return errorMessage;
    }
}
