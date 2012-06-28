package org.nutz.lang;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class ComboException extends RuntimeException {

    public ComboException() {
        list = new LinkedList<Throwable>();
    }

    private List<Throwable> list;

    public ComboException add(Throwable e) {
        list.add(e);
        return this;
    }

    @Override
    public Throwable getCause() {
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public String getLocalizedMessage() {
        StringBuilder sb = new StringBuilder();
        for (Throwable e : list)
            sb.append(e.getLocalizedMessage()).append('\n');
        return sb.toString();
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (Throwable e : list)
            sb.append(e.getMessage()).append('\n');
        return sb.toString();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        List<StackTraceElement> eles = new LinkedList<StackTraceElement>();
        for (Throwable e : list)
            for (StackTraceElement ste : e.getStackTrace())
                eles.add(ste);
        return eles.toArray(new StackTraceElement[eles.size()]);
    }

    @Override
    public void printStackTrace() {
        for (Throwable e : list) {
            e.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        for (Throwable e : list) {
            e.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        for (Throwable e : list) {
            e.printStackTrace(s);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Throwable e : list)
            sb.append(e.toString()).append('\n');
        return sb.toString();
    }

}
