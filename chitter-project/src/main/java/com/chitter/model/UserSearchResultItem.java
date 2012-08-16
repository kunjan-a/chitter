package com.chitter.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: kunjan
 * Date: 14/8/12
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserSearchResultItem {
    private String label;
    private String value;
    private String imagePath;

    public static final RowMapper<UserSearchResultItem> rowMapper = new RowMapper<UserSearchResultItem>() {
        @Override
        public UserSearchResultItem mapRow(ResultSet resultSet, int i) throws SQLException {
            return new UserSearchResultItem(resultSet);
        }
    };

    public UserSearchResultItem(ResultSet rs) throws SQLException {
        label = rs.getString("name");
        value = rs.getString("id");
        imagePath = UserItem.PROFILE_PIC_PATH + value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getImagePath() {
        return imagePath;
    }
}
