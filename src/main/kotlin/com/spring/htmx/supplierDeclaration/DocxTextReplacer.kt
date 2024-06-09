package com.spring.htmx.supplierDeclaration

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.function.Consumer

class DocxTextReplacer {
    fun replaceText(supplierDeclaration: SuppliersDeclarationEntity): ByteArrayOutputStream {
        val inFilePath = javaClass.classLoader.getResource("static/dummy.docx")!!.path

        FileInputStream(inFilePath).use { inputStream ->
            var doc = XWPFDocument(inputStream)

            doc = replaceText(doc, "ITEM_ID", supplierDeclaration.salesOrderItemId, true)
            doc = replaceText(doc, "CNUMBER", supplierDeclaration.customsTariffNumber, true)
            doc = replaceText(doc, "PRODUCT_DESCRIPTION", supplierDeclaration.salesDescription, true)
            doc = replaceText(doc, "CONTRY_LIST", supplierDeclaration.preferentialCountries, false)
            doc = replaceText(doc, "CDATE", SimpleDateFormat("dd.MM.yyyy").format(supplierDeclaration.created), true)

            return saveDoc(doc)
        }
    }

    private fun replaceText(doc: XWPFDocument, originalText: String, updatedText: String, bold: Boolean): XWPFDocument {
        replaceTextInParagraphs(doc.paragraphs, originalText, updatedText, bold)
        for (tbl in doc.tables) {
            for (row in tbl.rows) {
                for (cell in row.tableCells) {
                    replaceTextInParagraphs(cell.paragraphs, originalText, updatedText, bold)
                }
            }
        }
        return doc
    }

    private fun replaceTextInParagraphs(paragraphs: List<XWPFParagraph>, originalText: String, updatedText: String, bold: Boolean) {
        paragraphs.forEach(
            Consumer { paragraph: XWPFParagraph ->
                replaceTextInParagraph(
                    paragraph,
                    originalText,
                    updatedText,
                    bold
                )
            }
        )
    }

    private fun replaceTextInParagraph(paragraph: XWPFParagraph, originalText: String, updatedText: String, bold: Boolean) {
        val paragraphText: String = paragraph.paragraphText
        if (paragraphText.contains(originalText)) {
            val updatedParagraphText = paragraphText.replace(originalText, updatedText)
            while (paragraph.runs.size > 0) {
                paragraph.removeRun(0)
            }
            val newRun = paragraph.createRun()

            newRun.fontFamily = "Trebuchet MS"
            newRun.fontSize = 9
            newRun.isBold = bold
            newRun.setText(updatedParagraphText)
        }
    }

    private fun saveDoc(doc: XWPFDocument): ByteArrayOutputStream {
        val result = ByteArrayOutputStream()
        result.use { out ->
            doc.write(out)
        }

        return result
    }
}
