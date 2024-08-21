package org.gs;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

  @Inject MovieRepository movieRepository;

  /**
   * Finds all movies currently registered in the system
   * @return A `Response` with <strong>OK</strong> status containing a list of all registered movies
   */
  @GET
  public Response getAll() {
    List<Movie> movies = movieRepository.listAll();
    return Response.ok(movies).build();
  }

  /**
   * Retrieves a movie by its <strong>ID</strong>.
   * @param id Unique identifier of the targeted movie
   * @return A `Response` with <strong>OK</strong> status containing the movie if found,
   * or a <strong>NOT_FOUND</strong> status if not found
   */
  @GET
  @Path("{id}")
  public Response getById(@PathParam("id") Long id) {
    return movieRepository
        .findByIdOptional(id)
        .map(movie -> Response.ok(movie).build())
        .orElse(Response.status(NOT_FOUND).build());
  }

  /**
   * Retrieves a movie by its <strong>title</strong>
   * @param title The title of the movie to retrieve
   * @return A `Response` with <strong>OK</strong> status containing the movie if found,
   * or a <strong>NOT_FOUND</strong> status if not found
   */
  @GET
  @Path("title/{title}")
  public Response getByTitle(@PathParam("title") String title) {
    return movieRepository
        .find("title", title)
        .singleResultOptional()
        .map(movie -> Response.ok(movie).build())
        .orElse(Response.status(NOT_FOUND).build());
  }

  /**
   * Retrieves all movies by they <strong>country</strong>
   * @param country The country of the movies to retrieve
   * @return A `Response` with <strong>OK</strong> status containing all movies of the specified country
   */
  @GET
  @Path("country/{country}")
  public Response getByCountry(@PathParam("country") String country) {
    List<Movie> movies = movieRepository.findByCountry(country);
    return Response.ok(movies).build();
  }

  /**
   * Creates a new movie
   * @param movie New movie to be created
   * @return A `Response` with <strong>CREATED</strong> status,
   * or <strong>BAD_REQUEST</strong> if the movie received violated any validation constraint
   */
  @POST
  @Transactional
  public Response create(Movie movie) {
    movieRepository.persist(movie);
    if (movieRepository.isPersistent(movie)) {
      return Response.created(URI.create("/movies/" + movie.getId())).build();
    }
    return Response.status(BAD_REQUEST).build();
  }

  /**
   * Edits an existing movie
   * @param id The ID of the movie to be edited
   * @param movie New data of the edited movie
   * @return A `Response` with <strong>OK</strong> status containing the new edited movie,
   * or a <strong>NOT_FOUND</strong> status if the movie was not found
   */
  @PUT
  @Path("{id}")
  @Transactional
  public Response updateById(@PathParam("id") Long id, Movie movie) {
    return movieRepository
        .findByIdOptional(id)
        .map(
            m -> {
              m.setTitle(movie.getTitle());
              return Response.ok(m).build();
            })
        .orElse(Response.status(NOT_FOUND).build());
  }

  /**
   * Deletes a movie by its <strong>ID</strong>.
   * @param id The ID of the movie to delete
   * @return a Response with <strong>NO_CONTENT</strong> status if the movie was deleted,
   * or <strong>NOT_FOUND</strong> status if the movie was not found
   */
  @DELETE
  @Path("{id}")
  @Transactional
  public Response deleteById(@PathParam("id") Long id) {
    boolean deleted = movieRepository.deleteById(id);
    return deleted ? Response.noContent().build() : Response.status(NOT_FOUND).build();
  }
}
