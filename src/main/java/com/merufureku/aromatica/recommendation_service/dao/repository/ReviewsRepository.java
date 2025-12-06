package com.merufureku.aromatica.recommendation_service.dao.repository;

import com.merufureku.aromatica.recommendation_service.dao.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long>, JpaSpecificationExecutor<Reviews> {

    boolean existsByUserIdAndFragranceId(Integer userId, Long fragranceId);

    Optional<Reviews> findByIdAndUserIdAndFragranceId(Long id, Integer userId, Long fragranceId);
}
