package br.com.ipdiscovery.view.table;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import br.com.ipdiscovery.bean.Result;
import br.com.ipdiscovery.constant.Status;

/**
 * 
 * @author victor.bello
 *
 *         Modelo para utilização de JTable com os resultados da busca por IP
 */
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

	public class TableStyle extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -3345461648055531075L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int col) {
			JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			Color foreGroundColor = Color.BLACK;
			if (value instanceof Status) {
				Status status = (Status) value;
				switch (status) {
				case BLOCKED:
					foreGroundColor = Color.decode("#ff0000");
					break;
				case FREE:
					foreGroundColor = Color.decode("#0dab00");
					break;
				case TESTING:
					foreGroundColor = Color.decode("#0056ab");
					break;
				case TIMEOUT:
					foreGroundColor = Color.decode("#f07011");
					break;
				}
			}
			l.setForeground(foreGroundColor);
			return l;
		}
	}

}
