package org.nutz.dom.xls;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.nutz.dom.Cell;
import org.nutz.dom.Dom;
import org.nutz.dom.DomParser;
import org.nutz.dom.Listener;
import org.nutz.dom.Row;
import org.nutz.dom.Table;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class ExcelDomParser extends DomParser {

	// public static void main(String[] args) throws Exception {
	// FileInputStream ins = new FileInputStream(Files.findFile("test.xls"));
	// ExcelDomParser builder = new ExcelDomParser();
	// Dom dom = builder.parse(ins);
	// System.out.println(dom.getBody().toString());
	// ins.close();
	// }

	private Listener<HSSFWorkbook, Dom> bookListener;
	private Listener<HSSFSheet, Table> sheetListener;
	private Listener<HSSFRow, Row> rowListener;
	private Listener<HSSFCell, Cell> cellListener;

	@Override
	public Dom parse(InputStream ins) throws IOException {
		bookListener = this.getListener(HSSFWorkbook.class, Dom.class);
		sheetListener = this.getListener(HSSFSheet.class, Table.class);
		rowListener = this.getListener(HSSFRow.class, Row.class);
		cellListener = this.getListener(HSSFCell.class, Cell.class);
		HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(ins));
		Dom dom = new Dom();
		try {
			for (int i = 0; i < 10; i++) {
				HSSFSheet sheet = wb.getSheetAt(i);
				String name = wb.getSheetName(wb.getSheetIndex(sheet));
				buildTable(dom, sheet, name);
			}
		} catch (IndexOutOfBoundsException e) {}
		dom.body().ready();
		if (null != this.bookListener)
			this.bookListener.handle(wb, dom);
		return dom;
	}

	private Table buildTable(Dom dom, HSSFSheet sheet, String name) {
		Table table = dom.body().create(Table.class);
		int l = sheet.getFirstRowNum();
		int r = sheet.getLastRowNum();
		for (int i = l; i <= r; i++) {
			buildRow(table, sheet.getRow(i));
		}
		table.setTitle(name);
		if (null != sheetListener)
			sheetListener.handle(sheet, table);
		return table;
	}

	private Row buildRow(Table table, HSSFRow hssf) {
		Row row = table.createRow();
		if (null != hssf)
			for (int j = hssf.getFirstCellNum(); j < hssf.getLastCellNum(); j++) {
				buildCell(row, hssf.getCell(j));
			}
		if (null != this.rowListener)
			this.rowListener.handle(hssf, row);
		return row;
	}

	private Cell buildCell(Row row, HSSFCell hssf) {
		String value = null;
		try {
			if (null == hssf)
				value = "";
			else if (HSSFCell.CELL_TYPE_STRING == hssf.getCellType())
				value = hssf.getRichStringCellValue().getString();
			else if (HSSFCell.CELL_TYPE_NUMERIC == hssf.getCellType()) {
				double nv = hssf.getNumericCellValue();
				if (nv == (long) nv)
					value = String.valueOf((long) nv);
				else
					value = String.valueOf(nv);
			} else if (HSSFCell.CELL_TYPE_BLANK == hssf.getCellType())
				value = "";
			else if (HSSFCell.CELL_TYPE_BOOLEAN == hssf.getCellType())
				value = String.valueOf(hssf.getBooleanCellValue());
			else if (HSSFCell.CELL_TYPE_ERROR == hssf.getCellType())
				value = String.valueOf(hssf.getErrorCellValue());
			else if (HSSFCell.CELL_TYPE_FORMULA == hssf.getCellType()) {
				value = String.valueOf(hssf.getCellFormula());
			} else
				value = "";
			Cell cell = row.createCell(value);
			if (null != this.cellListener)
				this.cellListener.handle(hssf, cell);
			return cell;
		} catch (Exception e) {
			throw Lang.makeThrow("Error in row: %d value '%s'", row.getIndex(), value);
		}
	}

	static void dumpObject(Object obj, String name) {
		System.out.printf("\n%s\n%s [%s]\n", Strings.dup('-', 40), obj.getClass().getSimpleName(),
				name);
		Class<?> klass = obj.getClass();
		for (Method m : klass.getMethods()) {
			try {
				if (m.getParameterTypes().length == 0) {
					System.out.printf("%s\t: %s\n", m.getName(), m.invoke(obj));
				}
			} catch (Exception e) {}
		}
	}

}
