package com.geocent.security.audit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bpriest
 */
public abstract class Event {

    private String rawEvent;
    private Date eventTime;
    private String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
    private SimpleDateFormat format;

    public Event(String rawEvent) {
        this.rawEvent = rawEvent;
        format = new SimpleDateFormat(dateTimePattern);
        parseDateTime();
    }

    protected String getRawEvent() {
        return rawEvent;
    }

    private void parseDateTime() {
        //temporary hack for isolating date
        String timeStamp = rawEvent.split(",")[0].toString();
        try {
            this.eventTime = format.parse(timeStamp);
        } catch (ParseException ex) {
            System.out.println("Error parsing date");
        }
    }

    public String getTimeStamp() {
        return format.format(eventTime);
    }
}
