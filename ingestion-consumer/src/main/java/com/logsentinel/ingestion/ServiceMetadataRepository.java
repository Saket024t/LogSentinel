package com.logsentinel.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServiceMetadataRepository extends JpaRepository<ServiceMetadata, Long> {
    Optional<ServiceMetadata> findByServiceName(String serviceName);
}
