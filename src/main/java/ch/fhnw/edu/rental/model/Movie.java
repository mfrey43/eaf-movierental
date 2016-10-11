package ch.fhnw.edu.rental.model;

import java.util.Date;
import java.util.Objects;

public class Movie {
	private Long id;
	
	private final String title;
	private final Date releaseDate;
	private boolean rented;
	private PriceCategory priceCategory;

	public Movie(String title, Date releaseDate, PriceCategory priceCategory) throws NullPointerException {
		if ((title == null) || (releaseDate == null) || (priceCategory == null)) {
			throw new NullPointerException("not all input parameters are set!");
		}
		this.title = title;
		this.releaseDate = releaseDate;
		this.priceCategory = priceCategory;
		this.rented = false;
	}
	
	public PriceCategory getPriceCategory() {
		return priceCategory;
	}

	public void setPriceCategory(PriceCategory priceCategory) {
		this.priceCategory = priceCategory;
	}

	public String getTitle() {
		return title;
	}
	
	public Date getReleaseDate() {
		return releaseDate;
	}
	
	public boolean isRented() {
		return rented;
	}

	public void setRented(boolean rented) {
		this.rented = rented;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Movie movie = (Movie) o;

		if (rented != movie.rented) return false;
		if (id != null && movie.id != null && !id.equals(movie.id)) return false;
		if (title != null ? !title.equals(movie.title) : movie.title != null) return false;
		if (releaseDate != null ? !releaseDate.equals(movie.releaseDate) : movie.releaseDate != null) return false;
		return priceCategory != null ? priceCategory.equals(movie.priceCategory) : movie.priceCategory == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
		result = 31 * result + (rented ? 1 : 0);
		result = 31 * result + (priceCategory != null ? priceCategory.hashCode() : 0);
		return result;
	}
}
