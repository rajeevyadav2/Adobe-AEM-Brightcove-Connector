/*

    Adobe AEM Brightcove Connector

    Copyright (C) 2017 Coresecure Inc.

    Authors:    Alessandro Bonfatti
                Yan Kisen
                Pablo Kropilnicki

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    - Additional permission under GNU GPL version 3 section 7
    If you modify this Program, or any covered work, by linking or combining
    it with httpclient 4.1.3, httpcore 4.1.4, httpmine 4.1.3, jsoup 1.7.2,
    squeakysand-commons and squeakysand-osgi (or a modified version of those
    libraries), containing parts covered by the terms of APACHE LICENSE 2.0
    or MIT License, the licensors of this Program grant you additional
    permission to convey the resulting work.

 */
package com.coresecure.brightcove.wrapper.objects;

import com.coresecure.brightcove.wrapper.utils.Constants;
import com.coresecure.brightcove.wrapper.utils.HttpServices;
import com.coresecure.brightcove.wrapper.utils.JsonReader;
import org.apache.jackrabbit.util.Base64;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Account {
    private final String client_id;
    private final String client_secret;
    private final String account_id;
    private TokenObj authToken;
    public final Platform platform;
    private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

    public Account(Platform aPlatform, String aClient_id, String aClient_secret, String aAccount_id) {
        client_id = aClient_id;
        client_secret = aClient_secret;
        account_id = aAccount_id;
        platform = aPlatform;
    }

    public String getAccount_ID() {
        return account_id;
    }

    public boolean login() {
        boolean result = false;
        authToken = null;
        LOGGER.debug("getAccount_ID");
        String token = Base64.encode(client_id + ":" + client_secret);
        LOGGER.debug("token: {}", token);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(DavConstants.HEADER_AUTHORIZATION, "Basic " + token);
        String urlParameters = "grant_type=client_credentials";
        String targetURL = platform.getOAUTH_Url() + "/access_token";
        try {
            String loginResponse = HttpServices.executePost(targetURL, urlParameters, headers);
            if (loginResponse == null) return false;
            JSONObject response = JsonReader.readJsonFromString(loginResponse);
            LOGGER.debug(Constants.RESPONSE ,response);

            if (response.getString(Constants.ACCESS_TOKEN) != null && response.getString("token_type") != null) {
                authToken = new TokenObj(response.getString(Constants.ACCESS_TOKEN), response.getString("token_type"), response.getInt("expires_in"));
                result = true;
            }
        } catch (IOException e) {
            LOGGER.error(e.getClass().getName(), e);
        } catch (JSONException e) {
            LOGGER.error(e.getClass().getName(), e);
        }
        return result;
    }

    public TokenObj getToken() {
        return authToken;
    }

    public TokenObj getLoginToken() {
        this.login();
        return getToken();
    }


}
