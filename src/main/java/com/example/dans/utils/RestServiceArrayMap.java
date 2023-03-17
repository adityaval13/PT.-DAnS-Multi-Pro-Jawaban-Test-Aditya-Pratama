package com.example.dans.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestServiceArrayMap {

  public RestServiceArrayMap() {
  }

  private static final Logger log = LoggerFactory.getLogger(RestServiceArrayMap.class);
  private String url;
  private int rto;
  private int cto;
  private HashMap<String, Object> headers = new HashMap<String, Object>();

  public ArrayList<HashMap<String, Object>> restPost(HashMap<String, Object> requestBody) {

    ArrayList<HashMap<String, Object>> responsebuild = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> map = new HashMap<String, Object>();
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
      } else {
        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
      }

      String result = org.apache.commons.io.IOUtils.toString(br);

      log.info(String.format("RESPONSE 'POST' | URL = %s | STATUS = %d | HEADER = %s | RESPONSE BODY = %s", url, status,
          String.valueOf(con.getHeaderFields()), result));

      br.close();
      con.disconnect();

      if (status != 200) {
        map.put("conn_rc", "H99");
        map.put("conn_rd", "ERROR OCCURED");
        responsebuild.add(map);

        return responsebuild;
      }

      responsebuild = stringToArrayObj(result);
      return responsebuild;
    } catch (ConnectException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "connection refused");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);

      return responsebuild;
    } catch (java.net.SocketTimeoutException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "connection timeout");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (SocketException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "socket issue");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (IOException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "io issue");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (Exception e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "unhandled exception");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    }
  }

  public ArrayList<HashMap<String, Object>> restGet() {

    ArrayList<HashMap<String, Object>> responsebuild = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> map = new HashMap<String, Object>();
    ObjectMapper mapper = new ObjectMapper();

    try {
      // if (!requestParam.isEmpty()) {
      // for (String key : requestParam.keySet()) {
      // url = url + requestParam.get(key) + "/";
      // }

      // url = url.substring(0, url.length() - 1);
      // }

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
            String.format("RESPONSE 'GET' | URL = %s | STATUS = %d | HEADER = %s | RESPONSE BODY = %s", url, status,
                String.valueOf(con.getHeaderFields()), result));

        br.close();
        con.disconnect();

        responsebuild = stringToArrayObj(result);

        return responsebuild;
      } else {
        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        String result = org.apache.commons.io.IOUtils.toString(br);

        log.info(
            String.format("RESPONSE 'GET' | URL = %s | STATUS = %d | HEADER = %s | RESPONSE BODY = %s", url, status,
                String.valueOf(con.getHeaderFields()), result));

        br.close();
        con.disconnect();

        if (status != 200) {
          map.put("conn_rc", "H99");
          map.put("conn_rd", "ERROR OCCURED");

          responsebuild.add(map);
          return responsebuild;
        }

        responsebuild = stringToArrayObj(result.toString());

        return responsebuild;
      }
    } catch (ConnectException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "connection refused");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (java.net.SocketTimeoutException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "connection timeout");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (SocketException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "socket issue");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (IOException e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "io issue");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    } catch (Exception e) {
      map.put("conn_rc", "H68");
      map.put("conn_rd", "unhandled exception");

      log.info(ExceptionUtils.getStackTrace(e));
      responsebuild.add(map);
      return responsebuild;
    }
  }

  private ArrayList<HashMap<String, Object>> stringToArrayObj(String json) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      ArrayList<HashMap<String, Object>> datahashmap = new ArrayList<HashMap<String, Object>>();
      datahashmap = mapper.readValue(json, new TypeReference<ArrayList<HashMap<String, Object>>>() {
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
