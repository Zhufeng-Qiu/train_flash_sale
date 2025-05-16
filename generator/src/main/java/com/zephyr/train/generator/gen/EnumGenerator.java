package com.zephyr.train.generator.gen;


import cn.hutool.core.util.StrUtil;
import com.zephyr.train.business.enums.SeatColEnum;
import com.zephyr.train.business.enums.SeatTypeEnum;
import com.zephyr.train.business.enums.TrainTypeEnum;
import com.zephyr.train.member.enums.PassengerTypeEnum;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EnumGenerator {
//  static String path = "admin/src/assets/js/enums.js";
  static String path = "web/src/assets/js/enums.js";

  public static void main(String[] args) {
    StringBuffer bufferObject = new StringBuffer();
    StringBuffer bufferArray = new StringBuffer();
    long begin = System.currentTimeMillis();
    try {
      // add new enum
      toJson(PassengerTypeEnum.class, bufferObject, bufferArray);
      toJson(TrainTypeEnum.class, bufferObject, bufferArray);
      toJson(SeatTypeEnum.class, bufferObject, bufferArray);
      toJson(SeatColEnum.class, bufferObject, bufferArray);

      StringBuffer buffer = bufferObject.append("\n").append(bufferArray);
      writeJs(buffer);
    } catch (Exception e) {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("Execution duration: " + (end - begin) + " ms");
  }

  private static void toJson(Class clazz, StringBuffer bufferObject, StringBuffer bufferArray) throws Exception {
    // enumConst：transfer YesNoEnum to YES_NO
    String enumConst = StrUtil.toUnderlineCase(clazz.getSimpleName())
        .toUpperCase().replace("_ENUM", "");
    Object[] objects = clazz.getEnumConstants();
    Method name = clazz.getMethod("name");
    // Exclude enum fields and $VALUES, only get code, desc, etc.
    List<Field> targetFields = new ArrayList<>();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (!Modifier.isPrivate(field.getModifiers()) || "$VALUES".equals(field.getName())) {
        continue;
      }
      targetFields.add(field);
    }

    // Generate object
    bufferObject.append(enumConst).append("={");
    for (int i = 0; i < objects.length; i++) {
      Object obj = objects[i];
      bufferObject.append(name.invoke(obj)).append(":");

      // convert an enum value to JSON object string
      formatJsonObj(bufferObject, targetFields, clazz, obj);
      if (i < objects.length - 1) {
        bufferObject.append(",");
      }
    }
    bufferObject.append("};\n");

    // Generate array
    bufferArray.append(enumConst).append("_ARRAY=[");
    for (int i = 0; i < objects.length; i++) {
      Object obj = objects[i];
      // convert an enum value to JSON object string
      formatJsonObj(bufferArray, targetFields, clazz, obj);
      if (i < objects.length - 1) {
        bufferArray.append(",");
      }
    }
    bufferArray.append("];\n");
  }

  /**
   * Convert an enum value to JSON object string
   * For example：SeatColEnum.YDZ_A("A", "A", "1")
   * convert to：{code:"A",desc:"A",type:"1"}
   */
  private static void formatJsonObj(StringBuffer bufferObject, List<Field> targetFields, Class clazz, Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    bufferObject.append("{");
    for (int j = 0; j < targetFields.size(); j++) {
      Field field = targetFields.get(j);
      String fieldName = field.getName();
      String getMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      bufferObject.append(fieldName).append(":\"").append(clazz.getMethod(getMethod).invoke(obj)).append("\"");
      if (j < targetFields.size() - 1) {
        bufferObject.append(",");
      }
    }
    bufferObject.append("}");
  }

  /**
   * Write file
   * @param stringBuffer
   */
  public static void writeJs(StringBuffer stringBuffer) {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(path);
      OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
      System.out.println(path);
      osw.write(stringBuffer.toString());
      osw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

}
