package org.nutz.lang.tmpl;

import org.nutz.lang.Strings;

class StrReplaceConvertor implements StrEleConvertor {

    private String[] args;

    StrReplaceConvertor(String input) {
        args = Strings.split(input, false, ',');
    }

    @Override
    public String process(String str) {
        if (args == null || args.length == 0) {
            return str;
        }
        if (args.length == 1) {
            return str.replaceAll(args[0], "");
        }
        return str.replaceAll(args[0], args[1]);
    }

}
