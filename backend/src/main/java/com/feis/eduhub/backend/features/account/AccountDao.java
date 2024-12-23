package com.feis.eduhub.backend.features.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.feis.eduhub.backend.common.interfaces.ModelDao;
import com.feis.eduhub.backend.common.lib.Sql;

public class AccountDao implements ModelDao<Account> {
    private final String TABLE_NAME = "account";

    @Override
    public Optional<Account> findById(long id, Connection conn) throws SQLException {
        String query = String.format("SELECT * FROM \"%s\" WHERE account_id = ?", TABLE_NAME);
        PreparedStatement ps = conn.prepareStatement(query);
        Sql.setParams(ps, Arrays.asList(id));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return Optional.of(getTableData(rs));
        }
        return Optional.empty();
    }

    @Override
    public List<Account> getAll(Connection conn) throws SQLException {
        List<Account> accountsList = new ArrayList<>();
        String query = String.format("SELECT * FROM \"%s\"", TABLE_NAME);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            accountsList.add(getTableData(rs));
        }
        return accountsList;
    }

    @Override
    public Account create(Account account, Connection conn) throws SQLException {
        String query = String.format(
                "INSERT INTO \"%s\" (first_name, last_name, icon, role_id) VALUES (?, ?, ?, ?)",
                TABLE_NAME);
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        Sql.setParams(ps, Arrays.asList(account.getFirstName(), account.getLastName(), account.getIcon(),
                account.getRoleId()));
        ps.executeUpdate();
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (!generatedKeys.next()) {
            throw new RuntimeException("User not created");
        }
        account.setAccountId(generatedKeys.getLong(1));
        return account;
    }

    @Override
    public void update(long id, Account account, Connection conn) throws SQLException {
        String query = String.format(
                "UPDATE \"%s\" SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), icon = COALESCE(?, icon), role_id = COALESCE(?, role_id) WHERE account_id = ?",
                TABLE_NAME);
        PreparedStatement ps = conn.prepareStatement(query);
        Sql.setParams(ps, Arrays.asList(account.getFirstName(),
                account.getLastName(), account.getIcon(),
                account.getRoleId(), id));
        ps.executeUpdate();
    }

    @Override
    public void delete(long id, Connection conn) throws SQLException {
        String query = String.format("DELETE FROM \"%s\" WHERE account_id = ?", TABLE_NAME);
        PreparedStatement ps = conn.prepareStatement(query);
        Sql.setParams(ps, Arrays.asList(id));
        ps.executeUpdate();
    }

    private Account getTableData(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("account_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("icon"),
                rs.getLong("role_id"));
    }
}