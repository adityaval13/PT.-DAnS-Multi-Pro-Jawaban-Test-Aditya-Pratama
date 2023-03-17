package com.example.dans.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dans.models.entities.User;
import com.example.dans.services.UserService;
import com.example.dans.utils.RestService;
import com.example.dans.utils.RestServiceArrayMap;
import com.opencsv.CSVWriter;

@RestController
@RequestMapping
public class MainController {

  @Autowired
  private UserService userService;

  @PostMapping(path = "/login")
  public ResponseEntity<User> login(@RequestBody User user) {
    User validatedUser = userService.validateUser(user.getUsername(), user.getPassword());

    if (validatedUser != null) {
      return ResponseEntity.ok(validatedUser);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping(path = "/job/list")
  public ResponseEntity<Object> jobListApi() {
    RestServiceArrayMap jobListConfig = new RestServiceArrayMap();
    jobListConfig.setUrl("http://dev3.dansmultipro.co.id/api/recruitment/positions.json");
    jobListConfig.setRto(5000);
    jobListConfig.setCto(5000);

    return ResponseEntity.ok().body(jobListConfig.restGet());
  }

  @GetMapping(path = "/job/detail/{id}")
  public ResponseEntity<Object> jobDetail(@PathVariable("id") String id) {
    RestService jobListConfig = new RestService();
    jobListConfig.setUrl("http://dev3.dansmultipro.co.id/api/recruitment/positions/");
    jobListConfig.setRto(5000);
    jobListConfig.setCto(5000);

    HashMap<String, Object> map = new HashMap<>();
    map.put("id", id);
    return ResponseEntity.ok().body(jobListConfig.restGet(map));
  }

  @GetMapping(path = "/job/list/csv", produces = "text/csv")
  public ResponseEntity<byte[]> exportCsv() throws IOException {
    RestServiceArrayMap jobListConfig = new RestServiceArrayMap();
    jobListConfig.setUrl("http://dev3.dansmultipro.co.id/api/recruitment/positions.json");
    jobListConfig.setRto(5000);
    jobListConfig.setCto(5000);

    ArrayList<HashMap<String, Object>> data = jobListConfig.restGet();

    // create a byte array output stream to write the CSV data to
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // create a CSV writer with default separator and quote character
    CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
        CSVWriter.DEFAULT_SEPARATOR,
        CSVWriter.NO_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
        CSVWriter.DEFAULT_LINE_END);

    // write the header row to the CSV
    String[] header = { "id", "type", "url", "created_at", "company", "company_url", "location", "title", "description",
        "how_to_apply", "company_logo" };
    writer.writeNext(header);

    // write the data rows to the CSV
    for (HashMap<String, Object> row : data) {
      String[] rowData = {
          (String) row.get("id"),
          (String) row.get("type"),
          (String) row.get("url"),
          (String) row.get("created_at"),
          (String) row.get("company"),
          (String) row.get("company_url"),
          (String) row.get("location"),
          (String) row.get("title"),
          (String) row.get("description"),
          (String) row.get("how_to_apply"),
          (String) row.get("company_logo")
      };
      writer.writeNext(rowData);
    }

    // flush and close the CSV writer
    writer.flush();
    writer.close();

    // set the response headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("attachment", "hasil.csv");

    // convert the CSV data to a byte array and return it in the response
    byte[] bytes = outputStream.toByteArray();
    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
  }
}
