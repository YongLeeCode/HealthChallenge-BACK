package com.healthmate.healthmate.domain.routine.entity;

import com.healthmate.healthmate.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routines")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    public Routine(User user, String name) {
        this.user = user;
        this.name = name;
    }
}
