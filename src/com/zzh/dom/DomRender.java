package com.zzh.dom;

import java.io.IOException;
import java.io.OutputStream;

public abstract class DomRender extends DomBuilder {

	public abstract void render(Dom dom, OutputStream ops) throws IOException;

}
