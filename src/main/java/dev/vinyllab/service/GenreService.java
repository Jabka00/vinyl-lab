package dev.vinyllab.service;

import dev.vinyllab.dto.GenreCard;
import dev.vinyllab.entity.Genre;
import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.exception.NotFoundException;
import dev.vinyllab.form.GenreForm;
import dev.vinyllab.mapper.GenreMapper;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.GenreRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

  private final GenreRepository genres;
  private final AlbumRepository albums;
  private final GenreMapper genreMapper;

  public List<GenreCard> list() {
    Map<Long, Long> counts = albums.findDetailed().stream()
        .flatMap(album -> album.getGenres().stream())
        .collect(Collectors.groupingBy(Genre::getId, Collectors.counting()));
    return genres.findAllByOrderByNameAsc().stream()
        .map(genre -> genreMapper.toCard(genre, counts.getOrDefault(genre.getId(), 0L)))
        .toList();
  }

  public GenreForm form(Long id) {
    return genreMapper.toForm(require(id));
  }

  @Transactional
  public void create(GenreForm form) {
    ensureUnique(form.getName(), null);
    Genre genre = new Genre();
    genreMapper.apply(form, genre);
    save(genre);
  }

  @Transactional
  public void update(Long id, GenreForm form) {
    ensureUnique(form.getName(), id);
    Genre genre = require(id);
    genreMapper.apply(form, genre);
    save(genre);
  }

  @Transactional
  public void delete(Long id) {
    Genre genre = require(id);
    if (albums.existsByGenres_Id(id)) {
      throw new ConflictException("Жанр використовується в альбомах");
    }
    genres.delete(genre);
  }

  private Genre require(Long id) {
    return genres.findById(id).orElseThrow(() -> new NotFoundException("Жанр не знайдено"));
  }

  private void ensureUnique(String name, Long currentId) {
    boolean taken = currentId == null
        ? genres.existsByNameIgnoreCase(name.trim())
        : genres.existsByNameIgnoreCaseAndIdNot(name.trim(), currentId);
    if (taken) {
      throw new ConflictException("Такий жанр уже є");
    }
  }

  private void save(Genre genre) {
    try {
      genres.saveAndFlush(genre);
    } catch (DataIntegrityViolationException exception) {
      throw new ConflictException("Такий жанр уже є");
    }
  }
}
