package dev.vinyllab.repository;

import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.AlbumRating;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Query("select r from AlbumRating r where r.user.id = :userId and r.album.id = :albumId")
  Optional<AlbumRating> findRating(@Param("userId") Long userId, @Param("albumId") Long albumId);

  @Query("select r.album.id, r.score from AlbumRating r")
  List<Object[]> ratingScores();

  @Query("select count(r) from AlbumRating r")
  long countRatings();

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from AlbumRating r where r.user.id = :userId")
  void deleteRatingsForUser(@Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from AlbumRating r where r.album.id = :albumId")
  void deleteRatingsForAlbum(@Param("albumId") Long albumId);
}
