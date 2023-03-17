package com.example.dans.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestService {

  public RestService() {
  }

  private static final Logger log = LoggerFactory.getLogger(RestService.class);
  private String url;
  private int rto;
  private int cto;
  private HashMap<String, Object> headers = new HashMap<String, Object>();

  public HashMap<String, Object> restPost(HashMap<String, Object> requestBody) {

    HashMap<String, Object> responsebuild = new HashMap<String, Object>();
    ObjectMapper mapper = new ObjectMapper();

    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      headers.put("Content-Type", "application/json; charset=UTF-8");
      headers.put("User-Agent", "SpringBoot/1.0 ( compatible )");
      headers.put("Accept", "*/*");
      headers.put("Accept-Encoding", "UTF-8");
      headers.put("Connection", "keep-alive");
      headers.put("Cache-Control", "no-cache");
      headers.put("http.version", "HTTP/1.1");
      headers.put("http.scheme", "http");

      for (String key : headers.keySet()) {
        con.setRequestProperty(key, headers.get(key).toString().trim());
      }

      con.setReadTimeout(rto);
      con.setConnectTimeout(cto);

      con.setDoOutput(true);
      con.setDoInput(true);
      con.setRequestMethod("POST");

      String jj = mapper.writeValueAsString(requestBody);

      log.info(String.format("REQUEST 'POST' | URL = %s | HEADER = %s | REQUEST BODY = %s", url,
          mapper.writeValueAsString(headers), jj));
      OutputStream os = con.getOutputStream();
      os.write(jj.getBytes("UTF-8"));
      os.close();

      int status = con.getResponseCode();

      BufferedReader br = null;

      if (100 <= status && status <= 399) {
        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        // for (Map.Entry<String, List<String>> entries :
        // con.getHeaderFields().entrySet()) {
        // headersResponse.put(entries.getKey(), entries.getValue());
        // }
      } else {
        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      }

      String result = org.apache.commons.io.IOUtils.toString(br);

      log.info(String.format("RESPONSE 'POST' | URL = %s | STATUS = %d | HEADER = %s | RESPONSE BODY = %s", url, status,
          String.valueOf(con.getHeaderFields()), result));

      br.close();
      con.disconnect();

      if (status != 200) {
        responsebuild.put("conn_rc", "H99");
        responsebuild.put("conn_rd", "ERROR OCCURED");

        return responsebuild;
      }

      responsebuild = stringToMap(result);
      return responsebuild;
    } catch (ConnectException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "connection refused");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (java.net.SocketTimeoutException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "connection timeout");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (SocketException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "socket issue");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (IOException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "io issue");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (Exception e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "unhandled exception");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    }
  }

  public HashMap<String, Object> restGet(HashMap<String, Object> requestParam) {

    HashMap<String, Object> responsebuild = new HashMap<String, Object>();
    ObjectMapper mapper = new ObjectMapper();

    try {
      if (!requestParam.isEmpty()) {
        for (String key : requestParam.keySet()) {
          url = url + requestParam.get(key) + "/";
        }

        url = url.substring(0, url.length() - 1);
      }

      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      headers.put("Content-Type", "application/json; charset=UTF-8");
      headers.put("User-Agent", "SpringBoot/1.0 ( compatible )");
      headers.put("Accept", "*/*");
      headers.put("Accept-Encoding", "UTF-8");
      headers.put("Connection", "keep-alive");
      headers.put("Cache-Control", "no-cache");
      headers.put("http.version", "HTTP/1.1");
      headers.put("http.scheme", "http");

      for (String key : headers.keySet()) {
        con.setRequestProperty(key, headers.get(key).toString().trim());
      }

      con.setReadTimeout(rto);
      con.setConnectTimeout(cto);
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setRequestMethod("GET");

      log.info(String.format("REQUEST 'GET' | URL = %s | HEADER = %s", url,
          mapper.writeValueAsString(headers)));

      int status = con.getResponseCode();

      BufferedReader br = null;
      if (100 <= status && status <= 399) {
        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = org.apache.commons.io.IOUtils.toString(br);

        // for (Map.Entry<String, List<String>> entries :
        // con.getHeaderFields().entrySet()) {
        // headersResponse.put(entries.getKey(), entries.getValue());
        // }

        log.info(
            String.format("RESPONSE 'POST' | URL = %s | STATUS = %d | HEADER = %s | RESPONSE BODY = %s", url, status,
                String.valueOf(con.getHeaderFields()), result));

        br.close();
        con.disconnect();

        responsebuild = stringToMap(result);
        return responsebuild;
      } else {
        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        String result = org.apache.commons.io.IOUtils.toString(br);

        log.info(
            String.format("RESPONSE 'POST' | URL = %s | STATUS = %d | HEADER = %s | RESPONSE BODY = %s", url, status,
                String.valueOf(con.getHeaderFields()), result));

        br.close();
        con.disconnect();

        if (status != 200) {
          responsebuild.put("conn_rc", "H99");
          responsebuild.put("conn_rd", "ERROR OCCURED");

          return responsebuild;
        }

        responsebuild = stringToMap(result);
        return responsebuild;
      }
    } catch (ConnectException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "connection refused");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (java.net.SocketTimeoutException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "connection timeout");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (SocketException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "socket issue");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (IOException e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "io issue");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    } catch (Exception e) {
      responsebuild.put("conn_rc", "H68");
      responsebuild.put("conn_rd", "unhandled exception");

      log.info(ExceptionUtils.getStackTrace(e));
      return responsebuild;
    }
  }

  private HashMap<String, Object> stringToMap(String json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      HashMap<String, Object> datahashmap = new HashMap<String, Object>();
      datahashmap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
      });

      return datahashmap;
    } catch (Exception e) {
      log.info(ExceptionUtils.getStackTrace(e));
    }

    return null;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getRto() {
    return rto;
  }

  public void setRto(int rto) {
    this.rto = rto;
  }

  public int getCto() {
    return cto;
  }

  public void setCto(int cto) {
    this.cto = cto;
  }

  public HashMap<String, Object> getHeaders() {
    return headers;
  }

  public void setHeaders(HashMap<String, Object> headers) {
    this.headers = headers;
  }

}
