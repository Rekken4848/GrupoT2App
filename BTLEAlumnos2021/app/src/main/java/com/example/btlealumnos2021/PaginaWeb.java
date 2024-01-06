package com.example.btlealumnos2021;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class PaginaWeb extends Fragment {
    private WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        webView = v.findViewById(R.id.webViewWeb);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        ViewPager2 viewPager2 = ((MainActivity) getActivity()).viewPager;
        viewPager2.setUserInputEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Permitir zoom
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Cargar la página web con el mapa Leaflet
        webView.loadUrl("http://192.168.1.21:8080/");
        webView.setWebChromeClient(new WebChromeClient());
        return v;
    }
}