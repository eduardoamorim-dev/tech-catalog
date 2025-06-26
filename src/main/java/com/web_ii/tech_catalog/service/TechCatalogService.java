package com.web_ii.tech_catalog.service;

import java.util.List;
    
import com.web_ii.tech_catalog.models.TechCatalog;
    
public interface TechCatalogService {
    List <TechCatalog> getAllTechCatalog();
    void saveTechCatalog(TechCatalog techcatalog);
    TechCatalog getTechCatalogById(long id);
    void deleteTechCatalogById(long id);
}  