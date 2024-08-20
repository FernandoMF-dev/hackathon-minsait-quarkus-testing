package org.gs;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.gs.factory.MovieFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class MovieResourceTest {
  private static final Long NON_EXISTING_ID = 0L;

  @InjectMock
  MovieRepository movieRepository;

  @Inject
  MovieResource movieResource;

  @Inject
  MovieFactory movieFactory;

  @BeforeEach
  void setUp() {
    movieRepository.deleteAll();
  }

  @Test
  void getAll() {
    Movie movie = movieFactory.createAndPersist();
    List<Movie> movies = List.of(movie);
    Mockito.when(movieRepository.listAll()).thenReturn(movies);

    Response response = movieResource.getAll();

    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void getByIdOK() {
    Movie movie = movieFactory.createAndPersist();
    Mockito.when(movieRepository.findByIdOptional(movie.getId())).thenReturn(Optional.of(movie));

    Response response = movieResource.getById(movie.getId());

    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(movie.getId(), ((Movie) response.getEntity()).getId());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void getByIdKO() {
    Mockito.when(movieRepository.findByIdOptional(NON_EXISTING_ID)).thenReturn(Optional.empty());

    Response response = movieResource.getById(NON_EXISTING_ID);

    assertNotNull(response);
    assertNull(response.getEntity());
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  void getByTitleOK() {
    Movie movie = movieFactory.createAndPersist();
    PanacheQuery<Movie> panacheQuery = Mockito.mock(PanacheQuery.class);
    Mockito.when(panacheQuery.singleResultOptional()).thenReturn(Optional.of(movie));
    Mockito.when(movieRepository.find("title", movie.getTitle())).thenReturn(panacheQuery);

    Response response = movieResource.getByTitle(movie.getTitle());

    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(movie.getId(), ((Movie) response.getEntity()).getId());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void getByTitleKO() {
    Movie movie = movieFactory.createAndPersist();
    String title = movie.getTitle() + " invalid";
    PanacheQuery<Movie> panacheQuery = Mockito.mock(PanacheQuery.class);
    Mockito.when(panacheQuery.singleResultOptional()).thenReturn(Optional.empty());
    Mockito.when(movieRepository.find("title", title)).thenReturn(panacheQuery);

    Response response = movieResource.getByTitle(title);

    assertNotNull(response);
    assertNull(response.getEntity());
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  void getByCountry() {
    Movie movie = movieFactory.createAndPersist();
    List<Movie> movies = List.of(movie);
    Mockito.when(movieRepository.findByCountry(movie.getCountry())).thenReturn(movies);

    Response response = movieResource.getByCountry(movie.getCountry());

    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void createOK() {
    Movie movie = movieFactory.createEntity();
    Mockito.doNothing().when(movieRepository).persist(movie);
    Mockito.when(movieRepository.isPersistent(movie)).thenReturn(true);

    Response response = movieResource.create(movie);

    assertNotNull(response);
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
  }

  @Test
  void createKO() {
    Movie movie = movieFactory.createEntity();
    Mockito.doNothing().when(movieRepository).persist(movie);
    Mockito.when(movieRepository.isPersistent(movie)).thenReturn(false);

    Response response = movieResource.create(movie);

    assertNotNull(response);
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
  }

  @Test
  void updateByIdOK() {
    Movie movie = movieFactory.createAndPersist();
    String oldTitle = movie.getTitle();
    movie.setTitle(oldTitle + " modified");
    Mockito.when(movieRepository.findByIdOptional(movie.getId())).thenReturn(Optional.of(movie));

    Response response = movieResource.updateById(movie.getId(), movie);

    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(movie.getId(), ((Movie) response.getEntity()).getId());
    assertEquals(movie.getTitle(), ((Movie) response.getEntity()).getTitle());
    assertNotEquals(oldTitle, ((Movie) response.getEntity()).getTitle());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void updateByIdKO() {
    Movie movie = movieFactory.createAndPersist();
    String oldTitle = movie.getTitle();
    movie.setTitle(oldTitle + " modified");
    Mockito.when(movieRepository.findByIdOptional(NON_EXISTING_ID)).thenReturn(Optional.empty());

    Response response = movieResource.updateById(NON_EXISTING_ID, movie);

    assertNotNull(response);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  void deleteByIdOK() {
    Movie movie = movieFactory.createAndPersist();
    Mockito.when(movieRepository.deleteById(movie.getId())).thenReturn(true);

    Response response = movieResource.deleteById(movie.getId());

    assertNotNull(response);
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  void deleteByIdKO() {
    Mockito.when(movieRepository.deleteById(NON_EXISTING_ID)).thenReturn(false);

    Response response = movieResource.deleteById(NON_EXISTING_ID);

    assertNotNull(response);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }
}
