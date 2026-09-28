package dev.vinyllab.service;

import dev.vinyllab.dto.ArtistView;
import dev.vinyllab.entity.Artist;
import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.exception.NotFoundException;
import dev.vinyllab.form.ArtistForm;
import dev.vinyllab.mapper.ArtistMapper;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.ArtistRepository;
import dev.vinyllab.repository.projection.IdCount;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

  private final ArtistRepository artists;
  private final AlbumRepository albums;
  private final ArtistMapper artistMapper;
  private final AlbumService albumService;

  public List<ArtistView> list(String query) {
    String needle = normalize(query);
    Map<Long, Long> counts = counts();
    return artists.findAllByOrderByNameAsc().stream()
        .filter(artist -> needle == null || artist.getName().toLowerCase(Locale.forLanguageTag("uk")).contains(needle))
        .map(artist -> artistMapper.toView(artist, counts.getOrDefault(artist.getId(), 0L), List.of()))
        .toList();
  }

  public ArtistView details(Long id) {
    Artist artist = require(id);
    var albums = albumService.byArtist(id);
    return artistMapper.toView(artist, albums.size(), albums);
  }

  public ArtistForm form(Long id) {
    return artistMapper.toForm(require(id));
  }

  @Transactional
  public void create(ArtistForm form) {
    ensureUnique(form.getName(), null);
    Artist artist = new Artist();
    artistMapper.apply(form, artist);
    save(artist);
  }

  @Transactional
  public void update(Long id, ArtistForm form) {
    ensureUnique(form.getName(), id);
    Artist artist = require(id);
    artistMapper.apply(form, artist);
    save(artist);
  }

  @Transactional
  public void delete(Long id) {
    Artist artist = require(id);
    if (albums.existsByArtist_Id(id)) {
      throw new ConflictException("Спочатку приберіть альбоми цього виконавця");
    }
    artists.delete(artist);
  }

  private Artist require(Long id) {
    return artists.findById(id).orElseThrow(() -> new NotFoundException("Виконавця не знайдено"));
  }

  private void ensureUnique(String name, Long currentId) {
    boolean taken = currentId == null
        ? artists.existsByNameIgnoreCase(name.trim())
        : artists.existsByNameIgnoreCaseAndIdNot(name.trim(), currentId);
    if (taken) {
      throw new ConflictException("Виконавець із таким ім'ям уже є");
    }
  }

  private void save(Artist artist) {
    try {
      artists.saveAndFlush(artist);
    } catch (DataIntegrityViolationException exception) {
      throw new ConflictException("Виконавець із таким ім'ям уже є");
    }
  }

  private Map<Long, Long> counts() {
    return albums.countGroupedByArtist().stream()
        .collect(Collectors.toMap(IdCount::getId, IdCount::getTotal));
  }

  private String normalize(String query) {
    if (query == null || query.isBlank()) {
      return null;
    }
    return query.trim().toLowerCase(Locale.forLanguageTag("uk"));
  }
}
