package com.beeftracker.backend.base.query;


public class QueryBuilder {
    private StringBuilder selectClause = new StringBuilder();
    private StringBuilder whereClause = new StringBuilder();
    private StringBuilder orderByClause = new StringBuilder();
    private StringBuilder groupByClause = new StringBuilder();
    private Integer limit;
    private Integer offset;

    public QueryBuilder select(String select) {
        selectClause.append(select);
        return this;
    }

    public QueryBuilder where(String condition) {
        whereClause.append(condition);
        return this;
    }

    public QueryBuilder orderBy(String... columns) {
        if (columns.length > 0) {
            orderByClause.append("ORDER BY ").append(String.join(", ", columns));
        }
        return this;
    }

    public QueryBuilder groupBy(String group) {
        groupByClause.append(group);
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String build() {
        StringBuilder query = new StringBuilder();
        query.append(selectClause).append(" ");
        if (whereClause.length() > 0) {
            query.append(whereClause).append(" ");
        }
        if (groupByClause != null) {
            query.append(groupByClause).append(" ");
        }
        if (orderByClause.length() > 0) {
            query.append(orderByClause).append(" ");
        }



        if (limit != null) {
            query.append("LIMIT ").append(limit).append(" ");
        }

        if (offset != null) {
            query.append("OFFSET ").append(offset).append(" ");
        }

        return query.toString().trim();
    }
}
