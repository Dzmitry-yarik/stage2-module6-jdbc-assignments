package jdbc;


import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleJDBCRepository {

    private PreparedStatement ps = null;
    private Statement st = null;
    private CustomDataSource dataSource = CustomDataSource.getInstance();
    private Connection connection = dataSource.getConnection();

    private static final String CREATE_USER = """
            INSERT INTO myusers(
            firstname, lastname, age)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_USER = """
            UPDATE myusers
            SET firstname=?, lastname=?, age=?
            WHERE id = ?
            """;
    private static final String DELETE_USER = """
            DELETE FROM myusers
            WHERE id = ?
            """;
    private static final String FIND_USER_BY_ID = """
            SELECT id, firstname, lastname, age FROM myusers
            WHERE id = ?
            """;
    private static final String FIND_USER_BY_NAME = """
            SELECT id, firstname, lastname, age FROM myusers
            WHERE firstname LIKE CONCAT('%',?,'%')
            """;
    private static final String FIND_ALL_USER = """
            SELECT id, firstname, lastname, age FROM myusers
            """;

    public Long createUser(User user) {
        Long id = 0L;
        if (user.getFirstName() == null) user.setFirstName("firstName");
        if (user.getLastName() == null) user.setLastName("lastName");
        if (user.getAge() == 0) user.setAge(1);

        try (PreparedStatement statement = connection.prepareStatement(CREATE_USER)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id+1;
    }

    public User findUserById(Long userId) {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_ID)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = parserUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_NAME)) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = parserUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_USER)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(parserUser(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            statement.setLong(4, user.getId());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            if (statement.executeUpdate() != 0) {
                return findUserById(user.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User parserUser(ResultSet resultSet) {
        long id = 0;
        String firstname = null;
        String lastname = null;
        int age = 0;
        try {
            id = resultSet.getLong("id");
            firstname = resultSet.getString(2);
            lastname = resultSet.getString(3);
            age = resultSet.getInt("age");
        } catch (SQLException e) {
             e.printStackTrace();
        }

        User user = new User(id, firstname, lastname, age);
        return user;
    }
}


