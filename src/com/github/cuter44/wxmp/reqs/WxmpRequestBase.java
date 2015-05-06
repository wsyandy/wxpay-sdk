package com.github.cuter44.wxmp.reqs;

import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import javax.net.ssl.SSLContext;

import com.github.cuter44.nyafx.crypto.*;
import com.github.cuter44.nyafx.text.*;
import org.apache.http.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
//import org.apache.http.client.*;
import org.apache.http.client.methods.*;

import com.github.cuter44.wxmp.WxmpException;
import com.github.cuter44.wxmp.resps.WxmpResponseBase;

/**
 * @author galin<cuter44@foxmail.com>
 * @date 2014/12/25
 */
public abstract class WxmpRequestBase
{
    protected static final String KEY_APPID         = "appid";
    protected static final String KEY_SECRET        = "SECRET";

  // SSL
    /** Default http client to use to send request to weixin server.
     * Provide class-scope http client, which is used when <code>httpClient</code> is null, major for single-account use.
     * You can tweak this with your own. This will takes effect on follow-up request whose <code>httpClient</code> is unset.
     */
    public static CloseableHttpClient defaultHttpClient;

    /** Http client to use to send request to weixin server.
     * Provide object-scope http client, major for multi-account use.
     * You can directly set this field. This will takes effect on time when <code>.execute()</code> is called.
     * It is supposed that
     */
    public CloseableHttpClient httpClient;

    protected static CloseableHttpClient buildHttpClient(SSLContext ctx)
    {
        HttpClientBuilder hcb = HttpClientBuilder.create()
            .disableAuthCaching()
            .disableCookieManagement();

        return(hcb.build());
    }

    /** Config defualt http client
     * The existing <code>defaultHttpClient</code> will be dropped, without closing.
     */
    public static void configDefaultHC(SSLContext ctx)
    {
        defaultHttpClient = buildHttpClient(ctx);
    }

    /** Config http client
     * The existing <code>httpClient</code> will be dropped, without closing.
     * @return this
     */
    public WxmpRequestBase configHC(SSLContext ctx)
    {
        this.httpClient = buildHttpClient(ctx);

        return(this);
    }


  // CONSTRUCT
    public WxmpRequestBase(Properties aConf)
    {
        this.conf = aConf;

        return;
    }

  // CONFIG
    protected Properties conf;

    public String getProperty(String key)
    {
        return(
            this.conf.getProperty(key)
        );
    }

    /**
     * chain supported
     */
    public WxmpRequestBase setProperty(String key, String value)
    {
        this.conf.setProperty(key, value);
        return(this);
    }

    /**
     * batch setProperty
     * @param aConf a Map contains key-value pairs, where key must be String, and values must implement toString() at least.
     */
    public WxmpRequestBase setProperties(Map aConf)
    {
        this.conf.putAll(aConf);
        return(this);
    }

  // BUILD
   public abstract WxmpRequestBase build();

  // TO_URL
    /** Extract URL to execute request on client
     */
    public abstract String toURL()
        throws UnsupportedOperationException;

    /** Provide query string to sign().
     * toURL() may not invoke this method.
     */
    protected String toQueryString(List<String> paramNames)
    {
        URLBuilder ub = new URLBuilder();

        for (String key:paramNames)
            ub.appendParam(key, this.getProperty(key));

        return(ub.toString());
    }


  // EXECUTE
    /** Execute the constructed query
     */
    public abstract WxmpResponseBase execute()
        throws IOException, WxmpException, UnsupportedOperationException;

    protected static String toString(HttpResponse resp)
        throws IOException
    {
        HttpEntity he = resp.getEntity();

        Long l = he.getContentLength();
        ByteArrayOutputStream buffer = (l > 0) ? new ByteArrayOutputStream(l.intValue()) : new ByteArrayOutputStream();
        resp.getEntity().writeTo(buffer);

        String content = buffer.toString("utf-8");

        return(content);
    }

    /**
     */
    public String executeGet(String fullURL)
        throws IOException
    {
        CloseableHttpClient hc = (this.httpClient != null) ? this.httpClient : defaultHttpClient;

        HttpGet req = new HttpGet(fullURL);

        CloseableHttpResponse resp = hc.execute(req);

        String content = toString(resp);

        resp.close();

        return(content);
    }


  // MISC
}
