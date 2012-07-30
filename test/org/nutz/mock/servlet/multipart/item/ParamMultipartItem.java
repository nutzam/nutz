package org.nutz.mock.servlet.multipart.item;

import org.nutz.mock.servlet.multipart.MultipartItem;
import org.nutz.mock.servlet.multipart.inputing.InputingHelper;

public class ParamMultipartItem extends MultipartItem {

    public ParamMultipartItem(InputingHelper helper, String boundary, String name, String value) {
        super(helper, boundary + "\r\n");
        addInputing(helper.name(name));
        addInputing(helper.blankLine());
        addInputing(helper.data(value));
        addInputing(helper.blankLine());
    }

}
