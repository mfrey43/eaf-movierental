package ch.fhnw.edu.rental.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import ch.fhnw.edu.rental.model.Movie;
import ch.fhnw.edu.rental.persistence.MovieRepository;
import ch.fhnw.edu.rental.persistence.PriceCategoryRepository;

@Component
public class MovieRepositoryImpl implements MovieRepository {

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private PriceCategoryRepository priceCategoryRepo;

    @Override
    public Movie findOne(Long id) {
        Map<String, Object> res = template.queryForMap("select * from MOVIES where MOVIE_ID = ?", id);
        long priceCategory = (long) res.get("PRICECATEGORY_FK");
        Movie m = new Movie(
                (String) res.get("MOVIE_TITLE"),
                (java.sql.Timestamp) res.get("MOVIE_RELEASEDATE"),
                priceCategoryRepo.findOne(priceCategory)
        );
        m.setId((Long) res.get("MOVIE_ID"));
        m.setRented((Boolean) res.get("MOVIE_RENTED"));
        return m;
    }

    @Override
    public List<Movie> findAll() {
        return template.query("select * from MOVIES", (rs, row) -> createMovie(rs));

    }

    @Override
    public List<Movie> findByTitle(String name) {
        return template.query("select * from MOVIES where MOVIE_TITLE = ?",
                (rs, row) -> createMovie(rs), name);
    }

    @Override
    public Movie save(Movie movie) {
        if(movie.getId() != null && exists(movie.getId())){
            template.update("UPDATE MOVIES SET MOVIE_TITLE=?, MOVIE_RELEASEDATE=?, MOVIE_RENTED=?, PRICECATEGORY_FK=? where MOVIE_ID=?",
                    movie.getTitle(), movie.getReleaseDate(), movie.isRented(), movie.getPriceCategory().getId(), movie.getId()
            );
        }else{
            SimpleJdbcInsert inserter = new SimpleJdbcInsert(template).withTableName("MOVIES").usingGeneratedKeyColumns("MOVIE_ID");

            Map<String, Object> parameters = new HashMap<>(4);
            parameters.put("MOVIE_TITLE", movie.getTitle());
            parameters.put("MOVIE_RELEASEDATE", movie.getReleaseDate());
            parameters.put("MOVIE_RENTED", movie.isRented());
            parameters.put("PRICECATEGORY_FK", movie.getPriceCategory().getId());

            Number newId = inserter.executeAndReturnKey(parameters);
            movie.setId((Long)newId);

        }


        return movie;
    }

    @Override
    public void delete(Movie movie) {
        if (movie == null) throw new IllegalArgumentException();

        template.update("DELETE FROM MOVIES WHERE MOVIE_ID=?", movie.getId());
        movie.setId(null);
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException();
        delete(findOne(id));
    }

    @Override
    public boolean exists(Long id) {
        if (id == null) throw new IllegalArgumentException();
        return findOne(id) != null;
    }

    @Override
    public long count() {
        return template.queryForObject("SELECT COUNT(*) FROM MOVIES", Long.class);
    }

    private Movie createMovie(ResultSet rs) throws SQLException{
        long priceCategory = rs.getLong("PRICECATEGORY_FK");
        Movie m = new Movie(
                rs.getString("MOVIE_TITLE"),
                rs.getTimestamp("MOVIE_RELEASEDATE"),
                priceCategoryRepo.findOne(priceCategory)
        );
        m.setId(rs.getLong("MOVIE_ID"));
        m.setRented(rs.getBoolean("MOVIE_RENTED"));

        return m;
    }

}
