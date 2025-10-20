package com.networkinsights.ai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.networkinsights.ai.model.TeamMember;

@Service
public class TeamCoordinationService {
    
    private final List<TeamMember> teamMembers;
    
    public TeamCoordinationService() {
        System.out.println("Initializing Team Coordination service...");
        this.teamMembers = initializeTeamMembers();
        System.out.println("Team Coordination service initialized with " + teamMembers.size() + " team members");
    }
    
    /**
     * Initialize mock team members data
     */
    private List<TeamMember> initializeTeamMembers() {
        List<TeamMember> members = new ArrayList<>();
        
        // Europe Team
        TeamMember anna = new TeamMember();
        anna.setId("1");
        anna.setName("Anna Schmidt");
        anna.setEmail("anna.schmidt@company.com");
        anna.setRole("Network Operations Manager");
        anna.setDepartment("IT Operations");
        anna.setLocation("Frankfurt, Germany");
        anna.setTimezone("CET (UTC+1)");
        anna.setExpertise("Network Infrastructure, Security, System Administration");
        anna.setLanguages("English, German, French");
        anna.setAvailability("Available");
        anna.setContactInfo(Map.of("Phone", "+49-69-123456", "Slack", "@anna.schmidt"));
        anna.setResponsibilities(Map.of("Network Issues", "Primary contact for Europe network problems", 
                                       "Security Incidents", "Lead security incident response"));
        members.add(anna);
        
        // Asia-Pacific Team
        TeamMember david = new TeamMember();
        david.setId("2");
        david.setName("David Chen");
        david.setEmail("david.chen@company.com");
        david.setRole("Logistics Coordinator");
        david.setDepartment("Operations");
        david.setLocation("Singapore");
        david.setTimezone("SGT (UTC+8)");
        david.setExpertise("Supply Chain, Port Operations, Regional Coordination");
        david.setLanguages("English, Mandarin, Malay");
        david.setAvailability("Available");
        david.setContactInfo(Map.of("Phone", "+65-6123-4567", "Slack", "@david.chen"));
        david.setResponsibilities(Map.of("Asia-Pacific Logistics", "Coordinate logistics across Asia-Pacific region", 
                                        "Port Operations", "Manage port relationships and operations"));
        members.add(david);
        
        // North America Team
        TeamMember sarah = new TeamMember();
        sarah.setId("3");
        sarah.setName("Sarah Johnson");
        sarah.setEmail("sarah.johnson@company.com");
        sarah.setRole("Technical Support Lead");
        sarah.setDepartment("Customer Support");
        sarah.setLocation("New York, USA");
        sarah.setTimezone("EST (UTC-5)");
        sarah.setExpertise("Technical Support, Customer Relations, System Troubleshooting");
        sarah.setLanguages("English, Spanish");
        sarah.setAvailability("Available");
        sarah.setContactInfo(Map.of("Phone", "+1-212-555-0123", "Slack", "@sarah.johnson"));
        sarah.setResponsibilities(Map.of("North America Support", "Lead technical support for North America", 
                                        "Customer Relations", "Manage customer relationships and escalations"));
        members.add(sarah);
        
        // Global Infrastructure Team
        TeamMember mike = new TeamMember();
        mike.setId("4");
        mike.setName("Mike Rodriguez");
        mike.setEmail("mike.rodriguez@company.com");
        mike.setRole("Infrastructure Manager");
        mike.setDepartment("IT Infrastructure");
        mike.setLocation("London, UK");
        mike.setTimezone("GMT (UTC+0)");
        mike.setExpertise("System Architecture, Cloud Infrastructure, DevOps");
        mike.setLanguages("English, Spanish, Portuguese");
        mike.setAvailability("Available");
        mike.setContactInfo(Map.of("Phone", "+44-20-7946-0958", "Slack", "@mike.rodriguez"));
        mike.setResponsibilities(Map.of("Infrastructure Maintenance", "Oversee global infrastructure maintenance", 
                                       "System Architecture", "Design and implement system architecture"));
        members.add(mike);
        
        // Finance Team
        TeamMember lisa = new TeamMember();
        lisa.setId("5");
        lisa.setName("Lisa Wang");
        lisa.setEmail("lisa.wang@company.com");
        lisa.setRole("Finance Director");
        lisa.setDepartment("Finance");
        lisa.setLocation("Hong Kong");
        lisa.setTimezone("HKT (UTC+8)");
        lisa.setExpertise("Financial Planning, Budget Management, Cost Analysis");
        lisa.setLanguages("English, Mandarin, Cantonese");
        lisa.setAvailability("Available");
        lisa.setContactInfo(Map.of("Phone", "+852-2234-5678", "Slack", "@lisa.wang"));
        lisa.setResponsibilities(Map.of("Financial Planning", "Lead financial planning and budgeting", 
                                      "Cost Optimization", "Analyze and optimize operational costs"));
        members.add(lisa);
        
        // Legal Team
        TeamMember james = new TeamMember();
        james.setId("6");
        james.setName("James Thompson");
        james.setEmail("james.thompson@company.com");
        james.setRole("Legal Counsel");
        james.setDepartment("Legal");
        james.setLocation("Sydney, Australia");
        james.setTimezone("AEST (UTC+10)");
        james.setExpertise("Contract Law, Compliance, Risk Management");
        james.setLanguages("English");
        james.setAvailability("Available");
        james.setContactInfo(Map.of("Phone", "+61-2-9876-5432", "Slack", "@james.thompson"));
        james.setResponsibilities(Map.of("Legal Matters", "Handle legal matters and compliance", 
                                        "Contract Management", "Manage contracts and legal documentation"));
        members.add(james);
        
        // Marketing Team
        TeamMember maria = new TeamMember();
        maria.setId("7");
        maria.setName("Maria Garcia");
        maria.setEmail("maria.garcia@company.com");
        maria.setRole("Marketing Manager");
        maria.setDepartment("Marketing");
        maria.setLocation("Madrid, Spain");
        maria.setTimezone("CET (UTC+1)");
        maria.setExpertise("Digital Marketing, Brand Management, Market Analysis");
        maria.setLanguages("English, Spanish, French, Italian");
        maria.setAvailability("Available");
        maria.setContactInfo(Map.of("Phone", "+34-91-123-4567", "Slack", "@maria.garcia"));
        maria.setResponsibilities(Map.of("Marketing Strategy", "Develop and execute marketing strategies", 
                                         "Brand Management", "Manage brand positioning and messaging"));
        members.add(maria);
        
        // Security Team
        TeamMember alex = new TeamMember();
        alex.setId("8");
        alex.setName("Alex Kim");
        alex.setEmail("alex.kim@company.com");
        alex.setRole("Security Specialist");
        alex.setDepartment("Information Security");
        alex.setLocation("Seoul, South Korea");
        alex.setTimezone("KST (UTC+9)");
        alex.setExpertise("Cybersecurity, Incident Response, Security Analysis");
        alex.setLanguages("English, Korean, Japanese");
        alex.setAvailability("Available");
        alex.setContactInfo(Map.of("Phone", "+82-2-1234-5678", "Slack", "@alex.kim"));
        alex.setResponsibilities(Map.of("Security Incidents", "Handle security incidents and threats", 
                                       "Security Analysis", "Analyze security risks and vulnerabilities"));
        members.add(alex);
        
        return members;
    }
    
