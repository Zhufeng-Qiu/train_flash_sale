package com.zephyr.train.generator.util;

public class Field {
  private String name; // field name: course_id
  private String nameHump; // field name in camelCase: courseId
  private String nameBigHump; // field name in PascalCase: CourseId
  private String nameEn; // name: course
  private String type; // field type: char(8)
  private String javaType; // java type: String
  private String comment; // comment: course|ID
  private Boolean nullAble; // nullable or not
  private Integer length; // string length
  private Boolean enums; // whether it is enum
  private String enumsConst; // enum const: COURSE_LEVEL

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameHump() {
    return nameHump;
  }

  public void setNameHump(String nameHump) {
    this.nameHump = nameHump;
  }

  public String getNameBigHump() {
    return nameBigHump;
  }

  public void setNameBigHump(String nameBigHump) {
    this.nameBigHump = nameBigHump;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getJavaType() {
    return javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public Boolean getNullAble() {
    return nullAble;
  }

  public void setNullAble(Boolean nullAble) {
    this.nullAble = nullAble;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Boolean getEnums() {
    return enums;
  }

  public void setEnums(Boolean enums) {
    this.enums = enums;
  }

  public String getEnumsConst() {
    return enumsConst;
  }

  public void setEnumsConst(String enumsConst) {
    this.enumsConst = enumsConst;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Field{");
    sb.append("name='").append(name).append('\'');
    sb.append(", nameHump='").append(nameHump).append('\'');
    sb.append(", nameBigHump='").append(nameBigHump).append('\'');
    sb.append(", nameEn='").append(nameEn).append('\'');
    sb.append(", type='").append(type).append('\'');
    sb.append(", javaType='").append(javaType).append('\'');
    sb.append(", comment='").append(comment).append('\'');
    sb.append(", nullAble=").append(nullAble);
    sb.append(", length=").append(length);
    sb.append(", enums=").append(enums);
    sb.append(", enumsConst='").append(enumsConst).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
