package com.web_ii.tech_catalog.repository;

  import org.springframework.data.jpa.repository.JpaRepository; 
  import org.springframework.stereotype.Repository;  
  import com.web_ii.tech_catalog.models.TechCatalog;

  
  @Repository 
  public interface TechCatalogRepository extends JpaRepository<TechCatalog, Long>{
      
  }