/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2026 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression.operators.relational;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.parser.AbstractJSqlParser.Dialect;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IsDistinctExpressionTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM t WHERE a IS 1",
            "SELECT * FROM t WHERE a IS NOT b",
            "SELECT * FROM t WHERE a IS ? AND b IS NOT 'x'",
            "SELECT a IS b, a IS NOT NULL, a IS TRUE FROM t",
            "SELECT * FROM t WHERE a IS NOT DISTINCT FROM b"
    })
    void testSqliteIsOperator(String sqlStr) throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed(sqlStr, true,
                parser -> parser.withDialect(Dialect.SQLITE));
    }

    @Test
    void testSqliteIsOperatorMeansNotDistinct() throws JSQLParserException {
        assertTrue(((IsDistinctExpression) parseSqlite("a IS 1")).isNot());
        assertFalse(((IsDistinctExpression) parseSqlite("a IS NOT 1")).isNot());
    }

    @Test
    void testSqliteKeepsOtherIsPredicates() throws JSQLParserException {
        assertInstanceOf(IsNullExpression.class, parseSqlite("a IS NULL"));
        assertInstanceOf(IsNullExpression.class, parseSqlite("a IS NOT NULL"));
        assertInstanceOf(IsBooleanExpression.class, parseSqlite("a IS TRUE"));
        assertInstanceOf(IsUnknownExpression.class, parseSqlite("a IS UNKNOWN"));
        IsDistinctExpression distinct = (IsDistinctExpression) parseSqlite("a IS DISTINCT FROM b");
        assertFalse(distinct.isUseIsOperator());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SELECT * FROM t WHERE a IS 1", "SELECT * FROM t WHERE a IS NOT b"})
    void testIsOperatorNeedsSqliteDialect(String sqlStr) {
        assertThrows(JSQLParserException.class, () -> CCJSqlParserUtil.parse(sqlStr));
    }

    @Test
    void testIsOperatorDeparse() {
        IsDistinctExpression is = new IsDistinctExpression().withUseIsOperator(true).withNot(true);
        is.setLeftExpression(new Column("a"));
        is.setRightExpression(new LongValue(1));
        TestUtils.assertExpressionCanBeDeparsedAs(is, "a IS 1");
        TestUtils.assertExpressionCanBeDeparsedAs(is.withNot(false), "a IS NOT 1");
    }

    private static Expression parseSqlite(String condition) throws JSQLParserException {
        return CCJSqlParserUtil.parseCondExpression(condition, false,
                parser -> parser.withDialect(Dialect.SQLITE));
    }
}
