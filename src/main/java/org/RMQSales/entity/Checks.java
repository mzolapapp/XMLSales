package org.RMQSales.entity;

import com.sun.istack.NotNull;
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
@Table(name = "checks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Checks {
    @Id
    @NotNull
    @Column(name = "id")
    private UUID id;

    @Column(name = "check_status")
    private String check_status;

    @Column(name = "date_time")
    private LocalDateTime date_time;

    @Column(name = "division_id")
    private UUID division_id;

    @Column(name = "document_sum")
    private double document_sum;

    @Column(name = "employee_id")
    private UUID employee_id;

    @Column(name = "internet_sale")
    private boolean internet_sale;

    @Column(name = "operation_type")
    private String operation_type;

    @Column(name = "bonus_card_code")
    private String bonus_card_code;

    @Column(name = "posted")
    private boolean posted;

    @Column(name = "delete_mark")
    private boolean delete_mark;

    @CreationTimestamp
    @Column(name = "inserted_at", updatable = false)
    private LocalDateTime inserted_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updated_at;


}
