package com.example.pdffromjson.ui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.example.pdffromjson.R;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class MainActivity extends AppCompatActivity implements /*OnPageChangeListener, OnLoadCompleteListener,*/ DownloadFile.Listener {
    private static final String TAG = "MainActivity";
    private Spinner yearSpinner;
    //    PDFView pdfView;
    RemotePDFViewPager pdfView;
    PDFPagerAdapter pdfPagerAdapter;

    Uri uri;
    String pdfFileName;
    Integer pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        yearSpinner = findViewById(R.id.year_pdf_spinner);
        pdfView = findViewById(R.id.pdfViewPager);
        MainAdapter adapter = new MainAdapter(this, 10);
        yearSpinner.setAdapter(adapter);
        String url = "https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf";

        pdfView = new RemotePDFViewPager(this, url, this);
    }


  /*  private void displayFromUri() {
        Uri uri = Uri.parse("http://www.africau.edu/images/default/sample.pdf");

        pdfView.fromUrl("http://www.africau.edu/images/default/sample.pdf")
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();
    }
*/

//    @Override
//    public void loadComplete(int nbPages) {
//        PdfDocument.Meta meta = pdfView.getDocumentMeta();
//        printBookmarksTree(pdfView.getTableOfContents(), "-");
//    }
//
//    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
//        for (PdfDocument.Bookmark b : tree) {
//
//            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
//
//            if (b.hasChildren()) {
//                printBookmarksTree(b.getChildren(), sep + "-");
//            }
//        }
//    }
//
//    @Override
//    public void onPageChanged(int page, int pageCount) {
//        pageNumber = page;
//        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
//    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        // That's the positive case. PDF Download went fine

        pdfPagerAdapter = new PDFPagerAdapter(this, "AdobeXMLFormsSamples.pdf");
        pdfView.setAdapter(pdfPagerAdapter);
        setContentView(pdfView);
    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pdfPagerAdapter.close();
    }
}
