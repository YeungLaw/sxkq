package com.yeung.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yeung Law~
 */

@Data
@TableName("seckill_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户 ID,手机号码
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String nickname;
    /**
     * MD5(MD5(pass 明文+固定 salt)+salt)
     */
    private String password;

    private String slat;

    /**
     * 头像
     */
    private String head;

    /**
     * 注册时间
     */
    private Date registerDate;

    private Date lastLoginDate;

    /**
     * 登录次数
     */
    private Integer loginCount;

}
