package rest.pkbe.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "note_tags")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class NoteTag {

    public NoteTag(Note note, Tag tag) {
        this.note = note;
        this.tag = tag;
    }

    @EmbeddedId
    @EqualsAndHashCode.Include
    private NoteTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("noteId")
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
    
}
