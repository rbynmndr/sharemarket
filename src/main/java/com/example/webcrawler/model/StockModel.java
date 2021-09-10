package com.example.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockModel {
    private String stockNo;
    private String symbol;
    private String ltp;
    private String pointChange;
    private String porL;

    public StockModel(String symbol, String ltp, String pointChange, String porL){
        this.symbol = symbol;
        this.ltp = ltp;
        this.pointChange = pointChange;
        this.porL = porL;
    }

    public StockModel(String symbol, String stockNo){
        this.stockNo = stockNo;
        this.symbol = symbol;
    }
}
