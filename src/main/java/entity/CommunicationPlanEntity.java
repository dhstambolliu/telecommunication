package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CommunicationPlan")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CommunicationPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long ID;

    @Column(name = "ratePlanName")
    private String ratePlanName;

    @Column(name = "contractPeriod")
    private int contractPeriod;

    @Column(name = "price")
    private int price;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "endsAt")
    private LocalDateTime endsAt;

    @Column(name = "MSISDN")
    private long MSISDN;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "activationReason")
    private String activationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerID")
    private CustomerEntity customerEntity;

}
