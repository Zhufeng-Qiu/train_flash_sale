package com.zephyr.train.generator.util;


import cn.hutool.core.util.StrUtil;

import cn.hutool.json.JSONUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbUtil {

  public static String url = "";
  public static String user = "";
  public static String password = "";

  public static Connection getConnection() {
    Connection conn = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      String url = DbUtil.url;
      String user = DbUtil.user;
      String password = DbUtil.password;
      conn = DriverManager.getConnection(url, user, password);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return conn;
  }

  /**
   * Get table comment
   * @param tableName
   * @return
   * @throws Exception
   */
  public static String getTableComment(String tableName) throws Exception {
    Connection conn = getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select table_comment from information_schema.tables Where table_name = '" + tableName + "'");
    String tableNameCH = "";
    if (rs != null) {
      while(rs.next()) {
        tableNameCH = rs.getString("table_comment");
        break;
      }
    }
    rs.close();
    stmt.close();
    conn.close();
    System.out.println("Table name: " + tableNameCH);
    return tableNameCH;
  }

  /**
   * Get all column info
   * @param tableName
   * @return
   * @throws Exception
   */
  public static List<Field> getColumnByTableName(String tableName) throws Exception {
    List<Field> fieldList = new ArrayList<>();
    Connection conn = getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("show full columns from `" + tableName + "`");
    if (rs != null) {
      while(rs.next()) {
        String columnName = rs.getString("Field");
        String type = rs.getString("Type");
        String comment = rs.getString("Comment");
        String nullAble = rs.getString("Null"); //YES NO
        Field field = new Field();
        field.setName(columnName);
        field.setNameHump(lineToHump(columnName));
        field.setNameBigHump(lineToBigHump(columnName));
        field.setType(type);
        field.setJavaType(DbUtil.sqlTypeToJavaType(rs.getString("Type")));
        field.setComment(comment);
        if (comment.contains("|")) {
          field.setNameEn(titleCase(comment.substring(0, comment.indexOf("|"))));
        } else {
          field.setNameEn(titleCase(comment));
        }
        field.setNullAble("YES".equals(nullAble));
        if (type.toUpperCase().contains("varchar".toUpperCase())) {
          String lengthStr = type.substring(type.indexOf("(") + 1, type.length() - 1);
          field.setLength(Integer.valueOf(lengthStr));
        } else {
          field.setLength(0);
        }
        if (comment.contains("Enum")) {
          field.setEnums(true);

          // Take course level as example: get Enum[CourseLevelEnum] from comment, then get enumsConst = COURSE_LEVEL
          int start = comment.indexOf("[");
          int end = comment.indexOf("]");
          String enumsName = comment.substring(start + 1, end); // CourseLevelEnum
          String enumsConst = StrUtil.toUnderlineCase(enumsName)
              .toUpperCase().replace("_ENUM", "");
          field.setEnumsConst(enumsConst);
        } else {
          field.setEnums(false);
        }
        fieldList.add(field);
      }
    }
    rs.close();
    stmt.close();
    conn.close();
    System.out.println("Column info: " + JSONUtil.toJsonPrettyStr(fieldList));
    return fieldList;
  }

  /**
   * lowercase to title case：member id to Member Id
   */
  public static String titleCase(String str){
    String[] words = str.toLowerCase().split("\\s+");
    StringBuilder titleCase = new StringBuilder();

    for (String word : words) {
      if (word.length() > 0) {
        titleCase.append(Character.toUpperCase(word.charAt(0)))
            .append(word.substring(1))
            .append(" ");
      }
    }

    return titleCase.toString().trim();
  }

  /**
   * underline to camel：member_id to memberId
   */
  public static String lineToHump(String str){
    Pattern linePattern = Pattern.compile("_(\\w)");
    str = str.toLowerCase();
    Matcher matcher = linePattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while(matcher.find()){
      matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  /**
   * underline to Pascal：member_id to MemberId
   */
  public static String lineToBigHump(String str){
    String s = lineToHump(str);
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  /**
   * database type to Java type
   */
  public static String sqlTypeToJavaType(String sqlType) {
    if (sqlType.toUpperCase().contains("varchar".toUpperCase())
        || sqlType.toUpperCase().contains("char".toUpperCase())
        || sqlType.toUpperCase().contains("text".toUpperCase())) {
      return "String";
    } else if (sqlType.toUpperCase().contains("datetime".toUpperCase())) {
      return "Date";
    } else if (sqlType.toUpperCase().contains("bigint".toUpperCase())) {
      return "Long";
    } else if (sqlType.toUpperCase().contains("int".toUpperCase())) {
      return "Integer";
    } else if (sqlType.toUpperCase().contains("long".toUpperCase())) {
      return "Long";
    } else if (sqlType.toUpperCase().contains("decimal".toUpperCase())) {
      return "BigDecimal";
    } else if (sqlType.toUpperCase().contains("boolean".toUpperCase())) {
      return "Boolean";
    } else {
      return "String";
    }
  }
}
