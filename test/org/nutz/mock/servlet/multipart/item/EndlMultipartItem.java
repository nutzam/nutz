package org.nutz.mock.servlet.multipart.item;

import org.nutz.mock.servlet.multipart.MultipartItem;
import org.nutz.mock.servlet.multipart.inputing.InputingHelper;
import org.nutz.mock.servlet.multipart.inputing.StreamEndlInputing;

public class EndlMultipartItem extends MultipartItem {

    public EndlMultipartItem(InputingHelper helper, String boundary) {
        super(helper, boundary);
        this.addInputing(new StreamEndlInputing());
    }

}
