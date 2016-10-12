package ch.fhnw.edu.rental.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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
        return template.queryForObject("select * from PRICECATEGORIES where PRICECATEGORY_ID = ?", (rs, row) -> createPriceCategory(rs), id);
    }

    @Override
    public List<PriceCategory> findAll() {
        return template.query("select * from PRICECATEGORIES",
                (rs, row) -> createPriceCategory(rs));
    }

    @Override
    public PriceCategory save(PriceCategory category) {
        if(category.getId() != null && exists(category.getId())){
            template.update("UPDATE PRICECATEGORIES SET PRICECATEGORY_TYPE=? where PRICECATEGORY_ID=?",
                    category.toString(), category.getId()
            );
        }else{
            SimpleJdbcInsert inserter = new SimpleJdbcInsert(template).withTableName("PRICECATEGORIES").usingGeneratedKeyColumns("PRICECATEGORY_ID");

            Map<String, Object> parameters = new HashMap<>(1);
            parameters.put("PRICECATEGORY_TYPE", category.toString());

            Number newId = inserter.executeAndReturnKey(parameters);
            category.setId((Long)newId);

        }

        return category;
    }

    @Override
    public void delete(PriceCategory priceCategory) {
        if (priceCategory == null) throw new IllegalArgumentException();

        template.update("DELETE FROM PRICECATEGORIES WHERE PRICECATEGORY_ID=?", priceCategory.getId());
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

    private PriceCategory createPriceCategory(ResultSet rs) throws SQLException {
        PriceCategory pc;
        switch (rs.getString("PRICECATEGORY_TYPE")) {
            case "Children":
                pc = new PriceCategoryChildren();
                break;
            case "NewRelease":
                pc = new PriceCategoryNewRelease();
                break;
            case "Regular":
            default:
                pc = new PriceCategoryRegular();
                break;
        }
        pc.setId(rs.getLong("PRICECATEGORY_ID"));
        return pc;
    }
}
