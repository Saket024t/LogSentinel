package com.logsentinel.ingestion;

import jakarta.persistence.*;

@Entity
@Table(name = "services_metadata")
public class ServiceMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", unique = true, nullable = false)
    private String serviceName;

    @Column(name = "team_owner", nullable = false)
    private String teamOwner;

    @Column(nullable = false)
    private String criticality;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getTeamOwner() { return teamOwner; }
    public void setTeamOwner(String teamOwner) { this.teamOwner = teamOwner; }
    public String getCriticality() { return criticality; }
    public void setCriticality(String criticality) { this.criticality = criticality; }
}
