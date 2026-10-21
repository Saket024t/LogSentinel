package com.logsentinel.ingestion;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LogDocumentRepository extends ElasticsearchRepository<LogDocument, String> {
}
