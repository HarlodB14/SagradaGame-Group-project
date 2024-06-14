package DAL;

import java.sql.SQLException;
import java.util.List;

public interface IDAO<T> {

    T get(int id) throws SQLException;

    List<T> getAll() throws SQLException;

    int save(T type) throws SQLException;

    int insert(T type) throws SQLException;

    int update(T type) throws SQLException;

    int delete(T type) throws SQLException;

}
