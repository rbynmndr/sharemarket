package com.example.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock_table")
public class NepseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column
    private String symbol;
    @Column
    private String symbolNo;
    @Column
    private String ltp;
    @Column
    private LocalDate date;
}
