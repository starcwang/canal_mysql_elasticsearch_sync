package com.star.sync.elasticsearch.model;

import com.google.common.base.Objects;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-26 22:55:00
 */
public class DatabaseTableModel {
    private String database;
    private String table;

    public DatabaseTableModel() {
    }

    public DatabaseTableModel(String database, String table) {
        this.database = database;
        this.table = table;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseTableModel that = (DatabaseTableModel) o;
        return Objects.equal(database, that.database) &&
                Objects.equal(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(database, table);
    }
}
