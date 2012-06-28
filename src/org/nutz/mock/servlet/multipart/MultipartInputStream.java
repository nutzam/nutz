package org.nutz.mock.servlet.multipart;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.mock.servlet.MockInputStream;
import org.nutz.mock.servlet.multipart.inputing.InputingHelper;
import org.nutz.mock.servlet.multipart.item.EndlMultipartItem;
import org.nutz.mock.servlet.multipart.item.FileMultipartItem;
import org.nutz.mock.servlet.multipart.item.ParamMultipartItem;

public class MultipartInputStream extends MockInputStream {

    private Map<String, String> mimes;

    private LinkedList<MultipartItem> items;

    private Iterator<MultipartItem> it;

    private MultipartItem current;

    private String boundary;

    private InputingHelper helper;

    public MultipartInputStream(String charset, String boundary) {
        this.boundary = boundary;
        this.helper = new InputingHelper(charset);
        mimes = new HashMap<String, String>();
        addMime("png", "image/png");
        addMime("jpg", "image/jpg");
        addMime("gif", "image/gif");
        addMime("txt", "text/plain");
        items = new LinkedList<MultipartItem>();
        items.add(new EndlMultipartItem(helper, boundary));
    }

    public String getCharset() {
        return helper.getCharset();
    }

    private String getContentType(String suffixName) {
        String ct = mimes.get(suffixName);
        if (null == ct)
            return "application/octet-stream";
        return ct;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public int available() throws IOException {
        int re = 0;
        Iterator<MultipartItem> it = items.iterator();
        while (it.hasNext())
            re += it.next().size();
        return re;
    }

    public MultipartInputStream addMime(String suffix, String contentType) {
        mimes.put(suffix, contentType);
        return this;
    }

    public void append(String name, File f) {
        String contentType = getContentType(Files.getSuffixName(f));
        FileMultipartItem fmi = new FileMultipartItem(helper, boundary, name, f, contentType);
        append(fmi);
    }

    public void append(String name, String value) {
        append(new ParamMultipartItem(helper, boundary, name, value));
    }

    private void append(MultipartItem item) {
        items.add(items.size() - 1, item);
    }

    public int read() throws IOException {
        int d = current.read();
        while (-1 == d) {
            if (!it.hasNext())
                return -1;
            current = it.next();
            d = current.read();
        }
        return d;
    }

    public void init() {
        try {
            for (MultipartItem item : items)
                item.init();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        it = items.iterator();
        current = it.next();
    }

    @Override
    public void close() throws IOException {
        try {
            for (MultipartItem item : items)
                item.close();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
