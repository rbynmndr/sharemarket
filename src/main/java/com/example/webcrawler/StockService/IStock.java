package com.example.webcrawler.StockService;

import com.example.webcrawler.model.NepseModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IStock {
    NepseModel saveNepse(NepseModel nepseModel);

    List<NepseModel> findByDate(LocalDate date);

    List<NepseModel> findBySymbolNo(String symbol);

    int updateNepseModelSetSymbolNo(String SymbolNo, String symbol);
}
