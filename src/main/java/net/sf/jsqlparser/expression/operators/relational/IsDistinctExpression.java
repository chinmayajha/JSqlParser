/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class IsDistinctExpression extends BinaryExpression implements SupportsOldOracleJoinSyntax {

    private boolean not = false;
    /**
     * SQLite spelling: {@code a IS b} for IS NOT DISTINCT FROM, {@code a IS NOT b} for IS DISTINCT
     * FROM.
     */
    private boolean useIsOperator = false;

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean b) {
        not = b;
    }

    public IsDistinctExpression withNot(boolean not) {
        this.setNot(not);
        return this;
    }

    public boolean isUseIsOperator() {
        return useIsOperator;
    }

    public void setUseIsOperator(boolean useIsOperator) {
        this.useIsOperator = useIsOperator;
    }

    public IsDistinctExpression withUseIsOperator(boolean useIsOperator) {
        this.setUseIsOperator(useIsOperator);
        return this;
    }

    @Override
    public <T, S> T accept(ExpressionVisitor<T> expressionVisitor, S context) {
        return expressionVisitor.visit(this, context);
    }

    @Override
    public String getStringExpression() {
        if (useIsOperator) {
            return isNot() ? " IS " : " IS NOT ";
        }
        return " IS " + (isNot() ? "NOT " : "") + "DISTINCT FROM ";
    }

    @Override
    public String toString() {
        String retval = (oraclePriorPosition == ORACLE_PRIOR_START ? "PRIOR " : "")
                + getLeftExpression() + getStringExpression() + getRightExpression();
        return retval;
    }

    private int oraclePriorPosition = NO_ORACLE_PRIOR;

    @Override
    public int getOldOracleJoinSyntax() {
        return NO_ORACLE_JOIN;
    }

    @Override
    public void setOldOracleJoinSyntax(int oldOracleJoinSyntax) {
        throw new IllegalArgumentException(
                "oracle join operator (+) is not supported on this condition");
    }

    @Override
    public IsDistinctExpression withOldOracleJoinSyntax(int oldOracleJoinSyntax) {
        setOldOracleJoinSyntax(oldOracleJoinSyntax);
        return this;
    }

    @Override
    public int getOraclePriorPosition() {
        return oraclePriorPosition;
    }

    @Override
    public void setOraclePriorPosition(int oraclePriorPosition) {
        this.oraclePriorPosition = oraclePriorPosition;
    }

    public IsDistinctExpression withOraclePriorPosition(int oraclePriorPosition) {
        setOraclePriorPosition(oraclePriorPosition);
        return this;
    }

}
