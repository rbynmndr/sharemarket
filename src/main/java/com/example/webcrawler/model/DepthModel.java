package com.example.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepthModel {
    private String name;
    private String livePrice;
    private String indicator;
    private String indicatorColor;
    private String prClose;
    private String open;
    private String high;
    private String low;
    private String close;
}
