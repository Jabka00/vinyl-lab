package dev.vinyllab.repository.projection;

public interface RatingAggregate {

  Long getAlbumId();

  Double getAverageScore();

  Long getVoteCount();
}
