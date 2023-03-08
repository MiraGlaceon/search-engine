package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(indexes = @Index(name = "path_index", columnList = "path"))
@Setter
@Getter
@NoArgsConstructor
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(nullable = false, unique = true, length = 500)
    private String path;

    //@Column(nullable = false)
    private Integer code;

    // if you changed type of column you should alter table
    //@Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
