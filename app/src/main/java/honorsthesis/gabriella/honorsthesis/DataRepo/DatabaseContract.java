package honorsthesis.gabriella.honorsthesis.DataRepo;

import android.provider.BaseColumns;

/**
 * Created by Gabriella on 3/22/2017.
 */
public final class DatabaseContract {

    private DatabaseContract(){}

    /* Inner class that defines the table contents */
    public static class List implements BaseColumns {
        public static final String TABLE_NAME = "list";
        public static final String COLUMN_NAME = "name";
    }

    /* Inner class that defines the table contents */
    public static class Task implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PARENT_TASK = "parent_task";
        public static final String COLUMN_PARENT_LIST = "parent_list";
    }

    /* Inner class that defines the table contents */
    public static class Step implements BaseColumns {
        public static final String TABLE_NAME = "step";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_PARENT_PROCESS = "parent";
    }

    /* Inner class that defines the table contents */
    public static class Process implements BaseColumns {
        public static final String TABLE_NAME = "process";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_PARENT_LIST = "parent_list";

    }
}
