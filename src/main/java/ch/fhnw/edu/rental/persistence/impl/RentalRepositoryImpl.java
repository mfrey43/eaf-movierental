package ch.fhnw.edu.rental.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import ch.fhnw.edu.rental.model.Rental;
import ch.fhnw.edu.rental.model.User;
import ch.fhnw.edu.rental.persistence.MovieRepository;
import ch.fhnw.edu.rental.persistence.RentalRepository;
import ch.fhnw.edu.rental.persistence.UserRepository;

@Component
public class RentalRepositoryImpl implements RentalRepository {

	@Autowired
	private JdbcTemplate template;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private MovieRepository movieRepo;
	
	@Override
	public Rental findOne(Long id) {
		if(id == null) throw new IllegalArgumentException();

		return template.queryForObject("select * from RENTALS where RENTAL_ID = ?", (rs, row) -> createRental(rs), id);
	}

	@Override
	public List<Rental> findAll() {
		return template.query("select * from RENTALS", (rs, row) -> createRental(rs));
	}

	@Override
	public List<Rental> findByUser(User user) {
		return template.query("select * from RENTALS where USER_ID = ?", (rs, row) -> createRental(rs), user.getId());
	}

	@Override
	public Rental save(Rental rental) {
		if(rental.getId() != null && exists(rental.getId())){
			template.update("UPDATE RENTALS SET RENTAL_RENTALDATE=?, RENTAL_RENTALDAYS=?, USER_ID=?, MOVIE_ID=? where RENTAL_ID=?",
					rental.getRentalDate(), rental.getRentalDays(), rental.getUser().getId(), rental.getMovie().getId()
			);
		}else{
			SimpleJdbcInsert inserter = new SimpleJdbcInsert(template).withTableName("RENTALS").usingGeneratedKeyColumns("RENTAL_ID");

			Map<String, Object> parameters = new HashMap<>(3);
			parameters.put("RENTAL_RENTALDATE", rental.getRentalDate());
			parameters.put("RENTAL_RENTALDAYS", rental.getRentalDays());
			parameters.put("USER_ID", rental.getUser().getId());
			parameters.put("MOVIE_ID", rental.getMovie().getId());

			Number newId = inserter.executeAndReturnKey(parameters);
			rental.setId((Long)newId);

		}

		return rental;
	}

	@Override
	public void delete(Rental rental) {
		if(rental == null) throw new IllegalArgumentException();
		template.update("DELETE FROM RENTALS WHERE RENTAL_ID=?", rental.getId());
		rental.setId(null);
	}

	@Override
	public void delete(Long id) {
		if(id == null) throw new IllegalArgumentException();
		delete(findOne(id));
	}

	@Override
	public boolean exists(Long id) {
		if(id == null) throw new IllegalArgumentException();
		return findOne(id) != null;
	}

	@Override
	public long count() {
		return template.queryForObject("SELECT COUNT(*) FROM RENTALS", Long.class);
	}

	private Rental createRental(ResultSet rs) throws SQLException {
		Rental r = new Rental(
				userRepo.findOne(rs.getLong("USER_ID")),
				movieRepo.findOne(rs.getLong("MOVIE_ID")),
				rs.getInt("RENTAL_RENTALDAYS"),
				true
		);
		r.setId(rs.getLong("RENTAL_ID"));
		r.setRentalDate(rs.getTimestamp("RENTAL_RENTALDATE"));
		return r;
	}
}
