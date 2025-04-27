package com.web_ii.tech_catalog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.web_ii.tech_catalog.models.TechCatalog;
import com.web_ii.tech_catalog.repository.TechCatalogRepository;

    @Service
    public class TechCatalogServiceImpl implements TechCatalogService {
    
        @Autowired
        private TechCatalogRepository techCatalogRepository;
    
        @Override
        public List <TechCatalog> getAllTechCatalog(){
            return techCatalogRepository.findAll();
        }
    
        @Override
        public void saveTechCatalog(TechCatalog techCatalog){
            this.techCatalogRepository.save(techCatalog);
        }
    
        @Override
        public TechCatalog getTechCatalogById(long id) {
            Optional < TechCatalog > optional = techCatalogRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            } else {
                throw new RuntimeException("Tech Catalog not found with id: " + id);
            }
        }
    
        @Override
        public void deleteTechCatalogById(long id) {
            this.techCatalogRepository.deleteById(id);
        }
    
    }