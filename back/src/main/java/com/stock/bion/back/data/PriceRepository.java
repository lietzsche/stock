package com.stock.bion.back.data;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
    List<PriceEntity> findByCodeOrderByDateDesc(String code);
    boolean existsByCodeAndDate(String code, LocalDate date);

    @Query("select distinct p.code from PriceEntity p")
    List<String> findDistinctCodes();
}
