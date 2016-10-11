package ch.fhnw.edu.rental.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	private Long id;

	private String lastName;
	private String firstName;
	private String email;
	private List<Rental> rentals;

	public User(String lastName, String firstName) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.rentals = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String name) {
		this.lastName = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Rental> getRentals() {
		return rentals;
	}

	public void setRentals(List<Rental> rentals) {
		this.rentals = rentals;
	}

	public int getCharge() {
		int result = 0;
		for (Rental rental : rentals) {
			result += rental.getMovie().getPriceCategory().getCharge(rental.getRentalDays());
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		if (id != null && user.id != null && !id.equals(user.id)) return false;
		if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
		if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
		if (email != null ? !email.equals(user.email) : user.email != null) return false;
		return rentals != null ? rentals.equals(user.rentals) : user.rentals == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (rentals != null ? rentals.hashCode() : 0);
		return result;
	}
}
