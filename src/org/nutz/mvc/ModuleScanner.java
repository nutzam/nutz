package org.nutz.mvc;

import java.util.Collection;

public interface ModuleScanner {

    Collection<Class<?>> scan();

}
