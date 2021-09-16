package com.sn.app.db.data.schedule;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.app.db.data.base.AppBean;

/**
 * Author:Created by 泽鑫 on 2018/2/2 15:31.
 */

@DatabaseTable(tableName = ScheduleBean.TABLE_NAME)
public class ScheduleBean extends AppBean {

    public static final String TABLE_NAME = "ScheduleBean";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_READ = "read";

    @DatabaseField(columnName = COLUMN_DATE)
    private String  date;

    @DatabaseField(columnName = COLUMN_CONTENT)
    private String content;

    @DatabaseField(columnName = COLUMN_READ, dataType = DataType.BOOLEAN, defaultValue = "false")
    private boolean read;

    public String  getDate() {
        return date;
    }

    public void setDate(String  date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
