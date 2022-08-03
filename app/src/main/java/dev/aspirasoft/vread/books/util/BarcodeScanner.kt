package dev.aspirasoft.vread.books.util;

import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


/**
 * A utility class to scan barcodes using the ZXing-Android-Embedded library.
 *
 * @author AspiraSoft
 *
 * @constructor Creates a new BarcodeScanner instance.
 * @param activity The activity to use for scanning.
 * @param callback The callback to receive the result of the scan.
 *
 * @property scanOptions The default scan options to use.
 * @property scanLauncher The [ActivityResultLauncher] to launch the scan activity.
 */
class BarcodeScanner(activity: AppCompatActivity, callback: (String?) -> Unit) {

    private val scanOptions = ScanOptions().apply {
        this.setDesiredBarcodeFormats(ScanOptions.EAN_13)
        this.setPrompt("Scan a book barcode")
        this.setBeepEnabled(true)
        this.setBarcodeImageEnabled(true)
    }

    private val scanLauncher: ActivityResultLauncher<ScanOptions> =
        activity.registerForActivityResult(ScanContract()) {
            val barcode = it?.contents ?: ""
            callback(barcode)
        }

    /**
     * Starts the scan activity.
     */
    fun start() = scanLauncher.launch(scanOptions)

}
