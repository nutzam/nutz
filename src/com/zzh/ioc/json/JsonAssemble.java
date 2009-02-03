package com.zzh.ioc.json;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import com.zzh.ioc.Assemble;
import com.zzh.ioc.Mapping;
import com.zzh.json.Json;
import com.zzh.lang.Lang;

public class JsonAssemble implements Assemble {

	public JsonAssemble(File home) {
		this.home = home;
	}

	private File home;

	@SuppressWarnings("unchecked")
	@Override
	public Mapping getMapping(String name) {
		File file = new File(home.getAbsolutePath() + "/" + name + ".obj");
		if (!file.exists())
			return null;

		try {
			Map<String, Object> map = (Map<String, Object>) Json.fromJson(new BufferedInputStream(
					new FileInputStream(file)));
			return new ObjectMapping(name, map);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
