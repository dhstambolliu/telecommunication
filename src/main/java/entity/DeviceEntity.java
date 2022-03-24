package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Device")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class DeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long ID;

    @Column(name = "deviceName")
    private String deviceName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private int price;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "communicationPlanID", referencedColumnName = "ID")
    private CommunicationPlanEntity communicationPlanEntity;

}
