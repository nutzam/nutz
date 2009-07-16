package org.nutz.app;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.castor.Castors;
import org.nutz.app.output.SimpleOutput;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import static java.lang.String.*;

public class Application {

	public static final String[] LANGS = { "zh_CN", "en" };

	public Application(String prompt, String appName) {
		this.appName = appName;
		this.prompt = prompt;
		this.clientName = "me";
		this.localization = initLocalization(Files.findFile("localization"));
		cmds = new HashMap<String, Mirror<? extends Cmd>>();
		vars = new HashMap<String, Object>();
		lang = LANGS[0];
		output = new SimpleOutput();
		addCmd("\\q", QuitCmd.class);
		addCmd("exit", QuitCmd.class);
		addCmd("quite", QuitCmd.class);
		addCmd("?", HelpCmd.class);
		addCmd("help", HelpCmd.class);
		addCmd("lang", LangCmd.class);
		addCmd("batch", BatchCmd.class);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> initLocalization(File dir) {
		Map<String, String> map = new HashMap<String, String>();
		if (null != dir && dir.isDirectory()) {
			File[] fs = dir.listFiles();
			for (File f : fs) {
				Reader reader = Streams.fileInr(f);
				Map<? extends String, ? extends String> msgs = (Map<? extends String, ? extends String>) Json
						.fromJson(reader);
				if (null != msgs)
					map.putAll(msgs);
				try {
					reader.close();
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		Map<String, String> map2 = new HashMap<String, String>();
		for (String key : map.keySet()) {
			map2.put(Strings.trim(key.toLowerCase()), Strings.trim(map.get(key)));
		}
		return map2;
	}

	private String prompt;
	private String clientName;
	private String appName;
	Map<String, Mirror<? extends Cmd>> cmds;
	private String lang;
	private Output output;
	private Map<String, Object> vars;
	private Pattern watchInput;
	private Pattern ignoreInput;
	private List<String> batchCommands;
	private Map<String, String> localization;

	public Application duplicate() {
		Application newapp = Mirror.me(this.getClass()).born(prompt, appName);
		newapp.clientName = clientName;
		newapp.cmds = new HashMap<String, Mirror<? extends Cmd>>();
		newapp.cmds.putAll(cmds);
		newapp.vars = new HashMap<String, Object>();
		newapp.vars.putAll(vars);
		newapp.output = new SimpleOutput();
		newapp.lang = lang;
		return newapp;
	}

	public Application clearBatch() {
		batchCommands = null;
		return this;
	}

	public Application setBatch(String[] cmds) {
		batchCommands = new LinkedList<String>();
		for (String s : cmds)
			batchCommands.add(s);
		return this;
	}

	public void setWatchInput(String regex) {
		this.watchInput = Pattern.compile(regex);
	}

	public void setIgnoreInput(String regex) {
		this.ignoreInput = Pattern.compile(regex);
	}

	private String promptTemplate() {
		return "[%" + Math.max(appName.length(), clientName.length()) + "s]: ";
	}

	private String clientPrompt() {
		return format(promptTemplate(), clientName);
	}

	private String appPrompt() {
		return format(promptTemplate(), appName);
	}

	public Application setPrompt(String prompt) {
		this.prompt = prompt;
		return this;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getLang() {
		return lang;
	}

	public Application setLang(String lang) {
		if (!Lang.contains(LANGS, lang)) {
			printlnf("Unknow lang '%s'!", lang);
			printlnf("Only support [%s]", Json.toJson(LANGS, JsonFormat.compact()));
			return this;
		}
		this.lang = lang;
		return this;
	}

	public <T> T getVar(Class<T> classOfT, String name) {
		Object var = vars.get(name);
		if (null == var)
			return null;
		return Castors.me().castTo(var, classOfT);
	}

	public Application setVar(String name, Object var) {
		vars.put(name, var);
		return this;
	}

	public Application removeCar(String... names) {
		for (String name : names)
			vars.remove(name);
		return this;
	}

	public Application addCmd(String name, Class<? extends Cmd> cmdType) {
		cmds.put(name, Mirror.me(cmdType));
		return this;
	}

	public Output output() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}

	public Application print(String msg) {
		output.write(msg);
		return this;
	}

	public Application println() {
		return print("\n");
	}

	public Application println(String msg) {
		return print(msg + "\n");
	}

	public Application printlnf(String fmt, Object... args) {
		return println(format(fmt, args));
	}

	public Application printf(String fmt, Object... args) {
		output.write(format(fmt, args));
		return this;
	}

	private String ask(String fmt, Object... args) {
		printf(msg(fmt), args);
		String re = Strings.trim(readLine());
		if (isStop(re))
			throw stopCommand();
		return re;
	}

	private boolean isStop(String re) {
		return "!stop".equalsIgnoreCase(re);
	}

	public void said(String fmt, Object... args) {
		printlnf(appPrompt() + format(msg(fmt), args));
	}

	public String msg(String key) {
		String re = this.localization.get(Strings.trim(key.toLowerCase()));
		if (Strings.isBlank(re))
			return key;
		return re;
	}

	public String askone(String fmt, Object... args) {
		printlnf(appPrompt() + format(msg(fmt), args));
		return this.ask(clientPrompt());
	}

	public int askPositiveInteger(String fmt, Object... args) {
		int re = -1;
		while (re < 0)
			try {
				printlnf(msg(fmt), args);
				String s = this.ask(clientPrompt());
				re = Integer.parseInt(s);
				return re;
			} catch (NumberFormatException e) {}
		return re;
	}

	public String asking(String fmt, Object... args) {
		String answer = null;
		printlnf(appPrompt() + format(msg(fmt), args));
		do {
			answer = this.ask(clientPrompt());
		} while (Strings.isBlank(answer));
		return answer;
	}

	public void list(String title, Collection<?> coll) {
		list(title, coll.toArray(new Object[coll.size()]));
	}

	public void list(String title, Object[] items) {
		if (!Strings.isBlank(title)) {
			println(Strings.dup('=', 50));
			println(msg(title));
			println(Strings.dup('-', 50));
		}
		int i = 0;
		for (Object o : items) {
			printlnf(" %d - %s", i++, msg(o.toString()));
		}
	}

	public int choose(String title, Collection<?> coll) {
		return choose(title, coll.toArray(new Object[coll.size()]));
	}

	public int choose(String title, Object[] items) {
		list(null == title ? "Please choose one item below:" : title, items);
		int re = -1;
		do {
			String anwser;
			if (items.length > 1)
				anwser = ask("Input %d-%d :> ", 0, items.length - 1);
			else
				anwser = ask("Input %d :> ", 0);
			try {
				re = Integer.parseInt(anwser);
			} catch (NumberFormatException e) {}
		} while (re < 0 || re >= items.length);
		return re;
	}

	public String combo(String title, String[] items) {
		return this.combo(title, items, true);
	}

	public String combo(String title, String[] items, boolean acceptBlank) {
		println(Strings.dup('=', 50));
		println(msg(null == title ? "Please choose one item below:" : title));
		println(Strings.dup('-', 50));
		int i = 0;
		for (String s : items) {
			printlnf(" %d - %s", i++, s);
		}
		String anwser;
		do {
			String tip;
			if (items.length > 1)
				tip = "Input %d-%d or any text :> ";
			else
				tip = "Input %d or any text :> ";
			if (acceptBlank)
				anwser = ask(msg(tip), 0, items.length - 1);
			else
				anwser = asking(msg(tip), 0, items.length - 1);
			try {
				int index = Integer.parseInt(anwser);
				if (index < 0 || index >= items.length)
					continue;
				return items[index];
			} catch (NumberFormatException e) {}
			break;
		} while (true);
		return anwser;
	}

	/**
	 * Return a string array some length with fields. Each item is the result
	 * 
	 * @param title
	 *            form title words
	 * @param fields
	 * @return
	 */
	public String[] form(String title, String[] fields) {
		int max = 0;
		for (String f : fields)
			max = Math.max(max, f.length());
		String fmt = "%" + (max + 5) + "s : ";
		println(Strings.dup('=', 50));
		println(msg(null == title ? "Please fill each field of the form:" : title));
		println(Strings.dup('-', 50));
		String[] re = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			re[i] = null;
			while (Strings.isBlank(re[i]))
				re[i] = this.ask(fmt, fields[i]);
		}
		return re;
	}

	/**
	 * Fill the map one by one
	 * 
	 * @param title
	 *            form title words
	 * @param map
	 */
	public void form(String title, Map<String, Object> map) {
		String[] names = map.keySet().toArray(new String[map.size()]);
		String[] values = form(title, names);
		for (int i = 0; i < names.length; i++) {
			map.put(names[i], values[i]);
		}
	}

	public void run() {
		println();
		try {
			while (true) {
				printf(prompt);
				String str = readLine();
				try {
					exec(str);
				} catch (StopCmdMessage e) {
					String s = e.getMessage();
					if (!Strings.isBlank(s))
						this.said(s);
					continue;
				} catch (QuitApplication e) {
					break;
				}
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void exec(String str) throws Exception {
		CmdWrapper cw = parse(str);
		if (!Strings.isBlank(cw.name)) {
			Cmd cmd = getCommand(cw.name);
			if (null != cmd) {
				cmd.exec(cw.params);
			} else {
				printf("Unknow command '%s'\n", cw.name);
			}
		}
	}

	public Cmd getCommand(String name) {
		Mirror<? extends Cmd> mirror = cmds.get(name);
		if (null != mirror)
			return mirror.born(this);
		return null;
	}

	private String readLine() {
		if (null != batchCommands) {
			if (batchCommands.size() > 0) {
				String re = batchCommands.remove(0);
				this.print(re + "\n");
				return re;
			} else {
				throw new QuitApplication();
			}
		}
		StringBuilder sb = new StringBuilder();
		int c;
		try {
			while (-1 != (c = System.in.read())) {
				if (c == 10) {
					writeClientInput(sb.toString());
					return sb.toString();
				}
				sb.append((char) c);
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		writeClientInput(sb.toString());
		return sb.toString();
	}

	private void writeClientInput(String s) {
		if (Strings.isBlank(s))
			return;
		if (null != watchInput) {
			if (watchInput.matcher(s).find())
				output.writeClientInput(s);
			return;
		}
		if (null != ignoreInput)
			if (ignoreInput.matcher(s).find())
				return;
		output.writeClientInput(s);
	}

	public <T extends Cmd> void exec(Class<T> cmdType, String cmdArguments) throws Exception {
		String[] ss = parseString(cmdArguments);
		CmdParams params = parseParams(ss, 0, ss.length);
		Mirror.me(cmdType).born(this).exec(params);
	}

	private static class CmdWrapper {
		private String name;
		private CmdParams params;
	}

	private static String[] parseString(String str) {
		ArrayList<String> ss = new ArrayList<String>(20);
		StringBuilder sb = new StringBuilder();
		if (str != null) {
			char[] chars = str.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == '\'' || c == '"') {
					if (!Strings.isBlank(sb))
						ss.add(Strings.trim(sb).toString());
					sb = new StringBuilder();
					String s = escapingRead(chars, i + 1, c);
					ss.add(s);
					i += s.length() + 2;
				} else if (c <= 32) {
					if (!Strings.isBlank(sb))
						ss.add(Strings.trim(sb).toString());
					sb = new StringBuilder();
				} else {
					sb.append(c);
				}
			}
		}
		if (!Strings.isBlank(sb))
			ss.add(Strings.trim(sb).toString());
		return ss.toArray(new String[ss.size()]);
	}

	private static String escapingRead(char[] chars, int offset, char end) {
		StringBuilder sb = new StringBuilder();
		for (int i = offset; i < chars.length; i++) {
			char c = chars[i];
			if (c == end)
				break;
			if (c == '\\') {
				if (i >= (chars.length - 1)) {
					sb.append(c);
				} else {
					sb.append(chars[++i]);
				}
			} else
				sb.append(c);
		}
		return sb.toString();
	}

	private static CmdWrapper parse(String str) {
		String[] ss = parseString(str);
		CmdWrapper cw = new CmdWrapper();
		if (null == ss || ss.length == 0) {
			cw.name = "";
			cw.params = new CmdParams();
		} else {
			if (ss.length == 1) {
				cw.name = ss[0];
				cw.params = new CmdParams();
			} else {
				cw.name = ss[0];
				cw.params = parseParams(ss, 1, ss.length);
			}
		}
		return cw;
	}

	private static CmdParams parseParams(String[] ss, int beginIndex, int endIndex) {
		CmdParams params = new CmdParams();
		for (int i = beginIndex; i < endIndex; i++) {
			String p = ss[i];
			if (null == p)
				continue;
			p = exuviateQuotes(p);
			if (p.startsWith("-")) {
				if (i >= endIndex)
					throw new RuntimeException(format("param '%s' need a value!", p));
				params.add(p.substring(1), ss[++i]);
			} else {
				params.add(p);
			}
		}
		return params;
	}

	private static final Pattern QUOTEMARK = Pattern.compile("^(['\"][^'\"]*['\"])$");

	private static String exuviateQuotes(String p) {
		if (QUOTEMARK.matcher(p).find())
			return p.substring(1, p.length() - 1);
		return p;
	}

	public StopCmdMessage stopCommand() {
		return new StopCmdMessage();
	}

	public StopCmdMessage stopBecause(String fmt, Object... args) {
		return new StopCmdMessage(format(msg(fmt), args));
	}
}