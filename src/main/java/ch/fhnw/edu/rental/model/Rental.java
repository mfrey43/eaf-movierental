package ch.fhnw.edu.rental.model;

import java.util.Calendar;
import java.util.Date;


public class Rental {
	private Long id;
	
	private Movie movie;
	private User user;
	private Date rentalDate;
	private int rentalDays;

	public Rental(){}

	public Rental(User user, Movie movie, int rentalDays){
		this(user, movie, rentalDays, false);
	}
	
	public Rental(User user, Movie movie, int rentalDays, boolean isSetup) {
		if (user == null || movie == null || rentalDays <= 0) {
			throw new NullPointerException("not all input parameters are set!" + user + "/" + movie + "/" + rentalDays);
		}
		if (movie.isRented() && !isSetup) {
			throw new IllegalStateException("movie is already rented!");
		}
		this.user = user;
		user.getRentals().add(this);
		this.movie = movie;
		movie.setRented(true);
		this.rentalDays = rentalDays;
		this.rentalDate = Calendar.getInstance().getTime();
	}
	
	public Rental(User user, Movie movie, int rentalDays, Date rentalDate) {
		this(user, movie, rentalDays, false);
		this.setRentalDate(rentalDate);
	}
	
	public int calcRemainingDaysOfRental(Date date) {
		if (date == null) {
			throw new NullPointerException("given date is not set!");
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(rentalDate);
		calendar.add(Calendar.DAY_OF_YEAR, rentalDays);
		int endDay = calendar.get(Calendar.DAY_OF_YEAR);
		int endYear = calendar.get(Calendar.YEAR);
		calendar.setTime(date);
		int max = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
		int actDay = calendar.get(Calendar.DAY_OF_YEAR);
		int actYear = calendar.get(Calendar.YEAR);
		int diffDay = endDay - actDay;
		if (max!=365) {
			return 366*(endYear-actYear) + diffDay;
		} else {
			return 365*(endYear-actYear) + diffDay;
		}
	}

	public double getRentalFee() {
		return movie.getPriceCategory().getCharge(rentalDays);
	}

	public Long getId() {
		return id;
	}

	public Movie getMovie() {
		return movie;
	}

	public User getUser() {
		return user;
	}

	public Date getRentalDate() {
		return rentalDate;
	}

	public int getRentalDays() {
		return rentalDays;
	}

	public void setId(Long id) {
		this.id = id;
	}	

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setRentalDate(Date rentalDate) {
		this.rentalDate = rentalDate;
	}

	public void setRentalDays(int rentalDays) {
		this.rentalDays = rentalDays;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Rental rental = (Rental) o;

		if (rentalDays != rental.rentalDays) return false;
		if (id != null && rental.id != null && !id.equals(rental.id)) return false;
		if (movie != null ? !movie.equals(rental.movie) : rental.movie != null) return false;
		if (user != null ? !user.equals(rental.user) : rental.user != null) return false;
		return rentalDate != null ? rentalDate.equals(rental.rentalDate) : rental.rentalDate == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (movie != null ? movie.hashCode() : 0);
		result = 31 * result + (user != null ? user.hashCode() : 0);
		result = 31 * result + (rentalDate != null ? rentalDate.hashCode() : 0);
		result = 31 * result + rentalDays;
		return result;
	}
}
