package com.example.webcrawler.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BuyModel {
    private String buyOrder;
    private String buyQuantity;
    private String buyPrice;
}
