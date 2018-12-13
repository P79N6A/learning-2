package wang.xiaoluobo.shirosecurity.enums;

/**
 * 用户类型
 */
public enum UserTypeEnum {

    WEB(1, "web端"),
    APP(2, "APP端"),
    ADMIN(3, "admin后台");

    private Integer value;

    private String desc;

    UserTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return this.value;
    }

    public String desc() {
        return this.desc;
    }
}
