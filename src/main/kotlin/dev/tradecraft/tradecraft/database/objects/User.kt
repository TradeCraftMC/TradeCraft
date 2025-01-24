package dev.tradecraft.tradecraft.database.objects

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: String = "";
}