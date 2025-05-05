package com.zephyr.train.generator.server;

import com.zephyr.train.generator.util.DbUtil;
import com.zephyr.train.generator.util.Field;
import com.zephyr.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ServerGenerator {

  // windows
  // static String toPath = "generator\\src\\main\\java\\com\\zephyr\\train\\generator\\test\\";
  // mac
  static String serverPath = "[module]/src/main/java/com/zephyr/train/[module]/";
  static String pomPath = "generator/pom.xml";
  static {
    new File(serverPath).mkdirs();
  }

  public static void main(String[] args) throws Exception {
    // Get mybatis-generator
    String generatorPath = getGeneratorPath();

    // For example, generator-config-member.xml，extract module = member
    String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
    System.out.println("module: " + module);
    serverPath = serverPath.replace("[module]", module);
    // new File(servicePath).mkdirs();
    System.out.println("servicePath: " + serverPath);

    // Read table Node
    Document document = new SAXReader().read("generator/" + generatorPath);
    Node table = document.selectSingleNode("//table");
    System.out.println(table);
    Node tableName = table.selectSingleNode("@tableName");
    Node domainObjectName = table.selectSingleNode("@domainObjectName");
    System.out.println(tableName.getText() + "/" + domainObjectName.getText());

    // Set data source for DbUtil
    Node connectionURL = document.selectSingleNode("//@connectionURL");
    Node userId = document.selectSingleNode("//@userId");
    Node password = document.selectSingleNode("//@password");
    System.out.println("url: " + connectionURL.getText());
    System.out.println("user: " + userId.getText());
    System.out.println("password: " + password.getText());
    DbUtil.url = connectionURL.getText();
    DbUtil.user = userId.getText();
    DbUtil.password = password.getText();

    // Example: table name: zephyr_test
    // Domain = ZephyrTest
    String Domain = domainObjectName.getText();
    // domain = zephyrTest
    String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
    // do_main = zephyr-test
    String do_main = tableName.getText().replaceAll("_", "-");
    // table name
    String tableNameEn = DbUtil.getTableComment(tableName.getText());
    List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());

    System.out.println(tableNameEn);
    System.out.println(fieldList);

    // Assemble parameters
    Map<String, Object> param = new HashMap<>();
    param.put("Domain", Domain);
    param.put("domain", domain);
    param.put("do_main", do_main);
    System.out.println("Assemble parameters: " + param);

    generateOnTemplate(Domain, param, "service");
    generateOnTemplate(Domain, param, "controller");
  }

  private static void generateOnTemplate(String Domain, Map<String, Object> param, String target)
      throws IOException, TemplateException {
    FreemarkerUtil.initConfig(target + ".ftl");
    String toPath = serverPath + target + "/";
    new File(toPath).mkdirs();
    String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
    String fileName = toPath + Domain + Target + ".java";
    System.out.println("Start to generate: " + fileName);
    FreemarkerUtil.generator(fileName, param);
  }

  private static String getGeneratorPath() throws DocumentException {
    SAXReader saxReader = new SAXReader();
    Map<String, String> map = new HashMap<String, String>();
    map.put("pom", "http://maven.apache.org/POM/4.0.0");
    saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
    Document document = saxReader.read(pomPath);
    Node node = document.selectSingleNode("//pom:configurationFile");
    System.out.println(node.getText());
    return node.getText();
  }
}
