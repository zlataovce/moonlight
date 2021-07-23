package me.zlataovce.moonlight.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
public class Paste {
    @Id @GeneratedValue(strategy = GenerationType.AUTO) @JsonIgnore
    protected long id;
    @Getter
    private Date created;
    @Getter @Setter @Lob
    private String content;
    @Getter @Setter
    private String identifier;
    @Getter @Setter
    private String url = null;

    @PrePersist
    protected void onCreate() {
        this.created = new Date();
    }
}
