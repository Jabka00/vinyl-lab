package dev.vinyllab.repository;

import dev.vinyllab.entity.Album;
import dev.vinyllab.repository.projection.IdCount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Long> {

  @Query("""
      select distinct a from Album a
      join fetch a.artist
      left join fetch a.genres
      """)
  List<Album> findDetailed();

  @Query("""
      select distinct a from Album a
      join fetch a.artist
      left join fetch a.genres
      where a.id = :id
      """)
  List<Album> findDetailedById(@Param("id") Long id);

  @Query("""
      select distinct a from Album a
      join fetch a.artist
      left join fetch a.genres
      where a.artist.id = :artistId
      """)
  List<Album> findDetailedByArtistId(@Param("artistId") Long artistId);

  boolean existsByArtist_Id(Long artistId);

  boolean existsByGenres_Id(Long genreId);

  boolean existsByArtist_IdAndTitleIgnoreCase(Long artistId, String title);

  boolean existsByArtist_IdAndTitleIgnoreCaseAndIdNot(Long artistId, String title, Long id);

  @Query("select a.artist.id as id, count(a.id) as total from Album a group by a.artist.id")
  List<IdCount> countGroupedByArtist();

  @Query("select g.id as id, count(a.id) as total from Album a join a.genres g group by g.id")
  List<IdCount> countGroupedByGenre();
}
