package com.example.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SellModel {
    private String sellOrder;
    private String sellQuantity;
    private String sellPrice;
}
