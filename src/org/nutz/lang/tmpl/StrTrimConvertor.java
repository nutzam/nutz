package org.nutz.lang.tmpl;

class StrTrimConvertor implements StrEleConvertor {

    @Override
    public String process(String str) {
        return str.trim();
    }

}
