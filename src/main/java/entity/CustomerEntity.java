package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Customer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long ID;

    @Column(name = "PersonalID")
    private String personalID;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "cityOfBirth")
    private String cityOfBirth;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customerEntity")
    private Set<CommunicationPlanEntity> communicationPlanEntitySet;

}
