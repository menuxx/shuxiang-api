
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import java.io.FileOutputStream
import org.apache.poi.ss.usermodel.Workbook



@Throws(Exception::class)
fun main(args: Array<String>) {

    val wb1 = HSSFWorkbook()
    val fileOut2 = FileOutputStream("workbook.xlsx")
    wb1.write(fileOut2)
    fileOut2.close()

    val wb = HSSFWorkbook() //or new HSSFWorkbook();

    val sheet = wb.createSheet()
    val row = sheet.createRow(2)
    row.heightInPoints = 30F

    createCell(wb, row, 0.toShort(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
    createCell(wb, row, 1.toShort(), HorizontalAlignment.CENTER_SELECTION, VerticalAlignment.BOTTOM)
    createCell(wb, row, 2.toShort(), HorizontalAlignment.FILL, VerticalAlignment.CENTER)
    createCell(wb, row, 3.toShort(), HorizontalAlignment.GENERAL, VerticalAlignment.CENTER)
    createCell(wb, row, 4.toShort(), HorizontalAlignment.JUSTIFY, VerticalAlignment.JUSTIFY)
    createCell(wb, row, 5.toShort(), HorizontalAlignment.LEFT, VerticalAlignment.TOP)
    createCell(wb, row, 6.toShort(), HorizontalAlignment.RIGHT, VerticalAlignment.TOP)

    // Write the output to a file
    val fileOut = FileOutputStream("xssf-align.xlsx")
    wb.write(fileOut)
    fileOut.close()

}

/**
 * Creates a cell and aligns it a certain way.
 *
 * @param wb     the workbook
 * @param row    the row to create the cell in
 * @param column the column number to create the cell in
 * @param halign the horizontal alignment for the cell.
 */
private fun createCell(wb: Workbook, row: Row, column: Short, halign: HorizontalAlignment, valign: VerticalAlignment) {
    val cell = row.createCell(column.toInt())
    cell.setCellValue("Align It")
    val cellStyle = wb.createCellStyle()
    cellStyle.setAlignment(halign)
    cellStyle.setVerticalAlignment(valign)
    cell.cellStyle = cellStyle
}