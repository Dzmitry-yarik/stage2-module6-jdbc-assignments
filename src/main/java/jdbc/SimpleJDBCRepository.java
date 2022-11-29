package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;
    private CustomDataSource dataSource;

    private static final String createUserSQL = "INSERT INTO users (firstname, lastname, age) VALUES(?, ?, ?)";
    private static final String updateUserSQL = "UPDATE users SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM users WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM users WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM users WHERE name = ?";
    private static final String findAllUserSQL = "SELECT * FROM users";

    public Long createUser(User user) throws SQLException {
        Long id = null;
        try { connection = dataSource.getConnection();
            ps = connection.prepareStatement(createUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, String.valueOf(user.getAge()));
            id = (long) ps.executeUpdate();
        } finally {
            ps.close();
            connection.close();
        }
        return id;
    }

    public User findUserById(Long userId) {
        User user = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setString(1, String.valueOf(userId));
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No result");
            }
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            int age = Integer.parseInt(rs.getString("age"));
            user = new User(userId, firstname, lastname, age);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try { ps.close();
                    connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;

    }

    public User findUserByName(String userName) {
        User user = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No result");
            }
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            int age = Integer.parseInt(rs.getString("age"));
            Long id = Long.parseLong(rs.getString("id"));
            user = new User(id, firstname, lastname, age);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try { ps.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = null;
        try {
            connection = dataSource.getConnection();
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(findAllUserSQL);
            if (!rs.next()) {
                throw new SQLException("No results");
            }
            users = new ArrayList<User>();
            while (rs.next()) {
                Long id = Long.parseLong(rs.getString("id"));
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                int age = Integer.parseInt("age");
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { ps.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public User updateUser(User user) {
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, String.valueOf(user.getAge()));
            ps.setString(4, String.valueOf(user.getId()));
            if (ps.executeUpdate() == 0) throw new SQLException("User doesnt exists");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { ps.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    private void deleteUser(Long userId) {
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setString(1, String.valueOf(userId));
            if (ps.executeUpdate() == 0) {
                throw new SQLException("User doesnt exists");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { ps.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
