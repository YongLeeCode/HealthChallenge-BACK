package com.healthmate.healthmate.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 200)
	private String password;

	@Column(nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	public User(String email, String password, UserRole role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}
}


