package com.zzh.dom;

import java.util.List;

public class Row extends Node<Table, Cell> {

	public Cell createCell(String value) {
		Cell cell = create(Cell.class);
		cell.createText(value);
		return cell;
	}

	public List<Cell> cells() {
		return this.getChildren(Cell.class);
	}

}
