package org.nutz.http;

public interface SenderFactory {

    Sender create(Request request);
}