    /**
     * Parse team coordination query to extract relevant information
     */
    public TeamCoordinationQuery parseQuery(String query) {
        TeamCoordinationQuery parsedQuery = new TeamCoordinationQuery();
        String lowerQuery = query.toLowerCase();
        
        // Extract region
        if (lowerQuery.contains("europe") || lowerQuery.contains("european")) {
            parsedQuery.setRegion("Europe");
        } else if (lowerQuery.contains("asia") || lowerQuery.contains("asian") || lowerQuery.contains("apac")) {
            parsedQuery.setRegion("Asia-Pacific");
        } else if (lowerQuery.contains("north america") || lowerQuery.contains("america") || lowerQuery.contains("usa") || lowerQuery.contains("us")) {
            parsedQuery.setRegion("North America");
        } else if (lowerQuery.contains("global") || lowerQuery.contains("worldwide")) {
            parsedQuery.setRegion("Global");
        }
        
        // Extract issue type
        if (lowerQuery.contains("network") || lowerQuery.contains("infrastructure")) {
            parsedQuery.setIssueType("Network/Infrastructure");
        } else if (lowerQuery.contains("logistics") || lowerQuery.contains("shipping") || lowerQuery.contains("cargo")) {
            parsedQuery.setIssueType("Logistics");
        } else if (lowerQuery.contains("support") || lowerQuery.contains("technical")) {
            parsedQuery.setIssueType("Technical Support");
        } else if (lowerQuery.contains("security") || lowerQuery.contains("incident")) {
            parsedQuery.setIssueType("Security");
        } else if (lowerQuery.contains("finance") || lowerQuery.contains("budget") || lowerQuery.contains("cost")) {
            parsedQuery.setIssueType("Finance");
        } else if (lowerQuery.contains("legal") || lowerQuery.contains("compliance")) {
            parsedQuery.setIssueType("Legal");
        } else if (lowerQuery.contains("marketing") || lowerQuery.contains("brand")) {
            parsedQuery.setIssueType("Marketing");
        }
        
        // Extract role level
        if (lowerQuery.contains("manager") || lowerQuery.contains("lead") || lowerQuery.contains("director")) {
            parsedQuery.setRoleLevel("Management");
        } else if (lowerQuery.contains("specialist") || lowerQuery.contains("coordinator")) {
            parsedQuery.setRoleLevel("Specialist");
        } else if (lowerQuery.contains("analyst") || lowerQuery.contains("assistant")) {
            parsedQuery.setRoleLevel("Analyst");
        }
        
        return parsedQuery;
    }
    
