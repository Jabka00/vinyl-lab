package dev.vinyllab.service;

import dev.vinyllab.dto.AlbumView;
import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.Artist;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.exception.NotFoundException;
import dev.vinyllab.form.AlbumForm;
import dev.vinyllab.mapper.AlbumMapper;
import dev.vinyllab.repository.AlbumRatingRepository;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.ArtistRepository;
import dev.vinyllab.repository.CollectionItemRepository;
import dev.vinyllab.repository.GenreRepository;
import dev.vinyllab.repository.projection.RatingAggregate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {

  private static final Locale UK = Locale.forLanguageTag("uk");

  private final AlbumRepository albums;
  private final ArtistRepository artists;
  private final GenreRepository genres;
  private final AlbumRatingRepository ratings;
  private final CollectionItemRepository collectionItems;
  private final AlbumMapper albumMapper;

  public List<AlbumView> catalog(String query, Long genreId, String sort) {
    String needle = normalize(query);
    Map<Long, Score> summary = summaries();
    return albums.findDetailed().stream()
        .filter(album -> matches(album, needle, genreId))
        .map(album -> toView(album, summary, null, 0))
        .sorted(comparator(sort))
        .toList();
  }

  public List<AlbumView> topRated(int limit) {
    return catalog(null, null, "rating").stream()
        .filter(album -> album.ratingCount() > 0)
        .limit(limit)
        .toList();
  }

  public List<AlbumView> byArtist(Long artistId) {
    Map<Long, Score> summary = summaries();
    return albums.findDetailedByArtistId(artistId).stream()
        .map(album -> toView(album, summary, null, 0))
        .sorted(Comparator.comparingInt(AlbumView::releaseYear).reversed()
            .thenComparing(AlbumView::title, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  public List<AlbumView> choices() {
    return catalog(null, null, "title").stream()
        .sorted(Comparator.comparing(AlbumView::artistName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(AlbumView::title, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  public AlbumView details(Long id, Long userId) {
    Album album = requireDetailed(id);
    Integer myScore = null;
    long copies = 0;
    if (userId != null) {
      myScore = ratings.findByUserIdAndAlbumId(userId, id).map(row -> row.getScore()).orElse(null);
      copies = collectionItems.countByOwnerIdAndAlbumId(userId, id);
    }
    return toView(album, summaries(), myScore, copies);
  }

  public AlbumForm form(Long id) {
    return albumMapper.toForm(requireDetailed(id));
  }

  @Transactional
  public void create(AlbumForm form) {
    ensureUnique(form, null);
    Album album = new Album();
    albumMapper.apply(form, album, artist(form.getArtistId()), genreSet(form.getGenreIds()));
    save(album);
  }

  @Transactional
  public void update(Long id, AlbumForm form) {
    ensureUnique(form, id);
    Album album = requireDetailed(id);
    albumMapper.apply(form, album, artist(form.getArtistId()), genreSet(form.getGenreIds()));
    save(album);
  }

  @Transactional
  public void delete(Long id) {
    Album album = albums.findById(id).orElseThrow(() -> new NotFoundException("Альбом не знайдено"));
    if (collectionItems.existsByAlbumId(id)) {
      throw new ConflictException("Альбом є в чиїйсь колекції");
    }
    ratings.deleteForAlbum(id);
    albums.delete(album);
  }

  private Album requireDetailed(Long id) {
    List<Album> found = albums.findDetailedById(id);
    if (found.isEmpty()) {
      throw new NotFoundException("Альбом не знайдено");
    }
    return found.getFirst();
  }

  private Artist artist(Long id) {
    return artists.findById(id).orElseThrow(() -> new NotFoundException("Виконавця не знайдено"));
  }

  private Set<Genre> genreSet(Set<Long> ids) {
    List<Genre> found = genres.findAllById(ids);
    if (found.size() != ids.size()) {
      throw new NotFoundException("Обраний жанр не знайдено");
    }
    return new HashSet<>(found);
  }

  private void ensureUnique(AlbumForm form, Long currentId) {
    String title = form.getTitle().trim();
    boolean taken = currentId == null
        ? albums.existsByArtist_IdAndTitleIgnoreCase(form.getArtistId(), title)
        : albums.existsByArtist_IdAndTitleIgnoreCaseAndIdNot(form.getArtistId(), title, currentId);
    if (taken) {
      throw new ConflictException("У цього виконавця вже є альбом із такою назвою");
    }
  }

  private void save(Album album) {
    try {
      albums.saveAndFlush(album);
    } catch (DataIntegrityViolationException exception) {
      throw new ConflictException("У цього виконавця вже є альбом із такою назвою");
    }
  }

  private AlbumView toView(Album album, Map<Long, Score> summary, Integer myScore, long copies) {
    Score score = summary.getOrDefault(album.getId(), Score.EMPTY);
    return albumMapper.toView(album, score.average(), score.count(), myScore, copies);
  }

  private Map<Long, Score> summaries() {
    return ratings.aggregateAll().stream()
        .collect(Collectors.toMap(
            RatingAggregate::getAlbumId,
            row -> new Score(
                row.getAverageScore() == null ? 0d : row.getAverageScore(),
                row.getVoteCount() == null ? 0L : row.getVoteCount()
            )
        ));
  }

  private boolean matches(Album album, String needle, Long genreId) {
    if (genreId != null && album.getGenres().stream().noneMatch(genre -> genreId.equals(genre.getId()))) {
      return false;
    }
    if (needle == null) {
      return true;
    }
    String haystack = (album.getTitle() + " " + album.getArtist().getName()).toLowerCase(UK);
    return haystack.contains(needle);
  }

  private Comparator<AlbumView> comparator(String sort) {
    return switch (sort == null ? "" : sort) {
      case "rating" -> Comparator.comparingDouble(AlbumView::averageRating).reversed()
          .thenComparing(Comparator.comparingLong(AlbumView::ratingCount).reversed())
          .thenComparing(AlbumView::title, String.CASE_INSENSITIVE_ORDER);
      case "year" -> Comparator.comparingInt(AlbumView::releaseYear).reversed()
          .thenComparing(AlbumView::title, String.CASE_INSENSITIVE_ORDER);
      default -> Comparator.comparing(AlbumView::title, String.CASE_INSENSITIVE_ORDER);
    };
  }

  private record Score(double average, long count) {
    private static final Score EMPTY = new Score(0, 0);
  }

  private String normalize(String query) {
    if (query == null || query.isBlank()) {
      return null;
    }
    return query.trim().toLowerCase(UK);
  }
}
