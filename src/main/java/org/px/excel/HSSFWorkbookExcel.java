package org.px.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.px.excel.core.PxExcel;

/**
 * @author lgy
 * @date 2022-05-27 16:03
 */
public class HSSFWorkbookExcel extends PxExcel {

    @Override
    public void createWb() {
        super.wb = new HSSFWorkbook();
    }

    public Sheet createSheet(){
        return this.wb.createSheet();
    }

    public Sheet createSheet(String name){
        return this.wb.createSheet(name);
    }

}
