package org.px.excel.core;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author lgy
 * @date 2022-05-27 15:59
 */
public abstract class PxExcel {

    protected int MAX_ROWS = 65535;

    protected Workbook wb;

    public PxExcel() {

    }

    public abstract void createWb();

    public void setWb(Workbook wb) {
        this.wb = wb;
    }

    public void setMAX_ROWS(int MAX_ROWS) {
        this.MAX_ROWS = MAX_ROWS;
    }
}
