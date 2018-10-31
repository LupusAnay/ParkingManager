package com.henculus.parkingmanager;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetworkFragment extends Fragment {
    private static final String TAG = "NetworkFragment.java";
    private static final String URL_KEY = "UrlKey";

    private DownloadCallback _callback;
    private DownloadTask _downloadTask;
    private String _urlString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _urlString = getArguments().getString(URL_KEY);
    }

    @Override
    public void onAttach(Context context) {
        _callback = (DownloadCallback) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _callback = null;
    }

    @Override
    public void onDestroy() {
        cancelDownload();
        super.onDestroy();
    }

    static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {
        NetworkFragment networkFragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    public void startDownload(String file, String date, String place) {
        cancelDownload();
        _downloadTask = new DownloadTask(_callback);
        _downloadTask.execute(_urlString, file, date, place);
    }


    public void cancelDownload() {
        if (_downloadTask != null) {
            _downloadTask.cancel(true);
        }
    }

    private static class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {
        private DownloadCallback<String> _callback;

        DownloadTask(DownloadCallback<String> callback) {
            setCallback(callback);
        }

        void setCallback(DownloadCallback<String> callback) {
            _callback = callback;
        }

        static class Result {
            String resultValue;
            Exception exception;

            Result(String _resultValue) {
                resultValue = _resultValue;
            }

            Result(Exception _exception) {
                exception = _exception;
            }
        }

        @Override
        protected void onPreExecute() {
            if (_callback != null) {
                NetworkInfo networkInfo = _callback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    _callback.updateFromDownload(null);
                    cancel(true);
                }
            }

        }

        @Override
        protected Result doInBackground(String... params) {
            Result result = null;
            if (!isCancelled() && params != null && params.length > 0) {
                String urlString = params[0] + params[1] + "/?date=" + params[2] + "&place=" + params[3];

                try {
                    URL url = new URL(urlString);
                    String resultString = downloadUrl(url);
                    if (resultString != null) {
                        result = new Result(resultString);
                    } else {
                        throw new IOException("No response received.");
                    }

                } catch (Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result != null && _callback != null) {
                if (result.exception != null) {
                    _callback.updateFromDownload(result.exception.getMessage());
                } else if (result.resultValue != null) {
                    _callback.updateFromDownload(result.resultValue);
                }
                _callback.finishDownloading();
            }
        }

        @Override
        protected void onCancelled(Result result) {
        }

        private String downloadUrl(URL url) throws IOException {
            InputStream stream = null;
            HttpURLConnection connection = null;
            String result = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
                if (stream != null) {
                    result = readStream(stream, 500);
                }

            } finally {
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        private String readStream(InputStream stream, int maxReadSize) throws IOException, UnsupportedOperationException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] rawBuffer = new char[maxReadSize];
            int readSize;
            StringBuilder buffer = new StringBuilder();
            while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
                if (readSize > maxReadSize) {
                    readSize = maxReadSize;
                }
                buffer.append(rawBuffer, 0, readSize);
                maxReadSize -= readSize;
            }
            return buffer.toString();
        }
    }
}
