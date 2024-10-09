package nn.ru.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dirs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // прямая Dir-Docs
    @OneToMany(cascade = CascadeType.ALL
            , fetch = FetchType.EAGER
    )
    @JoinColumn(name = "dir_id")
    private List<Doc> docs;

    public void addDocToItem(Doc doc) {
        if (docs == null) {
            docs = new ArrayList<>();
        }
        docs.add(doc);
    }


}
