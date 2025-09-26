package com.xinyirun.scm.ai.bean.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AiUserQuotaExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AiUserQuotaExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(String value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(String value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLike(String value) {
            addCriterion("user_id like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotLike(String value) {
            addCriterion("user_id not like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<String> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<String> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andTenantIsNull() {
            addCriterion("tenant is null");
            return (Criteria) this;
        }

        public Criteria andTenantIsNotNull() {
            addCriterion("tenant is not null");
            return (Criteria) this;
        }

        public Criteria andTenantEqualTo(String value) {
            addCriterion("tenant =", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotEqualTo(String value) {
            addCriterion("tenant <>", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantLike(String value) {
            addCriterion("tenant like", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotLike(String value) {
            addCriterion("tenant not like", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantIn(List<String> values) {
            addCriterion("tenant in", values, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotIn(List<String> values) {
            addCriterion("tenant not in", values, "tenant");
            return (Criteria) this;
        }

        public Criteria andDailyLimitIsNull() {
            addCriterion("daily_limit is null");
            return (Criteria) this;
        }

        public Criteria andDailyLimitIsNotNull() {
            addCriterion("daily_limit is not null");
            return (Criteria) this;
        }

        public Criteria andDailyLimitEqualTo(Long value) {
            addCriterion("daily_limit =", value, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitNotEqualTo(Long value) {
            addCriterion("daily_limit <>", value, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitGreaterThan(Long value) {
            addCriterion("daily_limit >", value, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitGreaterThanOrEqualTo(Long value) {
            addCriterion("daily_limit >=", value, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitLessThan(Long value) {
            addCriterion("daily_limit <", value, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitLessThanOrEqualTo(Long value) {
            addCriterion("daily_limit <=", value, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitIn(List<Long> values) {
            addCriterion("daily_limit in", values, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitNotIn(List<Long> values) {
            addCriterion("daily_limit not in", values, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitBetween(Long value1, Long value2) {
            addCriterion("daily_limit between", value1, value2, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyLimitNotBetween(Long value1, Long value2) {
            addCriterion("daily_limit not between", value1, value2, "dailyLimit");
            return (Criteria) this;
        }

        public Criteria andDailyUsedIsNull() {
            addCriterion("daily_used is null");
            return (Criteria) this;
        }

        public Criteria andDailyUsedIsNotNull() {
            addCriterion("daily_used is not null");
            return (Criteria) this;
        }

        public Criteria andDailyUsedEqualTo(Long value) {
            addCriterion("daily_used =", value, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedNotEqualTo(Long value) {
            addCriterion("daily_used <>", value, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedGreaterThan(Long value) {
            addCriterion("daily_used >", value, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedGreaterThanOrEqualTo(Long value) {
            addCriterion("daily_used >=", value, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedLessThan(Long value) {
            addCriterion("daily_used <", value, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedLessThanOrEqualTo(Long value) {
            addCriterion("daily_used <=", value, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedIn(List<Long> values) {
            addCriterion("daily_used in", values, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedNotIn(List<Long> values) {
            addCriterion("daily_used not in", values, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedBetween(Long value1, Long value2) {
            addCriterion("daily_used between", value1, value2, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andDailyUsedNotBetween(Long value1, Long value2) {
            addCriterion("daily_used not between", value1, value2, "dailyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitIsNull() {
            addCriterion("monthly_limit is null");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitIsNotNull() {
            addCriterion("monthly_limit is not null");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitEqualTo(Long value) {
            addCriterion("monthly_limit =", value, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitNotEqualTo(Long value) {
            addCriterion("monthly_limit <>", value, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitGreaterThan(Long value) {
            addCriterion("monthly_limit >", value, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitGreaterThanOrEqualTo(Long value) {
            addCriterion("monthly_limit >=", value, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitLessThan(Long value) {
            addCriterion("monthly_limit <", value, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitLessThanOrEqualTo(Long value) {
            addCriterion("monthly_limit <=", value, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitIn(List<Long> values) {
            addCriterion("monthly_limit in", values, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitNotIn(List<Long> values) {
            addCriterion("monthly_limit not in", values, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitBetween(Long value1, Long value2) {
            addCriterion("monthly_limit between", value1, value2, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyLimitNotBetween(Long value1, Long value2) {
            addCriterion("monthly_limit not between", value1, value2, "monthlyLimit");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedIsNull() {
            addCriterion("monthly_used is null");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedIsNotNull() {
            addCriterion("monthly_used is not null");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedEqualTo(Long value) {
            addCriterion("monthly_used =", value, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedNotEqualTo(Long value) {
            addCriterion("monthly_used <>", value, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedGreaterThan(Long value) {
            addCriterion("monthly_used >", value, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedGreaterThanOrEqualTo(Long value) {
            addCriterion("monthly_used >=", value, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedLessThan(Long value) {
            addCriterion("monthly_used <", value, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedLessThanOrEqualTo(Long value) {
            addCriterion("monthly_used <=", value, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedIn(List<Long> values) {
            addCriterion("monthly_used in", values, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedNotIn(List<Long> values) {
            addCriterion("monthly_used not in", values, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedBetween(Long value1, Long value2) {
            addCriterion("monthly_used between", value1, value2, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andMonthlyUsedNotBetween(Long value1, Long value2) {
            addCriterion("monthly_used not between", value1, value2, "monthlyUsed");
            return (Criteria) this;
        }

        public Criteria andTotalCostIsNull() {
            addCriterion("total_cost is null");
            return (Criteria) this;
        }

        public Criteria andTotalCostIsNotNull() {
            addCriterion("total_cost is not null");
            return (Criteria) this;
        }

        public Criteria andTotalCostEqualTo(BigDecimal value) {
            addCriterion("total_cost =", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostNotEqualTo(BigDecimal value) {
            addCriterion("total_cost <>", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostGreaterThan(BigDecimal value) {
            addCriterion("total_cost >", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_cost >=", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostLessThan(BigDecimal value) {
            addCriterion("total_cost <", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_cost <=", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostIn(List<BigDecimal> values) {
            addCriterion("total_cost in", values, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostNotIn(List<BigDecimal> values) {
            addCriterion("total_cost not in", values, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_cost between", value1, value2, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_cost not between", value1, value2, "totalCost");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(LocalDateTime value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(LocalDateTime value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(LocalDateTime value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<LocalDateTime> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(LocalDateTime value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(LocalDateTime value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(LocalDateTime value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<LocalDateTime> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}