package com.menuxx.code.service

import com.google.common.collect.Lists
import com.menuxx.code.bean.SXItemCode
import com.menuxx.code.bean.getStatusTxt
import com.menuxx.code.mq.OneBatch
import com.menuxx.code.code.ItemCodeFactory
import com.menuxx.code.db.ItemCodeBatchDb
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

@Service
class CodeBatchService (
        private val codeBatchDb: ItemCodeBatchDb,
        private val itemCodeFactory: ItemCodeFactory
) {

    private val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    // 一个批次由一千个 code 组成，可以不到1000个，但是最多1000个
    private final val OnceBatchUnitCount = 2000

    /**
     * 计算批次数量
     */
    fun calcBatchCount(totalNum: Int) : Array<Int> {
        val count = Math.floor((totalNum / OnceBatchUnitCount).toDouble()).toInt()
        // 计算出 满 数量的的 批次个数
        val fullBatch = (1..count).map {
            OnceBatchUnitCount
        }.toCollection(Lists.newArrayList())
        // 计算最后一个批次的数量
        val leftOver = totalNum - (count * OnceBatchUnitCount)
        // 为0就不加入了
        if ( leftOver > 0 ) {
            fullBatch.add(leftOver)
        }
        return fullBatch.toTypedArray()
    }

    /**
     * 超找批处理系统中最后一次工作产生的编号
     */
    fun doBatchPlan(totalNum: Int) : Array<OneBatch> {
        val lastBatch = codeBatchDb.findLastBatch()
        // 找不到一批，说明是第一次生成，从 0 开始
        var startCode = if (lastBatch == null) {
            "0"
        } else {
            itemCodeFactory.next(lastBatch.endCode)
        }

        // 计算需要 需要产生的批次 和每个批次的数量
        val batches = calcBatchCount(totalNum)

        return batches.map { batchCount ->
            // 计算一个批次开始和结束的 code
            val batchStartCode = startCode
            val batchEndCode = itemCodeFactory.offsetBit(batchStartCode, batchCount - 1)
            startCode = itemCodeFactory.offsetBit(batchEndCode, 1)
            OneBatch(count = batchCount, startCode = batchStartCode, endCode = batchEndCode )
        }.toTypedArray()

    }

    fun genExcel2003(remark: String, codes: Array<SXItemCode>, urlPrefix: String) : ByteArray {
        val columns = arrayOf("item_code_url", "item_code", "status", "batchId", "create_time")
        val wb = HSSFWorkbook()
        val sheet = wb.createSheet(remark)
        // 第一列列头
        val rowFirst = sheet.createRow(0)
        columns.forEachIndexed { i, colName ->
            val headerCell = rowFirst.createCell(i)
            headerCell.setCellValue(colName)
        }
        codes.forEachIndexed { i, itemCode ->
            val row = sheet.createRow( i + 1 )
            columns.forEachIndexed { j, _ ->
                val cell = row.createCell(j)
                if ( j == 0 ) {
                    cell.setCellValue( urlPrefix + "~${itemCode.code}~${itemCode.salt}")
                }
                if ( j == 1 ) {
                    cell.setCellValue( itemCode.code )
                }
                if ( j == 2 ) {
                    cell.setCellValue( getStatusTxt(itemCode.status) )
                }
                if ( j == 3 ) {
                    cell.setCellValue( itemCode.batchId.toString() )
                }
                if ( j == 4 ) {
                    cell.setCellValue( dateformat.format(itemCode.createAt) )
                }
            }
        }
        val outputByte = ByteArrayOutputStream()
        wb.write(outputByte)
        return outputByte.toByteArray()
    }

    fun genExcel(remark: String, codes: Array<SXItemCode>, urlPrefix: String) : ByteArray {
        val columns = arrayOf("item_code_url", "item_code", "status", "batchId", "create_time")
        val wb = XSSFWorkbook()
        val sheet = wb.createSheet(remark)
        // 第一列列头
        val rowFirst = sheet.createRow(0)
        columns.forEachIndexed { i, colName ->
            val headerCell = rowFirst.createCell(i)
            headerCell.setCellValue(colName)
        }
        codes.forEachIndexed { i, itemCode ->
            val row = sheet.createRow(i + 1)
            columns.forEachIndexed { j, _ ->
                val cell = row.createCell(j)
                if ( j == 0 ) {
                    cell.setCellValue( urlPrefix + "~${itemCode.code}~${itemCode.salt}" )
                }
                if ( j == 1 ) {
                    cell.setCellValue( itemCode.code )
                }
                if ( j == 2 ) {
                    cell.setCellValue( getStatusTxt(itemCode.status) )
                }
                if ( j == 3 ) {
                    cell.setCellValue( itemCode.batchId.toString() )
                }
                if ( j == 4 ) {
                    cell.setCellValue( dateformat.format(itemCode.createAt) )
                }
            }
        }
        val outputByte = ByteArrayOutputStream()
        wb.write(outputByte)
        return outputByte.toByteArray()
    }

}