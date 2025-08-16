package org.ubb.ticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ubb.ticketing.domain.SolutionType;

import java.util.List;

@Repository
public interface SolutionTypeRepository extends JpaRepository<SolutionType, Long> {

    List<SolutionType> findByName(String name);
}
