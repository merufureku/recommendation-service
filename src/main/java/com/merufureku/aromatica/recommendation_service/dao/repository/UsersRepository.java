package com.merufureku.aromatica.recommendation_service.dao.repository;

import com.merufureku.aromatica.recommendation_service.dao.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {}
