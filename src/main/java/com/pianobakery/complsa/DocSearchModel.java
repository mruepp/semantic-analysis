package com.pianobakery.complsa;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 27.07.15.
 */



public class DocSearchModel extends AbstractTableModel {

    private String[] titles = {"%Similarities:", "Filename:"};
    private List<DocSearchFile> files;

    public DocSearchModel() {
        files = new ArrayList<DocSearchFile>();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex)
        {
            case 0: return Number.class;
            case 1: return String.class;
            default: return null;
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DocSearchFile theFile = getDocSearchFile(rowIndex);

        switch (columnIndex)
        {
            case 0: return theFile.getSimilarity();
            case 1: return theFile.getFileName();
            default: return null;
        }

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DocSearchFile theFile = getDocSearchFile(rowIndex);

        switch (columnIndex)
        {
            case 0: theFile.setSimilarity((String)aValue);break;
            case 1: theFile.setFile((File)aValue);break;

        }
        fireTableCellUpdated(rowIndex,columnIndex);

    }

    public DocSearchFile getDocFile(int rowIndex) {
        return files.get(rowIndex);

    }

    public void addDocFile(DocSearchFile aFile) {
        insertDocFile(getRowCount(), aFile);


    }

    public void insertDocFile(int rowIndex, DocSearchFile theFile) {
        files.add(rowIndex,theFile);
        fireTableRowsInserted(rowIndex, rowIndex);

    }

    public DocSearchFile getDocSearchFile(int row) {
        return files.get(row);
    }

    public void removeDocFile(int rowIndex) {
        files.remove(rowIndex);
        fireTableRowsDeleted(rowIndex,rowIndex);
    }

    public void resetModel() {

        files.clear();

        fireTableDataChanged();



    }





}
