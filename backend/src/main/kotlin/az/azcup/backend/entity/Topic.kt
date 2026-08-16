package az.azcup.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "topic")
class Topic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var slug: String = "",

    @Column(nullable = false)
    var orderIndex: Int = 0,

    @Column(nullable = false)
    var title: String = "",

    var monthTag: String? = null,

    @Column(length = 2000)
    var description: String? = null,

    @Column(nullable = false)
    var published: Boolean = false
)
