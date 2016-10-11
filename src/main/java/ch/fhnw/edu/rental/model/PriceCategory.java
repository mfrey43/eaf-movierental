package ch.fhnw.edu.rental.model;


public abstract class PriceCategory {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public abstract double getCharge(int daysRented);

	public int getFrequentRenterPoints(int daysRented) {
		return 1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PriceCategory that = (PriceCategory) o;

		return this.getClass() == that.getClass();

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
