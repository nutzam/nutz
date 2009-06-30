package org.nutz.app;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;

public class LangCmd extends Cmd {

	public LangCmd(Application app) {
		super(app);
	}

	@Override
	public void exec(CmdParams params) throws Exception {
		String lang = params.one();
		if (Strings.isBlank(lang)) {
			app.println(app.getLang());
		} else if ("?".equals(lang)) {
			app.println(Json.toJson(Json.toJson(Application.LANGS, JsonFormat.compact())));
		} else {
			app.setLang(lang);
		}
	}

}
