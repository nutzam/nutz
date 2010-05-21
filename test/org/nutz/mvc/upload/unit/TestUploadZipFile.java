package org.nutz.mvc.upload.unit;

import static java.lang.System.out;
import static org.nutz.mock.Mock.servlet.context;
import static org.nutz.mock.Mock.servlet.request;
import static org.nutz.mock.Mock.servlet.session;

import java.io.File;
import java.io.FileNotFoundException;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.upload.UploadException;
import org.nutz.mvc.upload.UploadUnit;
import org.nutz.mvc.upload.Uploading;
import org.nutz.trans.Atom;

public class TestUploadZipFile {

	public static void main(String[] args) throws FileNotFoundException {

		final Uploading up = UploadUnit.TYPE.born(8192);
		final FilePool tmps = new NutFilePool("~/nutz/junit/uploadtmp");
		final String charset = "UTF-8";

		File file = Files.findFile("org/nutz/mvc/upload/unit/ZipInputStream");
		MockZipInputStream ins = new MockZipInputStream(file);
		final MockHttpServletRequest req = request().setInputStream(ins);

		req.setSession(session(context()));
		req.init();

		req.setHeader(	"content-type",
						"multipart/form-data; boundary=----ESDT-32127434913589624b3fb6ad335a");
		req.setHeader("content-length", file.length());

		out.println("Begin...");
		Stopwatch sw = Stopwatch.run(new Atom() {
			public void run() {
				try {
					up.parse(req, charset, tmps);
				}
				catch (UploadException e) {
					throw Lang.wrapThrow(e);
				}
			}
		});
		out.println("\n...Done!");
		out.println(sw);
	}

}
