package com.example.pdffromjson.ui.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdffromjson.R;
import com.example.pdffromjson.ui.activity.MainActivity;
import com.example.pdffromjson.ui.adapter.YearSpinnerAdapter;
import com.example.pdffromjson.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends BaseFragment {

    private static final String TAG = "ReportsFragment";

    @BindView(R.id.year_pdf_spinner)
    Spinner yearSpinner;

    @BindView(R.id.pdf_webView)
    WebView pdfWebView;

//    @BindView(R.id.pdf_viewer)
//    PDFView pdfView;

    @BindView(R.id.report_text)
    TextView reportText;

    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<String> invalidYearList = new ArrayList<>();
    JSONObject objReports;
    private ProgressDialog progressDialog;

    public ReportsFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_reports;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("List of reports");

        //getting all reports data from reports.json file
        getAllReports();

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String year = yearList.get(position);
                    Log.e(TAG, "onItemSelected: " + year);
                    try {
                        String report = objReports.getString(year);
                        if (report.contains(".pdf")) {
                            pdfWebView.setVisibility(View.VISIBLE);
//                            pdfView.setVisibility(View.GONE);
                            reportText.setVisibility(View.GONE);
                            ConnectivityManager cm = (ConnectivityManager) getActivity()
                                    .getSystemService(CONNECTIVITY_SERVICE);
                            NetworkInfo info = cm.getActiveNetworkInfo();
                            if (info != null) {
                                setPdfWebView(report);
                                Log.d(TAG, "onItemSelected: pdf: \n" + report);
                            } else {
                                toastMessage("No network connection.");
                            }
                        } else {
                            pdfWebView.setVisibility(View.GONE);
                            reportText.setVisibility(View.VISIBLE);
//                            pdfView.setVisibility(View.VISIBLE);
                            reportText.setText("This report need to open with pdf reader " +
                                    "or we can direct open from sd storage '"+year+".pdf' file");
                            setPdfFromString(report, year);
                        }
                    } catch (JSONException e) {
                        pdfWebView.setVisibility(View.GONE);
//                        pdfView.setVisibility(View.GONE);
                        reportText.setVisibility(View.VISIBLE);
                        reportText.setText(Constants.REPORT_NOT_AVAILABLE);
                        e.printStackTrace();
                    }
                } else {
                    pdfWebView.setVisibility(View.GONE);
                    reportText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setPdfFromString(String report, String year) {
        String path = Environment.getExternalStorageDirectory() + File.separator + year + ".pdf";
        File file = new File(path);
            byte[] pdfAsBytes = Base64.decode(report, Base64.NO_WRAP);
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(file, false);
                outputStream.write(pdfAsBytes);
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        viewPdf(year);
    }

    // Method for opening a pdf file
    private void viewPdf(String year) {
        Uri mUri;
        File pdfFile = new File(Environment.getExternalStorageDirectory() + File.separator + year + ".pdf");
        if (Build.VERSION.SDK_INT >= 24) {
            mUri = FileProvider.getUriForFile(getActivity(),
                    getActivity().getPackageName() +
                            ".provider", pdfFile);
        } else {
            mUri = Uri.fromFile(pdfFile);
        }
        Log.e(TAG, "viewPdf: " + pdfFile.getPath() + "\n" + mUri);
        // Setting the intent for pdf reader
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(mUri, "application/pdf");
        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        PackageManager pm = getActivity().getPackageManager();
        Intent openInChooser = Intent.createChooser(target, "Choose");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(target, 0);
        if (resInfo.size() > 0) {
            try {
                getActivity().startActivity(openInChooser);
            } catch (Throwable throwable) {
                // PDF apps are not installed
                Toast.makeText(getActivity(), "PDF apps are not installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "PDF apps are not installed", Toast.LENGTH_SHORT).show();
        }

//        pdfView.fromUri(mUri)
//                .defaultPage(0)
//                .enableSwipe(true)
//                .swipeHorizontal(false)
//                .enableDoubletap(true);
//        pdfView.enableAnnotationRendering(true);
//        pdfView.loadPages();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setPdfWebView(String reportUrl) {
        progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
        progressDialog.setCancelable(false);
        pdfWebView.getSettings().setJavaScriptEnabled(true);
        pdfWebView.getSettings().setSupportZoom(true);
        pdfWebView.getSettings().setLoadWithOverviewMode(true);
        pdfWebView.getSettings().setUseWideViewPort(true);
        pdfWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progressDialog.dismiss();
            }
        });

        pdfWebView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + reportUrl);
    }

    private void getAllReports() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            objReports = obj.getJSONObject("reports");
            String statusStr = obj.getString("status");
            String yearStrArray = obj.getString("years");
            JSONArray invalidYearsJsonArray = obj.getJSONArray("invalidYears");

            Log.d(TAG, "getAllReports: "/*
                    + "\n reports: " + objReports*/
                    + "\n status: " + statusStr
                    + "\n years: " + yearStrArray
                    + "\n invalidYears: " + invalidYearsJsonArray);
            String yearStr = yearStrArray.replace("[", "").replace("]", "");
            Log.d(TAG, "yearStr: " + yearStr);

            String[] yearsArray = yearStr.split(", ");
            yearList.add("Select Year");
            for (String year : yearsArray) {
                Log.d(TAG, "year: " + year);
                yearList.add(year);
            }
            for (int i = 0; i < invalidYearsJsonArray.length(); i++) {
                String invalidYears = invalidYearsJsonArray.getString(i);
                invalidYearList.add(invalidYears);
            }
            YearSpinnerAdapter adapter = new YearSpinnerAdapter(getActivity(), yearList, invalidYearList);
            yearSpinner.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("reports.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


}
