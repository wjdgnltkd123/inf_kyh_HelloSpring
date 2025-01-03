package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values (?)";

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, member.getName());
            pstmt.executeUpdate();
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        finally {
            close(conn, pstmt, rs);
        }

        return member;
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (conn != null) {
            close(conn);
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select id, name from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong(1));
                member.setName(rs.getString(2));
                return Optional.of(member);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return Optional.empty();
    }

    private void close(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select id, name from member where name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong(1));
                member.setName(rs.getString(2));
                return Optional.of(member);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        String sql = "select id, name from member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong(1));
                member.setName(rs.getString(2));
                members.add(member);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return members;
    }
}
