package com.test.t3.difftable;

public class TableStructure {
	private String colName;
	private String colType;
	private int colLength;
	private int isNullable;

	public int getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(int isNullable) {
		this.isNullable = isNullable;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public int getColLength() {
		return colLength;
	}

	public void setColLength(int colLength) {
		this.colLength = colLength;
	}
}