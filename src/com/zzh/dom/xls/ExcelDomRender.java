package com.zzh.dom.xls;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.zzh.dom.Cell;
import com.zzh.dom.Dom;
import com.zzh.dom.DomRender;
import com.zzh.dom.Listener;
import com.zzh.dom.Row;
import com.zzh.dom.Table;

public class ExcelDomRender extends DomRender {

	// public static void main(String[] args) throws Exception {
	// File f = new File("D:/tmp/abc.xls");
	// Files.deleteFile(f);
	// Files.createNewFile(f);
	// OutputStream ops = new FileOutputStream(f);
	// ExcelDomRender render = new ExcelDomRender();
	// Dom dom = new Dom();
	// Table table = dom.getBody().createTable();
	// table.setName("sheet1");
	// Row row = table.createRow();
	// row.createCell("C11");
	// row.createCell("C12");
	// row = table.createRow();
	// row.createCell("C21");
	// row.createCell("C22");
	// dom.getBody().ready();
	// render.render(dom, ops);
	// ops.close();
	// }

	private Listener<HSSFWorkbook, Dom> bookListener;
	private Listener<HSSFSheet, Table> sheetListener;
	private Listener<HSSFRow, Row> rowListener;
	private Listener<HSSFCell, Cell> cellListener;

	@Override
	public void render(Dom dom, OutputStream ops) throws IOException {
		bookListener = this.getListener(HSSFWorkbook.class, Dom.class);
		sheetListener = this.getListener(HSSFSheet.class, Table.class);
		rowListener = this.getListener(HSSFRow.class, Row.class);
		cellListener = this.getListener(HSSFCell.class, Cell.class);
		HSSFWorkbook wb = makeWorkbook(dom);
		wb.write(ops);
	}

	private HSSFWorkbook makeWorkbook(Dom dom) {
		HSSFWorkbook wb = new HSSFWorkbook();
		List<Table> tables = dom.body().getChildren(Table.class);
		for (Iterator<Table> it = tables.iterator(); it.hasNext();) {
			Table tb = it.next();
			makeSheet(wb.createSheet(tb.getTitle()), tb);
		}
		if (null != this.bookListener)
			this.bookListener.handle(wb, dom);
		return wb;
	}

	public void makeSheet(HSSFSheet sheet, Table table) {
		List<Row> rows = table.rows();
		int i = 0;
		for (Iterator<com.zzh.dom.Row> it = rows.iterator(); it.hasNext();) {
			Row row = it.next();
			makeRow(sheet.createRow(i++), row);
		}
		if (null != sheetListener)
			sheetListener.handle(sheet, table);
	}

	private void makeRow(HSSFRow hssf, Row row) {
		List<Cell> cells = row.cells();
		int i = 0;
		for (Iterator<Cell> it = cells.iterator(); it.hasNext();) {
			Cell cell = it.next();
			makeCell(hssf.createCell(i++), cell);
		}
		if (null != this.rowListener)
			this.rowListener.handle(hssf, row);
	}

	private void makeCell(HSSFCell hssf, Cell cell) {
		String txt = cell.getText();
		if (null == txt || "null".equalsIgnoreCase(txt))
			txt = "";
		hssf.setCellValue(new HSSFRichTextString(txt));
		if (null != this.cellListener)
			this.cellListener.handle(hssf, cell);
	}
}
