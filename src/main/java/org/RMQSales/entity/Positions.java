package org.RMQSales.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Positions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "check_id")
    private Checks checkId;

    @Column(name = "check_link")
    private int checkLink;

    @Column(name = "price")
    private double price;

    @Column(name = "count")
    private double count;

    @Column(name = "sum")
    private double sum;

    @Column(name = "sum_vat")
    private double sum_vat;

    @Column(name = "mz_sale_tech")
    private String mz_sale_tech;

    @Column(name = "manual_discount_sum")
    private double manual_discount_sum;

    @Column(name = "auto_discount_sum")
    private double auto_discount_sum;

    @Column(name = "bonus_discount_sum")
    private double bonus_discount_sum;

    @Column(name = "mz_mpm_bonus_sum")
    private double mz_mpm_bonus_sum;

    @Column(name = "mz_sale_mech")
    private String mz_sale_mech;

    @Column(name = "mz_customer_profit")
    private double mz_customer_profit;

    @Column(name = "nomenclature_ref")
    private UUID nomenclature_ref;

    @Column(name = "nomenclature_info_ref")
    private UUID nomenclature_info_ref;

    @Column(name = "inserted_at")
    private LocalDateTime inserted_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;
}
