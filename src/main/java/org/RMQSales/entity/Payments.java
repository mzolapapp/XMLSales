package org.RMQSales.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "check_id")
    private Checks check_id;

    @Column(name = "payment_type")
    private String payment_type;

    @Column(name = "sum")
    private double sum;

    @Column(name = "payment_link")
    private int payment_link;

    @Column(name = "inserted_at")
    private LocalDateTime inserted_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

}
