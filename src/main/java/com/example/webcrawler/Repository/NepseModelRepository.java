package com.example.webcrawler.Repository;

import com.example.webcrawler.model.NepseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NepseModelRepository extends JpaRepository<NepseModel, Long> {
    List<NepseModel> findByDate(LocalDate date);

    List<NepseModel> findBySymbolNo(String symbolNo);

    @Modifying
    @Transactional
    @Query(value = "update stock_table s set s.symbol_no = ?1 where s.symbol = ?2", nativeQuery = true)
    int updateNepseModelSetSymbolNo(String symbolNo, String symbol);
}