package br.com.ipdiscovery.view.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.ipdiscovery.bean.Result;

public class ResultTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1353130016835005114L;
	private String[] columns;
	private List<Result> data = new ArrayList<Result>();

	public ResultTableModel(List<Result> data, String... columns) {
		this.data = data;
		this.columns = columns;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Result result = data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return result.getIp();
		case 1:
			return result.getSearchType();
		case 2:
			return result.getStatus();
		}

		return null;
	}

	public void addRowOrEdit(Result result) {
		if (data.contains(result)) {
			int row = data.indexOf(result);
			data.remove(result);
			data.add(result);
			fireTableCellUpdated(row, 1);
			fireTableCellUpdated(row, 2);
		} else {
			data.add(result);
			int linha = data.size() - 1;
			fireTableRowsInserted(linha, linha);
		}
	}

	public String getColumnName(int col) {
		return columns[col];
	}
}
