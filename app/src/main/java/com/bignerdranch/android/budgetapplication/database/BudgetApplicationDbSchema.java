package com.bignerdranch.android.budgetapplication.database;

public class BudgetApplicationDbSchema {
    public static final class ItemTable {
        public static final String NAME = "item";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String VALUE = "value";
            public static final String QUANTITY = "quantity";
            public static final String CURRENCY = "currency";
        }
    }

    public static final class IncomeTable {
        public static final String NAME = "income";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String VALUE = "value";
        }
    }
}
