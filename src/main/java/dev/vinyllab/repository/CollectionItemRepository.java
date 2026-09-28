package dev.vinyllab.repository;

import dev.vinyllab.entity.CollectionItem;
import dev.vinyllab.repository.projection.ConditionCount;
import dev.vinyllab.repository.projection.NamedCount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

  @Query("""
      select distinct c from CollectionItem c
      join fetch c.owner
      join fetch c.album a
      join fetch a.artist
      left join fetch a.genres
      where c.owner.id = :ownerId
      """)
  List<CollectionItem> findOwned(@Param("ownerId") Long ownerId);

  @Query("""
      select distinct c from CollectionItem c
      join fetch c.owner
      join fetch c.album a
      join fetch a.artist
      left join fetch a.genres
      where c.id = :id
      """)
  List<CollectionItem> findDetailedById(@Param("id") Long id);

  long countByOwnerId(Long ownerId);

  long countByOwnerIdAndAlbumId(Long ownerId, Long albumId);

  boolean existsByAlbumId(Long albumId);

  @Query("""
      select g.name as name, count(c.id) as total
      from CollectionItem c
      join c.album a
      join a.genres g
      where c.owner.id = :ownerId
      group by g.name
      order by count(c.id) desc, g.name asc
      """)
  List<NamedCount> countByGenre(@Param("ownerId") Long ownerId);

  @Query("""
      select c.condition as condition, count(c.id) as total
      from CollectionItem c
      where c.owner.id = :ownerId
      group by c.condition
      """)
  List<ConditionCount> countByCondition(@Param("ownerId") Long ownerId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from CollectionItem c where c.owner.id = :ownerId")
  void deleteForOwner(@Param("ownerId") Long ownerId);
}
