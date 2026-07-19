package hexlet.code.repository;

import hexlet.code.model.Label;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    boolean existsByName(String name);

    Optional<Label> findByName(String name);

    List<Label> findAllByIdIn(Collection<Long> ids);
}
