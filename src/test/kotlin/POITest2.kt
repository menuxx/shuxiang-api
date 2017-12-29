
import com.menuxx.code.bean.SXItemCodeExported
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.Test
import java.util.*


class POITest2 {


    @Test
    fun test1() {

        val columns = arrayListOf("code", "batch_id", "status", "export_date", "consume_date")

        val excelFileName = "C:/batch_02.xlsx"// name of excel file

        val sheetName = "01"// name of sheet

        val wb = XSSFWorkbook()
        val sheet = wb.createSheet(sheetName)

        val base62 = Base62()

        // iterating r number of rows
        for (r in 1000..1999) {

            val row = sheet.createRow(r)

            // iterating c number of columns
            for (c in 0..4) {
                val cell = row.createCell(c)
                if ( r == 1000 ) {
                    cell.setCellValue(columns[c])
                } else {
                    if ( c == 0 ) {
                        cell.setCellValue("http://q.nizhuantech.com/" + base62.encodeBase10(r.toLong()))
                    }
                    if ( c == 1 ) {
                        cell.setCellValue("1")
                    }
                    if ( c == 3 ) {
                        cell.setCellValue(SXItemCodeExported.toString())
                    }
                    if ( c == 4 ) {
                        cell.setCellValue(Date())
                    }
                }
//                if ( r == 1 ) {
//                    cell.setCellValue(columns[r])
//                }
//                if ( r == 2 ) {
//                    cell.setCellValue(columns[r])
//                }
//                if ( r == 3 ) {
//                    cell.setCellValue(columns[r])
//                }
//                if ( r == 4 ) {
//                    cell.setCellValue(columns[r])
//                }
//                else {
//
//                }


            }
        }

        val fileOut = FileOutputStream(excelFileName)

        // write this workbook to an Outputstream.
        wb.write(fileOut)
        fileOut.flush()
        fileOut.close()
    }

}