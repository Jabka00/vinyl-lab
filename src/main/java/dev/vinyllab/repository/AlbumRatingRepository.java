package dev.vinyllab.repository;

import dev.vinyllab.entity.AlbumRating;
import dev.vinyllab.repository.projection.RatingAggregate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRatingRepository extends JpaRepository<AlbumRating, Long> {

  Optional<AlbumRating> findByUserIdAndAlbumId(Long userId, Long albumId);

  @Query("""
      select r.album.id as albumId, avg(r.score) as averageScore, count(r.id) as voteCount
      from AlbumRating r
      group by r.album.id
      """)
  List<RatingAggregate> aggregateAll();

  @Query("""
      select r.album.id as albumId, avg(r.score) as averageScore, count(r.id) as voteCount
      from AlbumRating r
      where r.album.id in :albumIds
      group by r.album.id
      """)
  List<RatingAggregate> aggregateByAlbumIds(@Param("albumIds") Collection<Long> albumIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from AlbumRating r where r.user.id = :userId")
  void deleteForUser(@Param("userId") Long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from AlbumRating r where r.album.id = :albumId")
  void deleteForAlbum(@Param("albumId") Long albumId);
}
