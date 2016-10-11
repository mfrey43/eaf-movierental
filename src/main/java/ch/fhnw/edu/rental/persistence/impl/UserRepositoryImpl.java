package ch.fhnw.edu.rental.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import ch.fhnw.edu.rental.model.Rental;
import ch.fhnw.edu.rental.model.User;
import ch.fhnw.edu.rental.persistence.RentalRepository;
import ch.fhnw.edu.rental.persistence.UserRepository;

@Component
public class UserRepositoryImpl implements UserRepository {

	@Autowired
	private JdbcTemplate template;
	
	@Autowired
	private RentalRepository rentalRepo;

	@Override
	public User findOne(Long id) {
		if(id == null) throw new IllegalArgumentException();
		Map<String, Object> res = template.queryForMap("select * from USERS where USER_ID = ?", id);
		User u = new User(
				(String) res.get("USER_NAME"),
				(String) res.get("USER_FIRSTNAME")
		);
		u.setId((Long) res.get("USER_ID"));
		u.setEmail((String) res.get("USER_EMAIL"));

		return u;
	}

	@Override
	public List<User> findAll() {
		return template.query("select * from USERS", (rs, row) -> createUser(rs));
	}

	@Override
	public User save(User user) {
		if(user.getId() != null && exists(user.getId())){
			template.update("UPDATE USERS SET USER_EMAIL=?, USER_FIRSTNAME=?, USER_NAME=? where USER_ID=?",
					user.getEmail(), user.getFirstName(), user.getLastName(), user.getId()
			);
		}else{
			SimpleJdbcInsert inserter = new SimpleJdbcInsert(template).withTableName("USERS").usingGeneratedKeyColumns("USER_ID");

			Map<String, Object> parameters = new HashMap<>(3);
			parameters.put("USER_EMAIL", user.getEmail());
			parameters.put("USER_FIRSTNAME", user.getFirstName());
			parameters.put("USER_NAME", user.getLastName());

			Number newId = inserter.executeAndReturnKey(parameters);
			user.setId((Long)newId);

		}

		return user;
	}

	@Override
	public void delete(User user) {
		if(user == null) throw new IllegalArgumentException();
		for(Rental r : user.getRentals()){
			rentalRepo.delete(r);
		}
		template.update("DELETE FROM USERS WHERE USER_ID=?", user.getId());
		user.setId(null);
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
		return template.queryForObject("SELECT COUNT(*) FROM USERS", Long.class);
	}

	@Override
	public List<User> findByLastName(String lastName) {
		return template.query("select * from USERS where USER_NAME = ?", (rs, row) -> createUser(rs), lastName);
	}

	@Override
	public List<User> findByFirstName(String firstName) {
		return template.query("select * from USERS where USER_FIRSTNAME = ?", (rs, row) -> createUser(rs), firstName);
	}

	@Override
	public List<User> findByEmail(String email) {
		return template.query("select * from USERS where USER_EMAIL = ?", (rs, row) -> createUser(rs), email);
	}

	private User createUser(ResultSet rs) throws SQLException{
		User u = new User(
				rs.getString("USER_NAME"),
				rs.getString("USER_FIRSTNAME")
		);
		u.setId(rs.getLong("USER_ID"));
		u.setEmail(rs.getString("USER_EMAIL"));

		return u;
	}
}