    /**
     * Find team members based on query criteria
     */
    public List<TeamMember> findTeamMembers(TeamCoordinationQuery query) {
        return teamMembers.stream()
                .filter(member -> matchesQuery(member, query))
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a team member matches the query criteria
     */
    private boolean matchesQuery(TeamMember member, TeamCoordinationQuery query) {
        boolean matches = true;
        
        // Check region match
        if (query.getRegion() != null && !query.getRegion().isEmpty()) {
            String memberLocation = member.getLocation().toLowerCase();
            String queryRegion = query.getRegion().toLowerCase();
            
            if (queryRegion.contains("europe")) {
                matches = matches && (memberLocation.contains("germany") || memberLocation.contains("uk") || 
                                    memberLocation.contains("spain") || memberLocation.contains("frankfurt") || 
                                    memberLocation.contains("london") || memberLocation.contains("madrid"));
            } else if (queryRegion.contains("asia")) {
                matches = matches && (memberLocation.contains("singapore") || memberLocation.contains("hong kong") || 
                                    memberLocation.contains("seoul") || memberLocation.contains("sydney"));
            } else if (queryRegion.contains("america")) {
                matches = matches && (memberLocation.contains("new york") || memberLocation.contains("usa"));
            }
        }
        
        // Check issue type match
        if (query.getIssueType() != null && !query.getIssueType().isEmpty()) {
            String issueType = query.getIssueType().toLowerCase();
            String expertise = member.getExpertise().toLowerCase();
            
            if (issueType.contains("network") || issueType.contains("infrastructure")) {
                matches = matches && (expertise.contains("network") || expertise.contains("infrastructure") || expertise.contains("system"));
            } else if (issueType.contains("logistics")) {
                matches = matches && (expertise.contains("logistics") || expertise.contains("supply") || expertise.contains("port"));
            } else if (issueType.contains("support")) {
                matches = matches && (expertise.contains("support") || expertise.contains("customer"));
            } else if (issueType.contains("security")) {
                matches = matches && (expertise.contains("security") || expertise.contains("cybersecurity"));
            } else if (issueType.contains("finance")) {
                matches = matches && (expertise.contains("financial") || expertise.contains("budget") || expertise.contains("cost"));
            } else if (issueType.contains("legal")) {
                matches = matches && (expertise.contains("legal") || expertise.contains("compliance") || expertise.contains("contract"));
            } else if (issueType.contains("marketing")) {
                matches = matches && (expertise.contains("marketing") || expertise.contains("brand"));
            }
        }
        
        // Check role level match
        if (query.getRoleLevel() != null && !query.getRoleLevel().isEmpty()) {
            String roleLevel = query.getRoleLevel().toLowerCase();
            String memberRole = member.getRole().toLowerCase();
            
            if (roleLevel.contains("management")) {
                matches = matches && (memberRole.contains("manager") || memberRole.contains("lead") || 
                                    memberRole.contains("director"));
            } else if (roleLevel.contains("specialist")) {
                matches = matches && (memberRole.contains("specialist") || memberRole.contains("coordinator"));
            } else if (roleLevel.contains("analyst")) {
                matches = matches && (memberRole.contains("analyst") || memberRole.contains("assistant"));
            }
        }
        
        return matches;
    }
    
    /**
     * Generate team coordination response
     */
    public String generateTeamCoordinationResponse(TeamCoordinationQuery query, List<TeamMember> teamMembers, String language) {
        if (teamMembers.isEmpty()) {
            return language.equals("zh") ? 
                "Sorry, no relevant team members found. Please try different search criteria." :
                "Sorry, no relevant team members found. Please try different search criteria.";
        }
        
        StringBuilder response = new StringBuilder();
        
        if (language.equals("zh")) {
            response.append("👥 Team Coordination Information:\n\n");
        } else {
            response.append("👥 Team Coordination Information:\n\n");
        }
        
        for (TeamMember member : teamMembers) {
            response.append(generateMemberDetails(member, language));
            response.append("\n");
        }
        
        return response.toString();
    }
    
    /**
     * Generate detailed information for a team member
     */
    private String generateMemberDetails(TeamMember member, String language) {
        StringBuilder details = new StringBuilder();
        
        if (language.equals("zh")) {
            details.append("👤 Name: ").append(member.getName()).append("\n");
            details.append("📧 Email: ").append(member.getEmail()).append("\n");
            details.append("💼 Role: ").append(member.getRole()).append("\n");
            details.append("🏢 Department: ").append(member.getDepartment()).append("\n");
            details.append("📍 Location: ").append(member.getLocation()).append("\n");
            details.append("🕐 Timezone: ").append(member.getTimezone()).append("\n");
            details.append("🎯 Expertise: ").append(member.getExpertise()).append("\n");
            details.append("🗣️ Languages: ").append(member.getLanguages()).append("\n");
            details.append("📊 Status: ").append(member.getAvailability()).append("\n");
            
            if (!member.getContactInfo().isEmpty()) {
                details.append("📞 Contact Information:\n");
                member.getContactInfo().forEach((key, value) -> 
                    details.append("   • ").append(key).append(": ").append(value).append("\n"));
            }
            
            if (!member.getResponsibilities().isEmpty()) {
                details.append("📋 Responsibilities:\n");
                member.getResponsibilities().forEach((key, value) -> 
                    details.append("   • ").append(key).append(": ").append(value).append("\n"));
            }
        } else {
            details.append("👤 Name: ").append(member.getName()).append("\n");
            details.append("📧 Email: ").append(member.getEmail()).append("\n");
            details.append("💼 Role: ").append(member.getRole()).append("\n");
            details.append("🏢 Department: ").append(member.getDepartment()).append("\n");
            details.append("📍 Location: ").append(member.getLocation()).append("\n");
            details.append("🕐 Timezone: ").append(member.getTimezone()).append("\n");
            details.append("🎯 Expertise: ").append(member.getExpertise()).append("\n");
            details.append("🗣️ Languages: ").append(member.getLanguages()).append("\n");
            details.append("📊 Status: ").append(member.getAvailability()).append("\n");
            
            if (!member.getContactInfo().isEmpty()) {
                details.append("📞 Contact Information:\n");
                member.getContactInfo().forEach((key, value) -> 
                    details.append("   • ").append(key).append(": ").append(value).append("\n"));
            }
            
            if (!member.getResponsibilities().isEmpty()) {
                details.append("📋 Responsibilities:\n");
                member.getResponsibilities().forEach((key, value) -> 
                    details.append("   • ").append(key).append(": ").append(value).append("\n"));
            }
        }
        
        return details.toString();
    }
    
    /**
     * Get all team members
     */
    public List<TeamMember> getAllTeamMembers() {
        return new ArrayList<>(teamMembers);
    }
    
    /**
     * Get team member by ID
     */
    public Optional<TeamMember> getTeamMemberById(String id) {
        return teamMembers.stream()
                .filter(member -> member.getId().equals(id))
                .findFirst();
    }
    
    /**
     * Search team members by keywords
     */
    public List<TeamMember> searchTeamMembers(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            return getAllTeamMembers();
        }
        
        String lowerKeywords = keywords.toLowerCase();
        return teamMembers.stream()
                .filter(member -> 
                    member.getName().toLowerCase().contains(lowerKeywords) ||
                    member.getRole().toLowerCase().contains(lowerKeywords) ||
                    member.getDepartment().toLowerCase().contains(lowerKeywords) ||
                    member.getLocation().toLowerCase().contains(lowerKeywords) ||
                    member.getExpertise().toLowerCase().contains(lowerKeywords)
                )
                .collect(Collectors.toList());
    }
    
    /**
     * Inner class for team coordination query
     */
    public static class TeamCoordinationQuery {
        private String region;
        private String issueType;
        private String roleLevel;
        
        public TeamCoordinationQuery() {}
        
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public String getIssueType() {
            return issueType;
        }
        
        public void setIssueType(String issueType) {
            this.issueType = issueType;
        }
        
        public String getRoleLevel() {
            return roleLevel;
        }
        
        public void setRoleLevel(String roleLevel) {
            this.roleLevel = roleLevel;
        }
    }
}
