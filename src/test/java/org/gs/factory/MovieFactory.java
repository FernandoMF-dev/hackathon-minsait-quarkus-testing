package org.gs.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.gs.Movie;
import org.gs.MovieRepository;

/**
 * MovieFactory
 */
@ApplicationScoped
public class MovieFactory {
    @Inject
    MovieRepository movieRepository;

    public Movie createEntity() {
        Movie entity = new Movie();
        
        entity.setTitle(String.format("Movie %d", System.currentTimeMillis()));
        entity.setDescription(String.format("Movie %d", System.currentTimeMillis()));
        entity.setDirector(String.format("Movie %d", System.currentTimeMillis()));
        entity.setCountry(String.format("Movie %d", System.currentTimeMillis()));

        return entity;
    }

    @Transactional
    public Movie persist(Movie entity) {
        movieRepository.persist(entity);
        return entity;
    }

    public Movie createAndPersist() {
        Movie entity = createEntity();
        return persist(entity);
    }
}
