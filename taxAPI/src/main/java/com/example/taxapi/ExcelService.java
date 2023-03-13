package com.example.taxapi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExcelService {

    public ResourceDTO export(List<Company> companyList) {
        Resource resource=prepareExcel(companyList);
        return ResourceDTO.builder().resource(resource).mediaType(MediaType.parseMediaType
                        ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).build();
    }

    private Resource prepareExcel(List<Company> companyList){
        Workbook workbook=new XSSFWorkbook();
        Sheet sheet=workbook.createSheet("Companies");

        prepareHeaders(workbook,sheet,"Link","Mã số thuế","Tên","Số điện thoại","Đại diện"
                ,"Địa chỉ","Ngày cấp","Nganh nghề chính");
        populateUserData(workbook,sheet,companyList);

        try(ByteArrayOutputStream byteArrayOutputStream
                    =new ByteArrayOutputStream()){

            workbook.write(byteArrayOutputStream);
            return new
                    ByteArrayResource
                    (byteArrayOutputStream.toByteArray());
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException
                    ("Error while generating excel.");
        }
    }

    private void populateUserData(Workbook workbook, Sheet sheet,
                                  List<Company> companyList) {

        int rowNo=1;
        Font font=workbook.createFont();
        font.setFontName("Arial");

        CellStyle cellStyle=workbook.createCellStyle();
        cellStyle.setFont(font);

        for(Company company: companyList){
            int columnNo=0;
            Row row=sheet.createRow(rowNo);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getLink()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getTaxCode()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getTen()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getdThoai()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getDaiDien()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getDiaChi()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getNgayCap()),cellStyle);
            populateCell(sheet,row,columnNo++,
                    String.valueOf(company.getNganhNgheChinh()),cellStyle);
            rowNo++;
        }
    }

    private void populateCell(Sheet sheet,Row row,int columnNo,
                              String value,CellStyle cellStyle){

        Cell cell=row.createCell(columnNo);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        sheet.autoSizeColumn(columnNo);
    }

    private void prepareHeaders(Workbook workbook,
                                Sheet sheet, String... headers) {

        Row headerRow=sheet.createRow(0);
        Font font=workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");

        CellStyle cellStyle=workbook.createCellStyle();
        cellStyle.setFont(font);

        int columnNo=0;
        for(String header:headers){
            Cell headerCell=headerRow.createCell(columnNo++);
            headerCell.setCellValue(header);
            headerCell.setCellStyle(cellStyle);
        }
    }
}
