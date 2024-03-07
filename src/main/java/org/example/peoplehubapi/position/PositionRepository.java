package org.example.peoplehubapi.position;

import org.example.peoplehubapi.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long employeeId, LocalDate endDate, LocalDate startDate);

    List<Position> findByEmployeeId(Long id);
}

