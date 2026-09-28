package dev.vinyllab.repository;

import dev.vinyllab.entity.Artist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

  List<Artist> findAllByOrderByNameAsc();

  Optional<Artist> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
