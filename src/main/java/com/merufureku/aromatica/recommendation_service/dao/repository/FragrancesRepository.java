package com.merufureku.aromatica.recommendation_service.dao.repository;

import com.merufureku.aromatica.recommendation_service.dao.entity.Fragrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FragrancesRepository extends JpaRepository<Fragrance, Long> {}
