package com.example.controller;

import com.example.utils.Element;
import com.example.utils.JsonDataGenerator;
import com.example.utils.WordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
public class DataController {

    @GetMapping("/data")
    public List<Element> data() {
        return JsonDataGenerator.jsonData;
    }

    /**
     * 用于导出测试数据生成的word文档
     *
     * @return
     */
    @GetMapping("/download/data")
    public ResponseEntity<byte[]> downloadFile() throws Exception {

        File file = WordUtils.export(JsonDataGenerator.jsonData);
        log.info("file not null: {}", file!=null);
        log.info("file name: {}", file.getName());
        log.info("file path: {}", file.getAbsolutePath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
