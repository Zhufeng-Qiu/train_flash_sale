package com.zephyr.train.generator.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FreemarkerUtil {

  // windows
  // static String ftlPath = "generator\\src\\main\\java\\com\\zephyr\\train\\generator\\ftl\\";

  // mac
  static String ftlPath = "generator/src/main/java/com/zephyr/train/generator/ftl";

  static Template temp;

  /**
   * Read template
   */
  public static void initConfig(String ftlName) throws IOException {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
    cfg.setDirectoryForTemplateLoading(new File(ftlPath));
    cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
    temp = cfg.getTemplate(ftlName);
  }

  /**
   * Generate file based on template
   */
  public static void generator(String fileName, Map<String, Object> map) throws IOException, TemplateException {
    FileWriter fw = new FileWriter(fileName);
    BufferedWriter bw = new BufferedWriter(fw);
    temp.process(map, bw);
    bw.flush();
    fw.close();
  }
}
