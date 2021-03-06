package com.github.cuter44.wxpay.reqs;

import java.util.Properties;
import java.io.IOException;

import com.github.cuter44.nyafx.text.*;
import org.apache.http.client.fluent.*;

import com.github.cuter44.wxpay.*;
import com.github.cuter44.wxpay.constants.*;
import com.github.cuter44.wxpay.resps.*;

public class SnsUserinfo
{
  // KEYS
    public static final String URL_API_BASE = "https://api.weixin.qq.com/sns/userinfo";

  // CONF
    protected String accessToken;
    protected String openid;

  // CONSTRUCT
    public SnsUserinfo(String accessToken, String openid)
    {
        this.accessToken = accessToken;
        this.openid = openid;

        return;
    }

    public SnsUserinfo(SnsOAuthAccessTokenResponse resp)
    {
        this.accessToken = resp.getAccessToken();
        this.openid = resp.getOpenid();

        return;
    }

  // EXECUTE
    public SnsUserinfoResponse execute()
        throws IOException
    {
        String url = new URLBuilder()
            .appendPath(URL_API_BASE)
            .appendParam("access_token" , this.accessToken)
            .appendParam("openid"       , this.openid)
            .appendParam("lang"         , "zh_CN")
            .toString();

        String strResp = new String(
            Request.Get(url)
                .execute()
                .returnContent()
                .asBytes(),
            "utf-8"
        );

        return(new SnsUserinfoResponse(strResp));
    }

}
