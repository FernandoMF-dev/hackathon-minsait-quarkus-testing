package org.gs;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.gs.factory.MovieFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieResourceTestIT {
  private static final String API_MOVIES = "/movies";
  private static final Long NON_EXISTING_ID = 0L;

  @Inject
  MovieFactory movieFactory;

  @BeforeEach
  void setUp() {
    RestAssured.basePath = API_MOVIES;
  }

  @Test
  @Order(1)
  void getAll() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .when().get()
        .then().statusCode(Response.Status.OK.getStatusCode())
        .body("", Matchers.not(Matchers.empty()))
        .body("id", Matchers.hasItem(movie.getId().intValue()))
        .body("title", Matchers.hasItem(movie.getTitle()));
  }

  @Test
  @Order(1)
  void getById() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .pathParam("id", movie.getId())
        .when().get("/{id}")
        .then().statusCode(Response.Status.OK.getStatusCode())
        .body("id", Matchers.equalTo(movie.getId().intValue()))
        .body("title", Matchers.equalTo(movie.getTitle()));
  }

  @Test
  @Order(1)
  void getByIdKO() {
    given().accept(ContentType.JSON)
        .pathParam("id", NON_EXISTING_ID)
        .when().get("/{id}")
        .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @Order(1)
  void getByTitle() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .pathParam("title", movie.getTitle())
        .when().get("title/{title}")
        .then().statusCode(Response.Status.OK.getStatusCode())
        .body("id", Matchers.equalTo(movie.getId().intValue()))
        .body("title", Matchers.equalTo(movie.getTitle()));
  }

  @Test
  @Order(1)
  void getByTitleKO() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .pathParam("title", movie.getTitle() + " invalid")
        .when().get("title/{title}")
        .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @Order(2)
  void getByCountry() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .pathParam("country", movie.getCountry())
        .when().get("country/{country}")
        .then().statusCode(Response.Status.OK.getStatusCode())
        .body("", Matchers.not(Matchers.empty()))
        .body("id", Matchers.hasItem(movie.getId().intValue()))
        .body("title", Matchers.hasItem(movie.getTitle()));
  }

  @Test
  @Order(2)
  void getByCountryKO() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .pathParam("country", movie.getCountry() + " invalid")
        .when().get("country/{country}")
        .then().statusCode(Response.Status.OK.getStatusCode())
        .body("", Matchers.empty());
  }

  @Test
  @Order(3)
  void create() {
    Movie movie = movieFactory.createEntity();

    given().accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .body(movie)
        .when().post()
        .then().statusCode(Response.Status.CREATED.getStatusCode());
  }

  @Test
  @Order(4)
  void updateById() {
    Movie movie = movieFactory.createAndPersist();
    movie.setTitle(movie.getTitle() + " modified");

    given().accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .pathParam("id", movie.getId())
        .body(movie)
        .when().put("/{id}")
        .then().statusCode(Response.Status.OK.getStatusCode())
        .body("id", Matchers.equalTo(movie.getId().intValue()))
        .body("title", Matchers.equalTo(movie.getTitle()));
  }

  @Test
  @Order(4)
  void updateByIdKO() {
    Movie movie = movieFactory.createAndPersist();
    movie.setTitle(movie.getTitle() + " modified");

    given().accept(ContentType.JSON)
        .contentType(ContentType.JSON)
        .pathParam("id", NON_EXISTING_ID)
        .body(movie)
        .when().put("/{id}")
        .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @Order(5)
  void deleteById() {
    Movie movie = movieFactory.createAndPersist();

    given().accept(ContentType.JSON)
        .pathParam("id", movie.getId())
        .when().delete("/{id}")
        .then().statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  @Order(5)
  void deleteByIdKO() {
    given().accept(ContentType.JSON)
        .pathParam("id", NON_EXISTING_ID)
        .when().delete("/{id}")
        .then().statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }
}
