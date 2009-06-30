package org.nutz.dom;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class Table extends Node<Element, Row> {

	public Row createRow() {
		return create(Row.class);
	}

	Cell[][] cells = null;
	int rowCount;
	int colCount;

	@Override
	public void ready() {
		rowCount = this.getChildren().size();
		for (Iterator<Row> it = this.getChildren().iterator(); it.hasNext();) {
			List<Cell> cs = it.next().getChildren();
			if (colCount < cs.size())
				colCount = cs.size();
		}
		cells = new Cell[rowCount][colCount];

		for (int y = 0; y < rowCount; y++) {
			cells[y] = new Cell[colCount];
			Row row = getChildren().get(y);
			for (int x = 0; x < colCount; x++) {
				Cell cell = null;
				if (row.getChildren().size() <= x) {
					cell = row.createCell(null);
				} else {
					cell = row.getChildren().get(x);
				}
				cells[y][x] = cell;
			}
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColCount() {
		return colCount;
	}

	public Cell getCell(int row, int col) {
		try {
			return cells[row][col];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Row getRow(int index) {
		if (index < 0 || index >= rowCount)
			return null;
		try {
			return this.getChildren().get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Cell[] getRowCells(int index) {
		if (index < 0 || index >= rowCount)
			return null;
		return cells[index];
	}
	
	public List<Row> rows(){
		return this.getChildren(Row.class);
	}

	public Cell[] getColCells(int index) {
		if (index < 0 || index >= colCount)
			return null;
		Cell[] re = new Cell[rowCount];
		for (int i = 0; i < rowCount; i++) {
			re[i] = cells[i][index];
		}
		return re;
	}

}
