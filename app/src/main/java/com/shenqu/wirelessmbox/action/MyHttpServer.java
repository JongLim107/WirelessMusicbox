package com.shenqu.wirelessmbox.action;

/**
 * Created by JongLim on 2016/12/12.
 */

import com.shenqu.wirelessmbox.tools.FileUtils;
import com.shenqu.wirelessmbox.tools.JLLog;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Locale;

public class MyHttpServer {
    private static final String TAG = MyHttpServer.class.getSimpleName();
    private boolean isServerExit;
    public static final int LISTEN_PORT = 0x2f2f;

    public MyHttpServer(){
        start();
    }

    private class WebServiceHandler implements HttpRequestHandler {

        @Override
        public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
            JLLog.LOGV(TAG, "httpRequest = " + httpRequest.getRequestLine());
            String reqUri = httpRequest.getRequestLine().getUri();
            String reqType = httpRequest.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            if (!reqType.equals("GET")) {
                return;
            }
            if (reqUri.endsWith(".json")) {
                File file;
                if (reqUri.contains("local"))
                    file = new File(FileUtils.getLocalListPath());
                else if (reqUri.contains("favorite"))
                    file = new File(FileUtils.getFavoriteListPath());
                else
                    file = new File(FileUtils.getJSONFilePath() + reqUri.substring(reqUri.lastIndexOf("/")));
                JLLog.LOGV(TAG, file.getAbsolutePath() + ", exists:" + file.exists() + ", size:" + file.length());

                httpResponse.setEntity(new FileEntity(file, "application/json"));
                httpResponse.setHeader("content-length", String.valueOf(file.length()));
                httpResponse.setStatusCode(200);
            } else if (reqUri.startsWith(FileUtils.LOCAL_FILE_INDICATE)) {
                File file = new File(URLDecoder.decode(FileUtils.getLocalSongPath(reqUri)));
                JLLog.LOGV(TAG, file.getAbsolutePath() + ", exists:" + file.exists() + ", size:" + file.length());
                httpResponse.setEntity(new FileEntity(file, "application/octet-stream"));
                httpResponse.setHeader("content-length", String.valueOf(file.length()));
                httpResponse.setStatusCode(200);
            } else if (reqUri.contains("-HIVI-")) {
                File file = new File(URLDecoder.decode(reqUri));
                JLLog.LOGV(TAG, file.getAbsolutePath() + ", exists:" + file.exists() + ", size:" + file.length());
                httpResponse.setEntity(new FileEntity(file, "application/octet-stream"));
                httpResponse.setHeader("content-length", String.valueOf(file.length()));
                httpResponse.setStatusCode(200);
            }
        }
    }

    private class WorkerThread extends Thread {
        private final HttpServerConnection mConnection;
        private final HttpService httpService;

        WorkerThread(HttpService server, HttpServerConnection connet) {
            httpService = server;
            mConnection = connet;
        }

        public void run() {
            HttpContext context = new BasicHttpContext();
            try {
                while (!Thread.interrupted() && mConnection.isOpen()) {
                    httpService.handleRequest(mConnection, context);
                }
            } catch (HttpException e) {
                JLLog.LOGE(TAG, "HttpException " + e.getMessage());
            } catch (IOException e) {
                JLLog.LOGE(TAG, "IOException " + e.getMessage());
                interrupted();
            } finally {
                try {
                    mConnection.shutdown();
                } catch (IOException e) {
                    JLLog.LOGE(TAG, "Connection shutdown " + e.getMessage());
                }
            }
        }
    }

    private class RequestListenerThread extends Thread {
        private final HttpService httpService;
        private final HttpParams params;
        private final ServerSocket serversocket;

        RequestListenerThread() throws IOException {
            serversocket = new ServerSocket(LISTEN_PORT);
            //serversocket.setReuseAddress(true);

            params = new BasicHttpParams();
            params.setIntParameter("http.socket.timeout", 5000).setIntParameter("http.socket.buffer-size", 8192).setBooleanParameter("http.connection.stalecheck", false)
                    .setBooleanParameter("http.tcp.nodelay", true).setParameter("http.origin-server", "HttpComponents/1.1");

            BasicHttpProcessor processor = new BasicHttpProcessor(); //http协议处理器

            //http请求处理程序解析器
            HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
            //http请求处理程序，HttpFileHandler继承于HttpRequestHandler（http请求处理程序)
            registry.register("*", new WebServiceHandler());
            httpService = new HttpService(processor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
            httpService.setParams(params);
            httpService.setHandlerResolver(registry);//为http服务设置注册好的请求处理器。
        }

        public void run() {
            JLLog.LOGV(TAG, "Listening on port " + serversocket.getLocalPort());
            while (!Thread.interrupted() && !isServerExit) {
                try {
                    Object object = serversocket.accept();
                    DefaultHttpServerConnection defHttpSrvConn = new DefaultHttpServerConnection();
                    defHttpSrvConn.bind((Socket) object, params);
                    JLLog.LOGV(TAG, "Incoming connection from " + ((Socket) object).getInetAddress());
                    WorkerThread worker = new WorkerThread(httpService, defHttpSrvConn);
                    worker.setDaemon(true);
                    worker.start();
                } catch (IOException e) {
                    JLLog.LOGE(TAG, "I/O error initialising connection thread: " + e.getMessage());
                }
            }
            JLLog.LOGV(TAG, "Stop the Listening.");
        }
    }

    private void start() {
        isServerExit = false;
        try {
            RequestListenerThread listenerThread = new RequestListenerThread();
            listenerThread.setDaemon(false);
            listenerThread.start();
        } catch (IOException localIOException) {
            JLLog.LOGE(TAG, localIOException.getLocalizedMessage());
        }
    }

    public void stop(){
        isServerExit = true;
    }
}
