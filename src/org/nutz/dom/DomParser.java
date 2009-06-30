package org.nutz.dom;

import java.io.IOException;
import java.io.InputStream;

public abstract class DomParser extends DomBuilder {

	public abstract Dom parse(InputStream ins) throws IOException;

}
