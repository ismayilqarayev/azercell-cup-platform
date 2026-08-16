package az.azcup.backend.entity

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "problem")
class Problem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    var topic: Topic? = null,

    @Column(nullable = false)
    var orderIndex: Int = 0,

    var subgroupLabel: String? = null,

    @Column(nullable = false)
    var title: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var difficulty: Difficulty? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_tags", joinColumns = [JoinColumn(name = "problem_id")])
    @Column(name = "tag")
    @BatchSize(size = 50)
    var tags: MutableList<String> = mutableListOf(),

    @Column(nullable = false, length = 4000)
    var statement: String = "",

    @Column(length = 2000)
    var inputSpec: String? = null,

    @Column(length = 2000)
    var outputSpec: String? = null,

    @Column(nullable = false, length = 4000)
    var exampleInput: String = "",

    @Column(nullable = false, length = 4000)
    var exampleOutput: String = "",

    @Column(length = 4000)
    var approach: String? = null,

    @Column(length = 8000)
    var referenceSolution: String? = null
)
