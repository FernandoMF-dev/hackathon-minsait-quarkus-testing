package org.gs;

import io.quarkus.test.junit.QuarkusTest;
import org.gs.factory.MovieFactory;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class MovieRepositoryTest {

  @Inject
  MovieRepository movieRepository;

  @Inject
  MovieFactory movieFactory;

  @Test
  void findByCountryOK() {
    Movie movie = movieFactory.createAndPersist();
    List<Movie> result = movieRepository.findByCountry(movie.getCountry());

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.stream().anyMatch(m -> Objects.equals(movie.getId(), m.getId())));
  }

  @Test
  void findByCountryKO() {
    Movie movie = movieFactory.createAndPersist();
    List<Movie> result = movieRepository.findByCountry(movie.getCountry() + " invalid");

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
