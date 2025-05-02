package in.lakshay.entity;

import jakarta.persistence.*;
import lombok.Data;

// simple role entity for auth - ROLE_USER or ROLE_ADMIN
@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // using Integer cuz we'll only have a few roles

    @Column(nullable = false, unique = true)
    private String name;  // eg: ROLE_USER, ROLE_ADMIN

    // maybe add description field later?
}