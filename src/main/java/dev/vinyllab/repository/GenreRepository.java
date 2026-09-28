package dev.vinyllab.repository;

import dev.vinyllab.entity.Genre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

  List<Genre> findAllByOrderByNameAsc();

  Optional<Genre> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
