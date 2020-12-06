package com.eoi.collector.common;

public class MsgCode {
    /**
     * 处理成功返回码
     */
    public static final String success = "0000";

    /**
     * 文件乱码无法预览
     */
    public static final String charsetError = "0601";

    /**
     * 有重复记录返回码,{*}记录重复
     */
    public static final String notUnique = "0010";

    /**
     * 不存在记录的返回码,{*}记录不存在
     */
    public static final String notExist = "0020";

    /**
     * 已存在
     */
    public static final String alreadyExist = "0021";

    /**
     * 不允许为空的返回码
     */
    public static final String notNull = "0030";

    /**
     * 类型不匹配的返回码或者数据输入不一致
     */
    public static final String notMatch = "0040";

    /**
     * 超过最大长度的返回码
     */
    public static final String overLength = "0050";

    /**
     * 超时
     */
    public static final String timeOut = "0060";


    /**
     * 数据库插入错误情况返回码  01**
     */
    public static final String addError = "0100";

    /**
     * 数据库删除错误情况返回码  02**
     */
    public static final String delError = "0200";
    public static final String delInUsed = "0210";

    /**
     * 数据库修改错误情况返回码  03**
     */
    public static final String updateError = "0300";

    /**
     * 需要确认
     */
    public static final String needConfirm = "0320";

    /**
     * 连接测试失败也用这个
     */
    public static final String queryError = "0400";

    /**
     * 参数错误
     */
    public static final String paramsWarn = "0500";

    /**
     * 其他
     */
    public static final String other = "0900";

    /**
     * 非法访问
     */
    public static final String unAuthorized = "5000";

    /**
     * 没有权限，权限不够
     */
    public static final String noRights = "6000";

    /**
     * 验证失败
     */
    public static final String verifyError = "7000";

    /**
     * License验证失败
     */
    public static final String licenseError = "7070";

    /**
     * 上传文件失败
     */
    public static final String uploadFileError = "10000";
}
