package org.RMQSales.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "markups_discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkupsDiscounts {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "position_id")
    private Positions position_id;

    @OneToOne
    @JoinColumn(name = "check_id")
    private Checks check_id;

    @Column(name = "check_link")
    private int check_link;

    @Column(name = "sum")
    private double sum;

    @Column(name = "markup_discount_ref")
    private UUID markup_discount_ref;

    @Column(name = "inserted_at")
    private LocalDateTime inserted_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

}
