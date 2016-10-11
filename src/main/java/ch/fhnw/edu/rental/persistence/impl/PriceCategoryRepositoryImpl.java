package ch.fhnw.edu.rental.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ch.fhnw.edu.rental.model.PriceCategory;
import ch.fhnw.edu.rental.model.PriceCategoryChildren;
import ch.fhnw.edu.rental.model.PriceCategoryNewRelease;
import ch.fhnw.edu.rental.model.PriceCategoryRegular;
import ch.fhnw.edu.rental.persistence.PriceCategoryRepository;

@Component
public class PriceCategoryRepositoryImpl implements PriceCategoryRepository {

    @Autowired
    private JdbcTemplate template;

    @Override
    public PriceCategory findOne(Long id) {
        if (id == null) throw new IllegalArgumentException();

        Map<String, Object> res = template.queryForMap("select * from PRICECATEGORIES where PRICECATEGORY_ID = ?", id);
        PriceCategory pc = parseCategory((String) res.get("PRICECATEGORY_TYPE"));
        pc.setId((Long) res.get("PRICECATEGORY_ID"));

        return pc;
    }

    @Override
    public List<PriceCategory> findAll() {
        return template.query("select * from PRICECATEGORIES",
                (rs, row) -> {
                    PriceCategory pc = parseCategory(rs.getString("PRICECATEGORY_TYPE"));
                    pc.setId(rs.getLong("PRICECATEGORY_ID"));
                    return pc;
                });
    }

    @Override
    public PriceCategory save(PriceCategory category) {
        return category;
    }

    @Override
    public void delete(PriceCategory priceCategory) {
        if (priceCategory == null) throw new IllegalArgumentException();
        priceCategory.setId(null);
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
        return template.queryForObject("SELECT COUNT(*) FROM PRICECATEGORIES", Long.class);
    }

    private static PriceCategory parseCategory(String pcType) {
        switch (pcType) {
            case "Children":
                return new PriceCategoryChildren();
            case "NewRelease":
                return new PriceCategoryNewRelease();
            case "Regular":
            default:
                return new PriceCategoryRegular();
        }
    }

}