package rest.pkbe.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import rest.pkbe.domain.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>{
    
    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN FETCH n.noteTags WHERE n.user.id = :userId")
    List<Note> findAllByUserIdWithTags(@Param("userId") Long userId);

    boolean existsByIdAndUserId(@NonNull Long noteId, @NonNull Long userId);

    Optional<Note> findByIdAndUserId(@NonNull Long noteId, @NonNull Long userId);
    
    @Query("SELECT n FROM Note n JOIN FETCH n.noteTags WHERE n.user.id = :userId AND n.id = :noteId")
    Optional<Note> findByIdAndUserIdWithTags(@Param("userId") Long userId, @Param("noteId") Long noteId);
}
