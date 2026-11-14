package com.portfolio.dukaanmate.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.portfolio.dukaanmate.data.local.entity.InvoiceEntity
import com.portfolio.dukaanmate.domain.model.CartItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfInvoiceHelper {

    fun generateAndShareBill(
        context: Context,
        invoice: InvoiceEntity,
        items: List<CartItem>,
        shopName: String = "DukaanMate General Store"
    ) {
        val pdfDocument = PdfDocument()
        // Compact A5-sized bill page (420 x 600)
        val pageInfo = PdfDocument.PageInfo.Builder(420, 600 + (items.size * 25), 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(0, 108, 76) // Emerald Green
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val normalPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 40f

        // 1. Shop Name and Header
        canvas.drawText(shopName, 210f, y, titlePaint)
        y += 22f
        normalPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("TAX INVOICE / CASH MEMO", 210f, y, normalPaint)
        y += 18f
        canvas.drawLine(20f, y, 400f, y, linePaint)
        y += 20f

        // 2. Bill and Customer Information
        normalPaint.textAlign = Paint.Align.LEFT
        val dateStr = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date(invoice.createdAt))
        canvas.drawText("Bill No: ${invoice.invoiceNumber}", 25f, y, headerPaint)
        canvas.drawText("Date: $dateStr", 230f, y, normalPaint)
        y += 18f
        canvas.drawText("Customer: ${invoice.customerName}", 25f, y, normalPaint)
        if (invoice.customerPhone.isNotBlank()) {
            canvas.drawText("Phone: ${invoice.customerPhone}", 230f, y, normalPaint)
        }
        y += 15f
        canvas.drawLine(20f, y, 400f, y, linePaint)
        y += 20f

        // 3. Table Header
        canvas.drawText("Item Name", 25f, y, headerPaint)
        canvas.drawText("Qty", 230f, y, headerPaint)
        canvas.drawText("Rate", 280f, y, headerPaint)
        canvas.drawText("Total", 345f, y, headerPaint)
        y += 10f
        canvas.drawLine(20f, y, 400f, y, linePaint)
        y += 20f

        // 4. Items List
        for (item in items) {
            val itemTotal = item.product.sellPrice * item.quantity
            val shortName = if (item.product.name.length > 24) item.product.name.take(24) + "..." else item.product.name
            canvas.drawText(shortName, 25f, y, normalPaint)
            canvas.drawText("${item.quantity}", 235f, y, normalPaint)
            canvas.drawText("₹${item.product.sellPrice}", 280f, y, normalPaint)
            canvas.drawText("₹${String.format("%.2f", itemTotal)}", 345f, y, normalPaint)
            y += 22f
        }

        // 5. Grand Total
        canvas.drawLine(20f, y, 400f, y, linePaint)
        y += 25f
        titlePaint.textAlign = Paint.Align.RIGHT
        titlePaint.textSize = 16f
        canvas.drawText("Grand Total: ₹${String.format("%.2f", invoice.totalAmount)}", 395f, y, titlePaint)

        y += 35f
        normalPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("धन्यवाद! फिर पधारें (Thank You for Shopping!)", 210f, y, normalPaint)

        pdfDocument.finishPage(page)

        // 6. Save PDF in Cache folder
        val pdfFile = File(context.cacheDir, "${invoice.invoiceNumber}.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF बनाने में समस्या आई", Toast.LENGTH_SHORT).show()
            return
        } finally {
            pdfDocument.close()
        }

        // 7. Open WhatsApp or Share Sheet
        sharePdfFile(context, pdfFile, invoice)
    }

    private fun sharePdfFile(context: Context, file: File, invoice: InvoiceEntity) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(
                Intent.EXTRA_TEXT,
                "नमस्ते ${invoice.customerName}, आपका बिल (${invoice.invoiceNumber}) कुल राशि ₹${String.format("%.2f", invoice.totalAmount)} का तैयार है।"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Try to open directly in WhatsApp (if installed)
        val whatsappIntent = Intent(shareIntent).apply {
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            // If WhatsApp is not available, open standard Share dialog
            context.startActivity(Intent.createChooser(shareIntent, "बिल शेयर करें"))
        }
    }
}
