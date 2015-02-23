package cz.tsystems.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by KUBO on 23. 2. 2015.
 */
@SuppressLint("NewApi")
public class ProtocolPrintAdapter extends PrintDocumentAdapter {

    Context _context;
    final String _pdfFileUrl;

    public ProtocolPrintAdapter(Context context, final String pdfFileUrl) {
        this._context = context;
        _pdfFileUrl = pdfFileUrl;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        int pages = computePageCount(newAttributes);

        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("PCHI_report").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

        callback.onLayoutFinished(pdi, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        InputStream input = null;
        OutputStream output = null;

        try {
            File pdfFile = new File(_pdfFileUrl);
            input = new FileInputStream(pdfFile);
            output = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException ee){
            //Catch exception
        } catch (Exception e) {
            //Catch exception
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int computePageCount(PrintAttributes printAttributes) {
        int itemsPerPage = 4; // default item count for portrait mode

        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
        if (!pageSize.isPortrait()) {
            // Six items per page in landscape orientation
            itemsPerPage = 6;
        }

        // Determine number of print items
        int printItemCount = 1;//getPrintItemCount();

        return (int) Math.ceil(printItemCount / itemsPerPage);
    }
}
