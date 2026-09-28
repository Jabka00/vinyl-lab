package dev.vinyllab.service;

import dev.vinyllab.entity.Album;
import dev.vinyllab.entity.AlbumRating;
import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.exception.NotFoundException;
import dev.vinyllab.repository.AlbumRatingRepository;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

  private final AlbumRatingRepository ratings;
  private final AlbumRepository albums;
  private final UserRepository users;

  @Transactional
  public void rate(Long userId, Long albumId, int score) {
    if (score < 1 || score > 5) {
      throw new ConflictException("Оцінка має бути від 1 до 5");
    }
    Album album = albums.findById(albumId).orElseThrow(() -> new NotFoundException("Альбом не знайдено"));
    UserAccount user = users.findById(userId).orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
    AlbumRating rating = ratings.findByUserIdAndAlbumId(userId, albumId).orElseGet(AlbumRating::new);
    rating.setUser(user);
    rating.setAlbum(album);
    rating.setScore(score);
    ratings.save(rating);
  }
}
