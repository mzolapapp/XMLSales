package org.RMQSales.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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


    @Column(name = "position_id")
    private long position_id;

   @Column(name = "check_id")
    private UUID check_id;

    @Column(name = "check_link")
    private int check_link;

    @Column(name = "sum")
    private double sum;

    @Column(name = "markup_discount_ref")
    private UUID markup_discount_ref;

    @CreationTimestamp
    @Column(name = "inserted_at", updatable = false)
    private LocalDateTime inserted_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

}
