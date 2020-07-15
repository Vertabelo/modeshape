package org.modeshape.sequencer.ddl;

public class DdlDeadline {


    public static class DdlTimeoutException extends  RuntimeException {
    }

    static ThreadLocal<Long> timeout = new ThreadLocal<Long>();


    public static void setTimeout(long millis) {

        System.currentTimeMillis();

        long t = millis + System.currentTimeMillis();
        timeout.set(new Long(t));
    }


    public static void checkTimeout() {

        Long t = timeout.get();

        if (t == null) {
            return;
        }

        if (t.longValue() <  System.currentTimeMillis()) {
            throw new DdlTimeoutException();
        }
    }
}
